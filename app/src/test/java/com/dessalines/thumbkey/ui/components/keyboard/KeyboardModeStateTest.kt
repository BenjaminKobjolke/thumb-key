package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyboardMode
import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardModeStateTest {
    @Test
    fun shiftAndNumericTransitionsResetCapsLockWhenReturningToMain() {
        val capsLocked = KeyboardModeState().toggleCapsLock()

        assertEquals(KeyboardMode.SHIFTED, capsLocked.mode)
        assertEquals(KeyboardMode.MAIN, capsLocked.toggleShiftMode(false).mode)
        assertEquals(false, capsLocked.toggleShiftMode(false).capsLock)
        assertEquals(KeyboardMode.NUMERIC, capsLocked.toggleNumericMode(true).mode)
        assertEquals(false, capsLocked.toggleNumericMode(false).capsLock)
    }

    @Test
    fun emojiPickerCapsLockChangesFlagWithoutChangingMode() {
        val state =
            KeyboardModeState(mode = KeyboardMode.EMOJI)
                .toggleCapsLockFromEmojiPicker()

        assertEquals(KeyboardMode.EMOJI, state.mode)
        assertEquals(true, state.capsLock)
    }

    @Test
    fun languageSwitchReturnsToMainForKeyboardKeys() {
        val state =
            KeyboardModeState(mode = KeyboardMode.SHIFTED, capsLock = true)
                .onLanguageSwitched()

        assertEquals(KeyboardMode.MAIN, state.mode)
        assertEquals(true, state.capsLock)
    }

    @Test
    fun automaticCapitalizationDoesNotChangeNumericMode() {
        val state = KeyboardModeState(mode = KeyboardMode.NUMERIC)

        assertEquals(state, state.onAutoCapitalize(true))
    }

    @Test
    fun numericModeReturnsToLettersOnlyWhenConfiguredSpaceIsTyped() {
        val state = KeyboardModeState(mode = KeyboardMode.NUMERIC, capsLock = true)

        assertEquals(state, state.onKeyAction(KeyAction.CommitText(" "), switchToLettersAfterSpace = false))
        assertEquals(
            KeyboardModeState(),
            state.onKeyAction(KeyAction.CommitText(" "), switchToLettersAfterSpace = true),
        )
        assertEquals(
            state,
            state.onKeyAction(KeyAction.CommitText("x"), switchToLettersAfterSpace = true),
        )
    }

    @Test
    fun sketchToggleIsIgnoredWhileSearchIsActive() {
        val state = KeyboardModeState(mode = KeyboardMode.MAIN)

        assertEquals(state, state.toggleSketchMode(true, searching = true))
        assertEquals(KeyboardMode.SKETCH, state.toggleSketchMode(true, searching = false).mode)
    }
}
