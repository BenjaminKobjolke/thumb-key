package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingKeyboardFrameStateTest {
    @Test(timeout = 30_000)
    fun normalizeFloatingKeyboardPosition_clampsFractionsToTheFrame() {
        assertEquals(
            Offset(0f, 1f),
            normalizeFloatingKeyboardPosition(-0.2f, 1.4f),
        )
    }

    @Test(timeout = 30_000)
    fun normalizeFloatingKeyboardPosition_preservesFractionsInsideTheFrame() {
        assertEquals(
            Offset(0.25f, 0.75f),
            normalizeFloatingKeyboardPosition(0.25f, 0.75f),
        )
    }
}
