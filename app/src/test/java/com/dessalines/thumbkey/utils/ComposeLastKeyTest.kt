package com.dessalines.thumbkey.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ComposeLastKeyTest {
    @Test
    fun mapsRepresentativeValuesForEveryModifier() {
        val examples =
            mapOf(
                "\"" to ("a" to "ä"),
                "'" to ("e" to "é"),
                "`" to ("a" to "à"),
                "^" to ("c" to "ĉ"),
                "~" to ("n" to "ñ"),
                "°" to ("a" to "å"),
                "˘" to ("g" to "ğ"),
                "!" to ("a" to "æ"),
                "\$" to ("e" to "€"),
                "゛" to ("か" to "が"),
                "?" to ("a" to "ả"),
                "*" to ("a" to "ạ"),
                "ˇ" to ("c" to "č"),
                "-" to ("a" to "ā"),
            )

        examples.forEach { (modifier, example) ->
            assertEquals("modifier $modifier", example.second, composeLastKey(modifier, example.first))
        }
    }

    @Test
    fun keepsUnmappedCharSequenceInstance() {
        val textBefore = StringBuilder("unmapped")

        assertSame(textBefore, composeLastKey("\"", textBefore))
    }

    @Test
    fun keepsNullWhenNoTextPrecedesModifier() {
        assertNull(composeLastKey("\"", null))
    }

    @Test(expected = IllegalStateException::class)
    fun throwsForUnknownModifier() {
        composeLastKey("unknown", "a")
    }
}
