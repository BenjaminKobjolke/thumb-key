package com.dessalines.thumbkey.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VoiceImeSelectionTest {
    @Test(timeout = 30_000)
    fun voiceSubtypeIsPreferredOverVoiceImeName() {
        val selection =
            selectVoiceIme(
                packageName = "com.thumbkey",
                candidates =
                    listOf(
                        VoiceImeCandidate("other", "voice.keyboard/.Ime", listOf("keyboard")),
                        VoiceImeCandidate("other", "dictation/.Ime", listOf("keyboard", "VoIcE")),
                    ),
            )

        assertEquals(VoiceImeSelection("dictation/.Ime", 1), selection)
    }

    @Test(timeout = 30_000)
    fun voiceImeNameFallsBackToItsFirstSubtype() {
        val selection =
            selectVoiceIme(
                packageName = "com.thumbkey",
                candidates =
                    listOf(
                        VoiceImeCandidate("other", "keyboard.whisper/.Ime", listOf("keyboard")),
                    ),
            )

        assertEquals(VoiceImeSelection("keyboard.whisper/.Ime", 0), selection)
    }

    @Test(timeout = 30_000)
    fun thumbKeyPackageIsSkipped() {
        val selection =
            selectVoiceIme(
                packageName = "com.thumbkey",
                candidates =
                    listOf(
                        VoiceImeCandidate("com.thumbkey", "voice/.Ime", listOf("voice")),
                        VoiceImeCandidate("other", "keyboard/.Ime", listOf("voice")),
                    ),
            )

        assertEquals(VoiceImeSelection("keyboard/.Ime", 0), selection)
    }

    @Test(timeout = 30_000)
    fun noVoiceCandidateReturnsNull() {
        assertNull(
            selectVoiceIme(
                packageName = "com.thumbkey",
                candidates = listOf(VoiceImeCandidate("other", "keyboard/.Ime", listOf("keyboard"))),
            ),
        )
    }
}
