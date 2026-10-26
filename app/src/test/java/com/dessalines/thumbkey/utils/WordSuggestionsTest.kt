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
    fun extractsSuggestionContext() {
        assertEquals(SuggestionContext("", "i"), suggestionContext("I "))
        assertEquals(SuggestionContext("gr", "am"), suggestionContext("I am gr"))
        assertEquals(SuggestionContext("", "hello"), suggestionContext("Hello, how "))
        assertEquals(SuggestionContext("", "next"), suggestionContext("Done. Next "))
        assertEquals(SuggestionContext("", "next"), suggestionContext("Done! Next "))
        assertEquals(SuggestionContext("", null), suggestionContext("Done. "))
        assertEquals(SuggestionContext("", null), suggestionContext("Done? "))
        assertEquals(SuggestionContext("", null), suggestionContext("line\n"))
        assertEquals(SuggestionContext("ab", null), suggestionContext("line\nab"))
        assertEquals(SuggestionContext("", null), suggestionContext(""))
        assertEquals(SuggestionContext("am", null), suggestionContext("am"))
        assertEquals(SuggestionContext("", "am"), suggestionContext("I  am "))
        assertEquals(SuggestionContext("", "word"), suggestionContext("word, "))
        assertEquals(SuggestionContext("", "école"), suggestionContext("ÉCOLE "))
        assertEquals(SuggestionContext("", null), suggestionContext("123 "))
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

    @Test
    fun resolvesTypedWordBeforeEnterAndAfterPunctuation() {
        assertEquals("Morgen", learnableWord(typedLastWord("Hallo Morgen", "Hallo\nMorgen")))
        assertEquals("Morgen", learnableWord(typedLastWord("Morgen!", "Morgen!")))
    }
}
