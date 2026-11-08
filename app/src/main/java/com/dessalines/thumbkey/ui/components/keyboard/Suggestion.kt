package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.MIN_LETTERS_SAVED
import com.dessalines.thumbkey.utils.MIN_SUGGEST_USES
import com.dessalines.thumbkey.utils.SUGGESTION_SLOTS
import com.dessalines.thumbkey.utils.matchPrefixCase

/** Ranks learned word candidates and arranges them into the bar's left, center, and right slots. */
data class Suggestion(
    val first: WordUsage,
    val next: WordUsage? = null,
) {
    fun text(prefix: String): String = listOfNotNull(matchPrefixCase(first.word, prefix), next?.word).joinToString(" ")
}

/** Returns the best candidates first; next-word guesses stay ahead of ordinary prefix matches. */
fun rankSuggestions(
    prefix: String,
    prefixWords: List<WordUsage>,
    pairWords: List<WordUsage>,
    lastWordKey: String?,
): List<Suggestion> {
    val normalizedPrefix = prefix.lowercase()
    val normalizedLastWordKey = lastWordKey?.lowercase()

    fun eligible(words: List<WordUsage>): List<WordUsage> =
        words
            .asSequence()
            .filter { it.useCount >= MIN_SUGGEST_USES }
            .filterNot { it.wordKey == normalizedLastWordKey }
            .filter { normalizedPrefix.isEmpty() || it.wordKey.startsWith(normalizedPrefix) }
            .filter { it.wordKey.length - normalizedPrefix.length >= MIN_LETTERS_SAVED }
            .distinctBy(WordUsage::id)
            .sortedWith(
                compareByDescending<WordUsage> {
                    it.useCount * (it.wordKey.length - normalizedPrefix.length)
                }.thenByDescending(WordUsage::lastUsed),
            ).toList()

    val pairCandidates = eligible(pairWords)
    val pairIds = pairCandidates.mapTo(HashSet(), WordUsage::id)
    val prefixCandidates =
        if (normalizedPrefix.isEmpty()) {
            emptyList()
        } else {
            eligible(prefixWords).filterNot { it.id in pairIds }
        }

    return (pairCandidates + prefixCandidates)
        .take(SUGGESTION_SLOTS)
        .map { Suggestion(it) }
}

/** Places the best candidate in the center and uses the optional phrase in the right slot. */
fun buildSuggestions(
    ranked: List<Suggestion>,
    phraseNext: WordUsage?,
): List<Suggestion?> =
    listOf(
        ranked.getOrNull(1),
        ranked.firstOrNull(),
        if (phraseNext == null) {
            ranked.getOrNull(2)
        } else {
            ranked.firstOrNull()?.copy(next = phraseNext)
        },
    )
