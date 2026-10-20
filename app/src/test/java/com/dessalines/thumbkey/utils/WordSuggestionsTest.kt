package com.dessalines.thumbkey.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class WordSuggestionsTest {
    @Test
    fun acceptsOnlyLearnableWords() {
        assertEquals("Hallo", learnableWord("Hallo,"))
        assertEquals(null, learnableWord("a"))
        assertEquals(null, learnableWord("123"))
        assertEquals(null, learnableWord(null))
        assertEquals(null, learnableWord("!?"))
    }

    @Test
    fun extractsCurrentPartialWord() {
        assertEquals("Mo", currentPartialWord("Guten Mo"))
        assertEquals("", currentPartialWord("Guten "))
        assertEquals("", currentPartialWord(""))
        assertEquals("ab", currentPartialWord("line\nab"))
    }

    @Test
    fun escapesLikeWildcards() {
        assertEquals("a\\_b\\%", escapeLike("a_b%"))
    }

    @Test
    fun ranksFuzzyMatches() {
        assertEquals(0, fuzzyRank("mor", "Morgen"))
        assertEquals(1, fuzzyRank("rge", "Morgen"))
        assertEquals(2, fuzzyRank("mrgn", "Morgen"))
        assertEquals(null, fuzzyRank("ngm", "Morgen"))
        assertEquals(0, fuzzyRank("", "Morgen"))
        assertEquals(0, fuzzyRank("cafe", "Café"))
        assertEquals(0, fuzzyRank(" MO ", "morgen"))
    }

    @Test
    fun learnsOnlyConsecutivelyTypedWords() {
        assertEquals("hallo", typedLastWord("hallo", "x hallo"))
        assertEquals(null, typedLastWord("", "x pasted"))
    }
}
