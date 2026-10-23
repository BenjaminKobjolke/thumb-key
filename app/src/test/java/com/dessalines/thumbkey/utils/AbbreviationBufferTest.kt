package com.dessalines.thumbkey.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class AbbreviationBufferTest {
    @Test
    fun returnsLastWordWhenItWasTypedConsecutively() {
        assertEquals("gm", typedLastWord("hello gm", "hello gm"))
    }

    @Test
    fun rejectsWordDeletedWithoutBufferTracking() {
        assertEquals(null, typedLastWord("foo gm", "foo "))
    }

    @Test
    fun findsWordAfterUntrackedEnter() {
        assertEquals("gn", typedLastWord("gmgn", "gm\ngn"))
    }

    @Test
    fun rejectsTextWhenCursorMovedIntoAnotherWord() {
        assertEquals(null, typedLastWord("gm", "xgm"))
    }

    @Test
    fun rejectsPastedTextWithoutTrackedTyping() {
        assertEquals(null, typedLastWord("", "gm"))
    }

    @Test
    fun preservesTypedWordCase() {
        assertEquals("Gm", typedLastWord("Gm", "Gm"))
    }

    @Test
    fun expansionWithoutCursorPlaceholderKeepsExpansionText() {
        assertEquals(
            ExpansionResult(true, "good morning", null, 2),
            expansionResult("good morning", 2),
        )
    }

    @Test
    fun expansionWithCursorPlaceholderRemovesPlaceholderAndKeepsOffset() {
        assertEquals(
            ExpansionResult(true, "Dear ,", 5, 2),
            expansionResult("Dear \$0,", 2),
        )
    }
}
