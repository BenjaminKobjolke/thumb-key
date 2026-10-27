package com.dessalines.thumbkey.summera

import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.ThumbkeyApplication
import de.xida.aichatapi.TranscriptionReplacement
import de.xida.aichatapi.TranscriptionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal data class PendingTranscription(
    val id: Int,
    val file: File,
    val prompt: String?,
    val replacements: List<TranscriptionReplacement>,
)

internal suspend fun finishTranscription(
    ime: IMEService,
    result: TranscriptionStatus?,
    task: PendingTranscription,
    commit: (String) -> Unit,
): DictationState {
    val text = result?.text
    return when {
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
            withContext(Dispatchers.IO) { repository.addTranscript(text, task.id) }
            task.file.delete()
            if (task.prompt == null) {
                commit(applyReplacements(text, task.replacements))
                DictationState.Idle
            } else {
                // With a prompt the user picks which text to insert
                DictationState.Done(text, result.promptResult.takeUnless { result.promptFailed })
            }
        }
    }
}
