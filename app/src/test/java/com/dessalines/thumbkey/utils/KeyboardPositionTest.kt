package com.dessalines.thumbkey.utils

import androidx.compose.ui.Alignment
import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardPositionTest {
    @Test
    fun floatingIsAppendedAfterExistingStoredPositions() {
        assertEquals(
            listOf("Center", "Right", "Left", "Dual", "Floating"),
            KeyboardPosition.entries.map { it.name },
        )
    }

    @Test
    fun invalidStoredPositionsFallBackToCenter() {
        assertEquals(KeyboardPosition.Center, KeyboardPosition.fromStoredValue(-1))
        assertEquals(KeyboardPosition.Center, KeyboardPosition.fromStoredValue(KeyboardPosition.entries.size))
        assertEquals(KeyboardPosition.Floating, KeyboardPosition.fromStoredValue(4))
    }

    @Test
    fun cyclingLeftAndRightIncludesFloatingBetweenLeftAndDual() {
        assertEquals(KeyboardPosition.Center, cycleLeft(KeyboardPosition.Right))
        assertEquals(KeyboardPosition.Left, cycleLeft(KeyboardPosition.Center))
        assertEquals(KeyboardPosition.Floating, cycleLeft(KeyboardPosition.Left))
        assertEquals(KeyboardPosition.Dual, cycleLeft(KeyboardPosition.Floating))
        assertEquals(KeyboardPosition.Right, cycleLeft(KeyboardPosition.Dual))
        assertEquals(KeyboardPosition.Center, cycleRight(KeyboardPosition.Left))
        assertEquals(KeyboardPosition.Right, cycleRight(KeyboardPosition.Center))
        assertEquals(KeyboardPosition.Dual, cycleRight(KeyboardPosition.Right))
        assertEquals(KeyboardPosition.Floating, cycleRight(KeyboardPosition.Dual))
        assertEquals(KeyboardPosition.Left, cycleRight(KeyboardPosition.Floating))
    }

    @Test
    fun floatingAlignsToTheBottomStartForTheExistingKeyboardFrame() {
        assertEquals(Alignment.BottomStart, keyboardPositionToAlignment(KeyboardPosition.Floating))
    }
}
