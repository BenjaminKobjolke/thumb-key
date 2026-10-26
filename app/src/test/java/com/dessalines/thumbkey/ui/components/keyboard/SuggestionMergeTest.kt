package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.utils.SUGGESTION_LIMIT
import org.junit.Assert.assertEquals
import org.junit.Test

class SuggestionMergeTest {
    @Test
    fun keepsPairSuggestionsFirstAndFiltersLastWordOnlyFromRecent() {
        val lastWord = word(id = 1, key = "hallo")
        val pairWord = word(id = 2, key = "hallo")
        val recentWord = word(id = 3, key = "morgen")

        assertEquals(
            listOf(lastWord, pairWord, recentWord),
            mergeSuggestions(
                pairs = listOf(lastWord, pairWord),
                recent = listOf(lastWord, recentWord),
                lastWordKey = "hallo",
            ),
        )
    }

    @Test
    fun removesDuplicateIdsAndKeepsPairCopyFirst() {
        val pairWord = word(id = 1, key = "morgen", label = "pair")
        val recentDuplicate = word(id = 1, key = "morgen", label = "recent")
        val recentWord = word(id = 2, key = "abend")

        assertEquals(
            listOf(pairWord, recentWord),
            mergeSuggestions(listOf(pairWord), listOf(recentDuplicate, recentWord), lastWordKey = null),
        )
    }

    @Test
    fun nullLastWordKeyDoesNotFilterRecentWords() {
        val recent = listOf(word(id = 1, key = "hallo"))

        assertEquals(recent, mergeSuggestions(emptyList(), recent, lastWordKey = null))
    }

    @Test
    fun capsResultAtSuggestionLimit() {
        val words = (1..SUGGESTION_LIMIT + 1).map { word(id = it, key = "word$it") }

        assertEquals(SUGGESTION_LIMIT, mergeSuggestions(emptyList(), words, lastWordKey = null).size)
    }

    private fun word(
        id: Int,
        key: String,
        label: String = key,
    ) = WordUsage(id = id, word = label, wordKey = key, useCount = 1, lastUsed = id.toLong())
}
