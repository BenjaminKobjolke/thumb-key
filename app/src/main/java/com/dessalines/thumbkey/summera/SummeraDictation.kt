package com.dessalines.thumbkey.summera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.utils.TAG
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.TranscriptionReplacement
import de.xida.aichatapi.XidaAiClient
import de.xida.aichatapi.XidaAiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

private const val RECORDING_FILE = "summera-dictation.m4a"
private const val SAMPLING_RATE = 44_100

// Block body on purpose: the analyzer reads an expression body as running into the object below
@Suppress("ktlint:standard:function-expression-body")
fun hasMicrophonePermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED
}

/**
 * Records a voice note, sends it to Summera AI and types the transcript.
 * The keyboard renders [state] instead of its keys while it is not [DictationState.Idle].
 */
object SummeraDictation {
    var state: DictationState by mutableStateOf(DictationState.Idle)
        private set

    /** Applies replacements just saved in the settings to an open result, so it shows them right away. */
    fun onReplacementsSaved(replacements: List<TranscriptionReplacement>) {
        (state as? DictationState.Done)?.let { state = it.withReplacements(replacements) }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var recorder: MediaRecorder? = null
    private var job: Job? = null

    fun toggle(ime: IMEService) {
        when (state) {
            is DictationState.Recording -> {
                stopAndSend(ime)
            }

            // Done: the unread result is dropped, the spacebar starts a new recording
            DictationState.Idle, is DictationState.Failed, is DictationState.Done -> {
                start(ime)
            }

            // Uploading or polling: wait for it, or cancel from the screen
            else -> {}
        }
    }

    fun start(ime: IMEService) {
        try {
            @Suppress("DEPRECATION")
            val newRecorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(ime) else MediaRecorder()
            recorder = newRecorder
            newRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            newRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            newRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            newRecorder.setAudioSamplingRate(SAMPLING_RATE)
            newRecorder.setOutputFile(recordingFile(ime).absolutePath)
            newRecorder.prepare()
            newRecorder.start()
            state = DictationState.Recording(System.currentTimeMillis())
        } catch (e: Exception) {
            // MediaRecorder throws IOException, IllegalStateException or a bare RuntimeException
            fail(ime, e)
        }
    }

    /** Re-sends the kept recording after a send failure, or records anew after a recorder failure. */
    fun retry(ime: IMEService) {
        if (!recordingFile(ime).exists()) {
            start(ime)
            return
        }
        val credentials = credentialsOrSignIn(ime) ?: return
        send(ime, credentials)
    }

    fun stopAndSend(ime: IMEService) {
        val credentials = credentialsOrSignIn(ime)
        if (credentials == null) {
            releaseRecorder()
            return
        }
        try {
            // Throws when it is stopped before any audio arrived
            recorder?.stop()
        } catch (e: RuntimeException) {
            fail(ime, e)
            return
        }
        releaseRecorder()
        send(ime, credentials)
    }

    /** The stored credentials, or null with the sign-in failure already shown. */
    private fun credentialsOrSignIn(ime: IMEService): Credentials? {
        val credentials = SummeraAccount.credentials(ime)
        if (credentials == null) {
            state = DictationState.Failed(ime.getString(R.string.summera_sign_in_first))
        }
        return credentials
    }

    private fun send(
        ime: IMEService,
        credentials: Credentials,
    ) {
        state = DictationState.Uploading
        SummeraSounds.startWaiting(ime)

        val file = recordingFile(ime)
        val prompt = SummeraAccount.activePromptText(ime)
        job =
            scope.launch {
                runTranscription(ime, credentials, file, prompt)
            }
    }

    private suspend fun runTranscription(
        ime: IMEService,
        credentials: Credentials,
        file: File,
        prompt: String?,
    ) {
        try {
            val client = SummeraAccount.client(ime)
            val id =
                withContext(Dispatchers.IO) {
                    client.services.transcribe(credentials, file, Locale.getDefault().language, prompt = prompt)
                }
            val replacements = loadReplacements(ime, client, credentials, prompt)
            state = DictationState.Polling()
            val result = pollTranscription(client, credentials, id)
            state =
                finishTranscription(
                    ime,
                    result,
                    PendingTranscription(id, file, prompt, replacements),
                ) { text -> commit(ime, text) }
        } catch (e: XidaAiException) {
            SummeraAccount.log(ime, "dictation: failed ${e.message}")
            state = DictationState.Failed(e.message.orEmpty())
        } finally {
            SummeraSounds.stopWaiting()
            // Cancellation means the user left the wait: stop silently.
            if (currentCoroutineContext().isActive) SummeraSounds.playDone(ime)
        }
        // A failed run keeps the file for retry; cancel() removes it.
    }

    private suspend fun loadReplacements(
        ime: IMEService,
        client: XidaAiClient,
        credentials: Credentials,
        prompt: String?,
    ) = if (prompt == null) {
        try {
            withContext(Dispatchers.IO) { client.settings.getTranscriptionReplacements(credentials) }
        } catch (e: XidaAiException) {
            SummeraAccount.log(ime, "dictation: replacements failed ${e.message}")
            emptyList()
        }
    } else {
        emptyList()
    }

    private suspend fun pollTranscription(
        client: XidaAiClient,
        credentials: Credentials,
        id: Int,
    ) = poll {
        val status = withContext(Dispatchers.IO) { client.services.info(credentials, id) }
        state = DictationState.Polling(status)
        status.takeIf { it.isFinished }
    }

    /**
     * Pulls the transcripts dictated before (another phone, a reinstall) into [repository], once per
     * sign-in. A failure leaves the flag unset, so the next open of the history tries again.
     */
    suspend fun restoreTranscripts(
        context: Context,
        repository: ClipboardRepository,
    ) = restoreTranscriptHistory(context, repository)

    /**
     * The keyboard window went away. The mic never stays open behind it, but a running upload or
     * poll goes on: its transcript lands in the transcript history. A failure is also kept, panel
     * and recording, so the user can restore connectivity and retry. A finished result stays so
     * the user can return from its replacements screen and still insert it.
     */
    fun onKeyboardHidden(ime: IMEService) {
        when (state) {
            is DictationState.Recording -> {
                stopAndSend(ime)
            }

            DictationState.Uploading,
            is DictationState.Polling,
            is DictationState.Failed,
            is DictationState.Done,
            -> {}

            else -> {
                cancel(ime)
            }
        }
    }

    fun openReplacements(
        ime: IMEService,
        transcript: String,
    ) = openDictationReplacements(ime, transcript)

    /** Types one of the texts of a [DictationState.Done] result and brings the keys back. */
    fun insert(
        ime: IMEService,
        text: String,
    ) {
        commit(ime, text)
        state = DictationState.Idle
    }

    /** Closes a [DictationState.Done] result: back to the keys in a text field, else hides the keyboard. */
    fun dismiss(ime: IMEService) = dismissDictation(ime)

    private fun commit(
        ime: IMEService,
        text: String,
    ) {
        val connection = ime.currentInputConnection
        if (ime.isInputViewShown && connection != null) connection.commitText(text, 1)
    }

    fun cancel(context: Context) {
        SummeraSounds.stopWaiting()
        job?.cancel()
        job = null
        releaseRecorder()
        recordingFile(context).delete()
        state = DictationState.Idle
    }

    private fun fail(
        context: Context,
        e: Exception,
    ) {
        Log.e(TAG, "Summera dictation failed", e)
        releaseRecorder()
        recordingFile(context).delete()
        state = DictationState.Failed(e.message ?: e.javaClass.simpleName)
    }

    private fun releaseRecorder() {
        recorder?.release()
        recorder = null
    }

    private fun recordingFile(context: Context): File = File(context.cacheDir, RECORDING_FILE)
}
