package com.dessalines.thumbkey.utils

import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.ThumbkeyApplication
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.DEFAULT_WORD_COMBINATIONS_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_WORD_SUGGESTIONS_ENABLED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val SUGGESTION_LIMIT = 50

fun learnableWord(
    word: String?,
    minimumLength: Int = 2,
): String? {
    val cleaned = word?.trimEnd('.', ',', '!', '?', ';', ':', '"', '\'', ')') ?: return null
    return cleaned.takeIf { value ->
        value.length >= minimumLength && value.any(Char::isLetter) && !value.all(Char::isDigit)
    }
}

data class SuggestionContext(
    val prefix: String,
    val previousKey: String?,
    val lastWordKey: String?,
)

val EMPTY_SUGGESTION_CONTEXT = SuggestionContext(prefix = "", previousKey = null, lastWordKey = null)

fun currentPartialWord(textBeforeCursor: String): String =
    if (textBeforeCursor.isEmpty() || textBeforeCursor.last().isWhitespace()) {
        ""
    } else {
        lastWord(textBeforeCursor).orEmpty()
    }

fun suggestionContext(textBeforeCursor: String): SuggestionContext {
    if (textBeforeCursor.isEmpty()) return EMPTY_SUGGESTION_CONTEXT

    val prefix = currentPartialWord(textBeforeCursor)
    val textBeforeCurrentWord = if (prefix.isEmpty()) textBeforeCursor else textBeforeCursor.dropLast(prefix.length)
    val sentenceBoundary =
        textBeforeCurrentWord.indexOfLast { it == '\n' || it == '.' || it == '!' || it == '?' }
    val currentSentence = textBeforeCurrentWord.substring(sentenceBoundary + 1)
    val previousText =
        if (prefix.isEmpty() && ',' in currentSentence) {
            currentSentence.substringBeforeLast(',')
        } else {
            currentSentence
        }
    val previousKey = learnableWord(lastWord(previousText.trimEnd()), minimumLength = 1)?.lowercase()
    val lastWordKey =
        if (prefix.isEmpty()) {
            learnableWord(lastWord(textBeforeCursor.trimEnd()), minimumLength = 1)?.lowercase()
        } else {
            null
        }

    return SuggestionContext(prefix = prefix, previousKey = previousKey, lastWordKey = lastWordKey)
}

fun escapeLike(prefix: String): String = prefix.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

fun fuzzyRank(
    query: String,
    word: String,
): Int? {
    val normalizedQuery = stripCombiningMarks(query.trim()).lowercase()
    if (normalizedQuery.isEmpty()) return 0

    val normalizedWord = stripCombiningMarks(word).lowercase()
    if (normalizedWord.startsWith(normalizedQuery)) return 0
    if (normalizedWord.contains(normalizedQuery)) return 1

    var queryIndex = 0
    for (character in normalizedWord) {
        if (character == normalizedQuery[queryIndex]) queryIndex++
        if (queryIndex == normalizedQuery.length) return 2
    }
    return null
}

fun learnTypedWord(
    ime: IMEService,
    textBeforeCursor: String,
) {
    val app = ime.application as ThumbkeyApplication
    val settings = app.appSettingsRepository.appSettings.value
    if ((settings?.wordSuggestionsEnabled ?: DEFAULT_WORD_SUGGESTIONS_ENABLED) == 0 ||
        isUriOrEmailOrPasswordField(ime) ||
        ime.isInputRedirected
    ) {
        return
    }

    val word = learnableWord(AbbreviationManager.getInstance(ime).typedWordBeforeCursor(textBeforeCursor)) ?: return
    val previousKey =
        if ((settings?.wordCombinationsEnabled ?: DEFAULT_WORD_COMBINATIONS_ENABLED) == 0) {
            null
        } else {
            suggestionContext(textBeforeCursor).previousKey
        }
    CoroutineScope(Dispatchers.IO).launch {
        val now = System.currentTimeMillis()
        val db = AppDB.getDatabase(ime)
        db.wordUsageDao().recordUse(word, word.lowercase(), now)
        if (previousKey != null) {
            db.wordPairDao().recordUse(previousKey, word.lowercase(), now)
        }
    }
}
