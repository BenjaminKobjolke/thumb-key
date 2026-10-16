package com.dessalines.thumbkey.summera

import de.xida.aichatapi.TranscriptionReplacement
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplyReplacementsTest {
    @Test
    fun replacesWholeWordsIgnoringCase() {
        val replacements = listOf(TranscriptionReplacement("Xeeda", "Xida"))

        assertEquals("Xida ist meine Firma", applyReplacements("Xeeda ist meine Firma", replacements))
        assertEquals("Xida.", applyReplacements("xeeda.", replacements))
        assertEquals("Xeedas", applyReplacements("Xeedas", replacements))
    }

    @Test
    fun replacesPhrasesAndSpecialCharactersLiterally() {
        val replacements = listOf(TranscriptionReplacement("C++", "$1 Kotlin"))

        assertEquals("$1 Kotlin ist gut", applyReplacements("C++ ist gut", replacements))
    }

    @Test
    fun emptyListLeavesTextUnchanged() {
        assertEquals("Xeeda", applyReplacements("Xeeda", emptyList()))
    }

    @Test
    fun appliesSavedReplacementsToBothDoneTexts() {
        val replacements = listOf(TranscriptionReplacement("okay", "OK"))

        assertEquals(
            DictationState.Done("das ist OK", "Ja, das wäre OK."),
            DictationState.Done("das ist okay", "Ja, das wäre okay.").withReplacements(replacements),
        )
        assertEquals(
            DictationState.Done("OK", null),
            DictationState.Done("okay", null).withReplacements(replacements),
        )
    }
}
