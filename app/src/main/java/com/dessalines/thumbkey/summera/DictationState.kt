package com.dessalines.thumbkey.summera

import de.xida.aichatapi.TranscriptionReplacement
import de.xida.aichatapi.TranscriptionStatus

const val EXTRA_TRANSCRIPT = "summera_transcript"

/** Replaces whole words only; [TranscriptionReplacement.from] may also be a phrase. */
internal fun applyReplacements(
    text: String,
    replacements: List<TranscriptionReplacement>,
): String =
    replacements.fold(text) { result, replacement ->
        if (replacement.from.isBlank()) {
            result
        } else {
            Regex(
                "(?<![\\p{L}\\p{N}])${Regex.escape(replacement.from)}(?![\\p{L}\\p{N}])",
                RegexOption.IGNORE_CASE,
            ).replace(result, Regex.escapeReplacement(replacement.to))
        }
    }

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

/** The result with [replacements] applied to both texts, for replacements saved after it arrived. */
internal fun DictationState.Done.withReplacements(replacements: List<TranscriptionReplacement>) =
    DictationState.Done(
        transcript = applyReplacements(transcript, replacements),
        answer = answer?.let { applyReplacements(it, replacements) },
    )
