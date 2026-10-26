package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyboardMode
import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardCallbacksTest {
    @Test
    fun autoCapitalizedFirstLetterReturnsToMainMode() {
        val (callbacks, currentState) = callbacksFor(KeyboardModeState(mode = KeyboardMode.SHIFTED))

        callbacks.onAutoCapitalize(false)
        callbacks.onKeyEvent(KeyAction.CommitText("H"))

        assertEquals(KeyboardMode.MAIN, currentState().mode)
    }

    @Test
    fun autoCapitalizationKeepsCapsLockEnabled() {
        val (callbacks, currentState) =
            callbacksFor(KeyboardModeState(mode = KeyboardMode.SHIFTED, capsLock = true))

        callbacks.onAutoCapitalize(false)
        callbacks.onKeyEvent(KeyAction.CommitText("H"))

        assertEquals(KeyboardMode.SHIFTED, currentState().mode)
        assertEquals(true, currentState().capsLock)
    }

    @Test
    fun autoCapitalizesAfterSentenceEndingSpace() {
        val (callbacks, currentState) = callbacksFor(KeyboardModeState())

        callbacks.onAutoCapitalize(true)
        callbacks.onKeyEvent(KeyAction.CommitText(". "))

        assertEquals(KeyboardMode.SHIFTED, currentState().mode)
    }

    @Test
    fun numericModeReturnsToLettersAfterSpaceWhenConfigured() {
        val (callbacks, currentState) =
            callbacksFor(
                KeyboardModeState(mode = KeyboardMode.NUMERIC),
                switchToLettersAfterSpace = true,
            )

        callbacks.onAutoCapitalize(false)
        callbacks.onKeyEvent(KeyAction.CommitText(" "))

        assertEquals(KeyboardMode.MAIN, currentState().mode)
    }

    private fun callbacksFor(
        initialState: KeyboardModeState,
        switchToLettersAfterSpace: Boolean = false,
    ): Pair<KeyboardCallbacks, () -> KeyboardModeState> {
        var state = initialState
        val callbacks =
            keyboardCallbacks(
                KeyboardCallbacksInput(
                    update = { transform -> state = transform(state) },
                    sketchSearching = false,
                    switchToLettersAfterSpace = switchToLettersAfterSpace,
                    onSwitchLanguage = {},
                    onChangePosition = {},
                    onToggleHideLetters = {},
                ),
            )
        return Pair(callbacks, { state })
    }
}
