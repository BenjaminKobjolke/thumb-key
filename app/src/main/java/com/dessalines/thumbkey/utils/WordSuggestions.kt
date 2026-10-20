package com.dessalines.thumbkey.utils

import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.ThumbkeyApplication
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.DEFAULT_WORD_SUGGESTIONS_ENABLED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val SUGGESTION_LIMIT = 50

fun learnableWord(word: String?): String? {
    val cleaned = word?.trimEnd('.', ',', '!', '?', ';', ':', '"', '\'', ')') ?: return null
    return cleaned.takeIf { value ->
        value.length >= 2 && value.any(Char::isLetter) && !value.all(Char::isDigit)
    }
}

fun currentPartialWord(textBeforeCursor: String): String =
    if (textBeforeCursor.isEmpty() || textBeforeCursor.last().isWhitespace()) {
        ""
    } else {
        lastWord(textBeforeCursor).orEmpty()
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
    CoroutineScope(Dispatchers.IO).launch {
        AppDB.getDatabase(ime).wordUsageDao().recordUse(word, word.lowercase(), System.currentTimeMillis())
    }
}
