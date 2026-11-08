package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.SUGGESTION_SLOTS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SuggestionRankingTest {
    @Test
    fun ranksByUsesTimesLettersLeftAndHidesSmallSavings() {
        val frequentShort = word(id = 1, key = "hello", uses = 4)
        val lessFrequentLong = word(id = 2, key = "helicopter", uses = 2)
        val savesOneLetter = word(id = 3, key = "help", uses = 100)

        assertEquals(
            listOf(lessFrequentLong, frequentShort),
            rankSuggestions(
                prefix = "hel",
                prefixWords = listOf(frequentShort, lessFrequentLong, savesOneLetter),
                pairWords = emptyList(),
                lastWordKey = null,
            ).map(Suggestion::first),
        )
    }

    @Test
    fun keepsNextWordSuggestionsAheadOfHigherScoringPrefixMatches() {
        val pairWord = word(id = 1, key = "hello", uses = 2)
        val prefixWord = word(id = 2, key = "helicopter", uses = 100)

        assertEquals(
            listOf(pairWord, prefixWord),
            rankSuggestions(
                prefix = "hel",
                prefixWords = listOf(prefixWord),
                pairWords = listOf(pairWord),
                lastWordKey = null,
            ).map(Suggestion::first),
        )
    }

    @Test
    fun excludesTheLastTypedWord() {
        val lastWord = word(id = 1, key = "hello")
        val otherWord = word(id = 2, key = "helpful")

        assertEquals(
            listOf(otherWord),
            rankSuggestions(
                prefix = "hel",
                prefixWords = listOf(lastWord, otherWord),
                pairWords = emptyList(),
                lastWordKey = "hello",
            ).map(Suggestion::first),
        )
    }

    @Test
    fun keepsPairCopyWhenTheSameWordIsAlsoAPrefixMatch() {
        val pairCopy = word(id = 1, key = "hello", label = "pair")
        val prefixCopy = word(id = 1, key = "hello", label = "prefix")

        assertEquals(
            listOf(pairCopy),
            rankSuggestions(
                prefix = "hel",
                prefixWords = listOf(prefixCopy),
                pairWords = listOf(pairCopy),
                lastWordKey = null,
            ).map(Suggestion::first),
        )
    }

    @Test
    fun limitsResultsToSuggestionSlots() {
        val words = (1..SUGGESTION_SLOTS + 2).map { word(id = it, key = "word$it") }

        assertEquals(
            SUGGESTION_SLOTS,
            rankSuggestions(
                prefix = "wo",
                prefixWords = words,
                pairWords = emptyList(),
                lastWordKey = null,
            ).size,
        )
    }

    @Test
    fun emptyPrefixShowsOnlyNextWordGuesses() {
        val pairWord = word(id = 1, key = "hello")
        val unrelatedWord = word(id = 2, key = "helicopter")

        assertEquals(
            listOf(pairWord),
            rankSuggestions(
                prefix = "",
                prefixWords = listOf(unrelatedWord),
                pairWords = listOf(pairWord),
                lastWordKey = null,
            ).map(Suggestion::first),
        )
    }

    @Test
    fun buildsPhysicalSlotsAndAddsThePhraseToTheRightSuggestion() {
        val best = word(id = 1, key = "hello")
        val left = word(id = 2, key = "helpful")
        val right = word(id = 3, key = "helicopter")
        val next = word(id = 4, key = "there")

        val slots =
            buildSuggestions(
                ranked = listOf(Suggestion(best), Suggestion(left), Suggestion(right)),
                phraseNext = next,
            )

        assertEquals(listOf(Suggestion(left), Suggestion(best), Suggestion(best, next)), slots)
        assertEquals("Hello there", slots[2]?.text("He"))
        assertEquals(Suggestion(best, next), buildSuggestions(listOf(Suggestion(best)), phraseNext = next)[2])
    }

    private fun word(
        id: Int,
        key: String,
        uses: Int = 2,
        label: String = key,
    ) = WordUsage(id = id, word = label, wordKey = key, useCount = uses, lastUsed = id.toLong())
}
