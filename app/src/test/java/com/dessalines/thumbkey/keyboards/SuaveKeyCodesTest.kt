package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import org.junit.Assert.assertEquals
import org.junit.Test

class SuaveKeyCodesTest {
    @Test(timeout = 30_000)
    fun mapsAllSupportedCharacters() {
        val expectedCodes =
            listOf(
                KeyEvent.KEYCODE_A,
                KeyEvent.KEYCODE_B,
                KeyEvent.KEYCODE_C,
                KeyEvent.KEYCODE_D,
                KeyEvent.KEYCODE_E,
                KeyEvent.KEYCODE_F,
                KeyEvent.KEYCODE_G,
                KeyEvent.KEYCODE_H,
                KeyEvent.KEYCODE_I,
                KeyEvent.KEYCODE_J,
                KeyEvent.KEYCODE_K,
                KeyEvent.KEYCODE_L,
                KeyEvent.KEYCODE_M,
                KeyEvent.KEYCODE_N,
                KeyEvent.KEYCODE_O,
                KeyEvent.KEYCODE_P,
                KeyEvent.KEYCODE_Q,
                KeyEvent.KEYCODE_R,
                KeyEvent.KEYCODE_S,
                KeyEvent.KEYCODE_T,
                KeyEvent.KEYCODE_U,
                KeyEvent.KEYCODE_V,
                KeyEvent.KEYCODE_W,
                KeyEvent.KEYCODE_X,
                KeyEvent.KEYCODE_Y,
                KeyEvent.KEYCODE_Z,
                KeyEvent.KEYCODE_1,
                KeyEvent.KEYCODE_2,
                KeyEvent.KEYCODE_3,
                KeyEvent.KEYCODE_4,
                KeyEvent.KEYCODE_5,
                KeyEvent.KEYCODE_6,
                KeyEvent.KEYCODE_7,
                KeyEvent.KEYCODE_8,
                KeyEvent.KEYCODE_9,
                KeyEvent.KEYCODE_0,
                KeyEvent.KEYCODE_PERIOD,
                KeyEvent.KEYCODE_COMMA,
                KeyEvent.KEYCODE_SLASH,
                KeyEvent.KEYCODE_SEMICOLON,
                KeyEvent.KEYCODE_EQUALS,
                KeyEvent.KEYCODE_MINUS,
                KeyEvent.KEYCODE_LEFT_BRACKET,
                KeyEvent.KEYCODE_RIGHT_BRACKET,
                KeyEvent.KEYCODE_BACKSLASH,
                KeyEvent.KEYCODE_SPACE,
            )
        val supportedCharacters = "abcdefghijklmnopqrstuvwxyz1234567890.,/;=-[]\\ "

        assertEquals(supportedCharacters.length, expectedCodes.size)
        supportedCharacters.forEachIndexed { index, char ->
            assertEquals("Character '$char'", expectedCodes[index], suaveKeyCode(char))
        }
    }

    @Test(timeout = 30_000)
    fun foldsUppercaseLettersToTheirKeyCodes() {
        "abcdefghijklmnopqrstuvwxyz".forEach { lowercase ->
            assertEquals(suaveKeyCode(lowercase), suaveKeyCode(lowercase.uppercaseChar()))
        }
    }

    @Test(timeout = 30_000)
    fun mapsUnsupportedCharactersToUnknown() {
        listOf('ä', 'ß', '!', '\n', '\t').forEach { char ->
            assertEquals("Character '$char'", KeyEvent.KEYCODE_UNKNOWN, suaveKeyCode(char))
        }
    }
}
