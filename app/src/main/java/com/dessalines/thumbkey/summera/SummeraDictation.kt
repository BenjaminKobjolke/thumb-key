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

            DictationState.Idle, is DictationState.Failed -> {
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

    fun stopAndSend(ime: IMEService) {
        val credentials = SummeraAccount.credentials(ime)
        if (credentials == null) {
            releaseRecorder()
            state = DictationState.Failed(ime.getString(R.string.summera_sign_in_first))
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
        state = DictationState.Uploading

        val file = recordingFile(ime)
        job =
            scope.launch {
                try {
                    val client = SummeraAccount.client()
                    val id =
                        withContext(Dispatchers.IO) {
                            client.services.transcribe(credentials, file, Locale.getDefault().language)
                        }
                    state = DictationState.Polling()
                    // The log lines never carry the dictated text
                    SummeraAccount.logSignIn(ime, "dictation: uploaded, id=$id")

                    val result =
                        poll {
                            val status = withContext(Dispatchers.IO) { client.services.info(credentials, id) }
                            state = DictationState.Polling(status)
                            SummeraAccount.logSignIn(ime, "dictation: status=${status.status} message=${status.message}")
                            status.takeIf { it.isFinished }
                        }
                    val text = result?.text
                    state =
                        when {
                            result == null -> {
                                SummeraAccount.logSignIn(ime, "dictation: timeout")
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
                                val connection = ime.currentInputConnection
                                if (ime.isInputViewShown && connection != null) connection.commitText(text, 1)
                                DictationState.Idle
                            }
                        }
                } catch (e: XidaAiException) {
                    SummeraAccount.logSignIn(ime, "dictation: failed ${e.message}")
                    state = DictationState.Failed(e.message.orEmpty())
                }
                // Not in a finally: a cancelled run must not delete the file of the next recording,
                // cancel() already removed its own
                file.delete()
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
            val client = SummeraAccount.client()
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
            SummeraAccount.logSignIn(context, "transcripts: restored ${items.size}")
        } catch (e: XidaAiException) {
            SummeraAccount.logSignIn(context, "transcripts: restore failed ${e.message}")
        }
    }

    /**
     * The keyboard window went away. The mic never stays open behind it, but a running upload or
     * poll goes on: its transcript lands in the transcript history.
     */
    fun onKeyboardHidden(ime: IMEService) {
        when (state) {
            is DictationState.Recording -> {
                stopAndSend(ime)
            }

            DictationState.Uploading, is DictationState.Polling -> {}

            else -> {
                cancel(ime)
            }
        }
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
