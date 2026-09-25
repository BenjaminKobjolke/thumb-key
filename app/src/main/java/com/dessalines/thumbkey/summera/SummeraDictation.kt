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
import com.dessalines.thumbkey.ThumbkeyApplication
import com.dessalines.thumbkey.db.ClipboardItem
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.db.SOURCE_TRANSCRIPT
import com.dessalines.thumbkey.utils.TAG
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.TranscriptionStatus
import de.xida.aichatapi.XidaAiException
import de.xida.aichatapi.createdAtMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

private const val RECORDING_FILE = "summera-dictation.m4a"
private const val SAMPLING_RATE = 44_100

// The server caps services/list at 100 per page
private const val RESTORE_PAGE_SIZE = 100

sealed interface DictationState {
    data object Idle : DictationState

    data class Recording(
        val startedAt: Long,
    ) : DictationState

    data object Uploading : DictationState

    data class Polling(
        /** The last answer of the server, null until the first poll is back. */
        val result: TranscriptionStatus? = null,
    ) : DictationState

    data class Failed(
        val message: String,
    ) : DictationState

    /** Both texts of a dictation with a prompt; [answer] is null when the AI step failed. */
    data class Done(
        val transcript: String,
        val answer: String?,
    ) : DictationState
}

fun hasMicrophonePermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

/**
 * Records a voice note, sends it to Summera AI and types the transcript.
 * The keyboard renders [state] instead of its keys while it is not [DictationState.Idle].
 */
object SummeraDictation {
    var state: DictationState by mutableStateOf(DictationState.Idle)
        private set

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

        val file = recordingFile(ime)
        val prompt = SummeraAccount.activePromptText(ime)
        job =
            scope.launch {
                try {
                    val client = SummeraAccount.client(ime)
                    val id =
                        withContext(Dispatchers.IO) {
                            client.services.transcribe(credentials, file, Locale.getDefault().language, prompt = prompt)
                        }
                    state = DictationState.Polling()

                    val result =
                        poll {
                            val status = withContext(Dispatchers.IO) { client.services.info(credentials, id) }
                            state = DictationState.Polling(status)
                            status.takeIf { it.isFinished }
                        }
                    val text = result?.text
                    state =
                        when {
                            result == null -> {
                                SummeraAccount.log(ime, "dictation: timeout")
                                DictationState.Failed(ime.getString(R.string.summera_timeout))
                            }

                            text == null -> {
                                DictationState.Failed(result.message ?: result.status)
                            }

                            else -> {
                                // Kept whatever happens to the keyboard; typed only into a field that is on screen,
                                // the connection can still point at the old one after a hide
                                val repository = (ime.application as ThumbkeyApplication).clipboardRepository
                                withContext(Dispatchers.IO) { repository.addTranscript(text, id) }
                                file.delete()
                                if (prompt == null) {
                                    commit(ime, text)
                                    DictationState.Idle
                                } else {
                                    // With a prompt the user picks which text to insert
                                    DictationState.Done(text, result.promptResult.takeUnless { result.promptFailed })
                                }
                            }
                        }
                } catch (e: XidaAiException) {
                    SummeraAccount.log(ime, "dictation: failed ${e.message}")
                    state = DictationState.Failed(e.message.orEmpty())
                }
                // A failed run keeps the file for retry; cancel() removes it.
            }
    }

    /**
     * Pulls the transcripts dictated before (another phone, a reinstall) into [repository], once per
     * sign-in. A failure leaves the flag unset, so the next open of the history tries again.
     */
    suspend fun restoreTranscripts(
        context: Context,
        repository: ClipboardRepository,
    ) {
        val credentials = SummeraAccount.credentials(context) ?: return
        if (SummeraAccount.transcriptsRestored(context)) return
        try {
            val client = SummeraAccount.client(context)
            val items = mutableListOf<ClipboardItem>()
            var start = 0
            do {
                val page = withContext(Dispatchers.IO) { client.services.list(credentials, start, RESTORE_PAGE_SIZE) }
                page.items
                    .filter { it.status == TranscriptionStatus.COMPLETE && !it.text.isNullOrBlank() }
                    .mapTo(items) {
                        ClipboardItem(
                            text = it.text.orEmpty(),
                            timestamp = it.createdAtMillis(),
                            source = SOURCE_TRANSCRIPT,
                            remoteId = it.id,
                        )
                    }
                start += page.items.size
            } while (page.items.isNotEmpty() && start < page.total)
            withContext(Dispatchers.IO) { repository.restoreTranscripts(items) }
            SummeraAccount.setTranscriptsRestored(context)
            SummeraAccount.log(context, "transcripts: restored ${items.size}")
        } catch (e: XidaAiException) {
            SummeraAccount.log(context, "transcripts: restore failed ${e.message}")
        }
    }

    /**
     * The keyboard window went away. The mic never stays open behind it, but a running upload or
     * poll goes on: its transcript lands in the transcript history. A failure is also kept, panel
     * and recording, so the user can restore connectivity and retry.
     */
    fun onKeyboardHidden(ime: IMEService) {
        when (state) {
            is DictationState.Recording -> {
                stopAndSend(ime)
            }

            DictationState.Uploading, is DictationState.Polling, is DictationState.Failed -> {}

            else -> {
                cancel(ime)
            }
        }
    }

    /** Types one of the texts of a [DictationState.Done] result and brings the keys back. */
    fun insert(
        ime: IMEService,
        text: String,
    ) {
        commit(ime, text)
        state = DictationState.Idle
    }

    private fun commit(
        ime: IMEService,
        text: String,
    ) {
        val connection = ime.currentInputConnection
        if (ime.isInputViewShown && connection != null) connection.commitText(text, 1)
    }

    fun cancel(context: Context) {
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
