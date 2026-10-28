package com.dessalines.thumbkey.utils

import android.util.Log
import android.view.KeyEvent

/** Handles keyboard modes, position changes, and small remaining key actions. */
private fun handleModeToggleAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    if (action is KeyAction.Sketch) {
        context.onSketchAction(action)
        return true
    }
    return handlePrimaryModeToggleAction(action, context) || handleSecondaryModeToggleAction(action, context)
}

private fun handlePrimaryModeToggleAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.ToggleShiftMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Shifted: $enable")
            context.callbacks.modeToggles.onToggleShiftMode(enable)
        }

        is KeyAction.ToggleCtrlMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Ctrled: $enable")
            context.callbacks.modeToggles.onToggleCtrlMode(enable)
        }

        is KeyAction.ToggleAltMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Alted: $enable")
            context.callbacks.modeToggles.onToggleAltMode(enable)
        }

        is KeyAction.ToggleNumericMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Numeric: $enable")
            context.callbacks.modeToggles.onToggleNumericMode(enable)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleSecondaryModeToggleAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.ToggleEmojiMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Emoji: $enable")
            context.keyboardSettings.textProcessor?.handleFinishInput(context.ime)
            context.callbacks.secondaryModeToggles.onToggleEmojiMode(enable)
        }

        is KeyAction.ToggleClipboardMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Clipboard: $enable")
            context.keyboardSettings.textProcessor?.handleFinishInput(context.ime)
            context.callbacks.secondaryModeToggles.onToggleClipboardMode(enable)
        }

        is KeyAction.ToggleTranscriptHistoryMode -> {
            context.keyboardSettings.textProcessor?.handleFinishInput(context.ime)
            context.callbacks.secondaryModeToggles.onToggleTranscriptHistoryMode(action.enable)
        }

        is KeyAction.ToggleSketchMode -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Sketch: $enable")
            context.keyboardSettings.textProcessor?.handleFinishInput(context.ime)
            context.callbacks.secondaryModeToggles.onToggleSketchMode(enable)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleKeyboardControlAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        KeyAction.ToggleCapsLock -> {
            Log.d(TAG, "Toggling Caps Lock")
            context.callbacks.other.onToggleCapsLock()
        }

        KeyAction.ToggleHideLetters -> {
            Log.d(TAG, "Toggling Hide letters")
            context.callbacks.other.onToggleHideLetters()
        }

        is KeyAction.ShiftAndCapsLock -> {
            val enable = action.enable
            Log.d(TAG, "Toggling Shifted: $enable")
            context.callbacks.modeToggles.onToggleShiftMode(enable)
            context.callbacks.other.onToggleCapsLock()
        }

        KeyAction.SwitchLanguage -> {
            context.callbacks.other.onSwitchLanguage()
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleKeyboardPositionAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    val positionChange = action.positionChange() ?: return false
    context.callbacks.other.onChangePosition(positionChange)
    return true
}

private fun KeyAction.positionChange(): ((KeyboardPosition) -> KeyboardPosition)? =
    when (this) {
        is KeyAction.MoveKeyboard.ToPosition -> {
            val target = position
            ({ _: KeyboardPosition -> target })
        }

        KeyAction.MoveKeyboard.Left -> {
            ::moveLeft
        }

        KeyAction.MoveKeyboard.Right -> {
            ::moveRight
        }

        KeyAction.MoveKeyboard.CycleLeft -> {
            ::cycleLeft
        }

        KeyAction.MoveKeyboard.CycleRight -> {
            ::cycleRight
        }

        else -> {
            null
        }
    }

private fun moveLeft(position: KeyboardPosition): KeyboardPosition =
    if (position == KeyboardPosition.Right) KeyboardPosition.Center else KeyboardPosition.Left

private fun moveRight(position: KeyboardPosition): KeyboardPosition =
    if (position == KeyboardPosition.Left) KeyboardPosition.Center else KeyboardPosition.Right

private val CYCLE_LEFT_POSITIONS =
    mapOf(
        KeyboardPosition.Right to KeyboardPosition.Dual,
        KeyboardPosition.Center to KeyboardPosition.Left,
        KeyboardPosition.Left to KeyboardPosition.Right,
        KeyboardPosition.Dual to KeyboardPosition.Center,
    )

private val CYCLE_RIGHT_POSITIONS =
    mapOf(
        KeyboardPosition.Left to KeyboardPosition.Dual,
        KeyboardPosition.Center to KeyboardPosition.Right,
        KeyboardPosition.Right to KeyboardPosition.Left,
        KeyboardPosition.Dual to KeyboardPosition.Center,
    )

private fun cycleLeft(position: KeyboardPosition): KeyboardPosition = CYCLE_LEFT_POSITIONS.getValue(position)

private fun cycleRight(position: KeyboardPosition): KeyboardPosition = CYCLE_RIGHT_POSITIONS.getValue(position)

private fun handleOtherKeyAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean = handleEventAndDeleteActions(action, context) || handleRemainingKeyActions(action, context)

private fun handleEventAndDeleteActions(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.SendEvent -> {
            val ev = action.event
            Log.d(TAG, "sending key event: $ev")
            context.keyboardSettings.textProcessor?.handleKeyEvent(context.ime, ev)
                ?: context.ime.currentInputConnection.sendKeyEvent(ev)
            context.callbacks.other.onKeyEvent(action)
        }

        // Some apps have issues with delete key events; report those issues in their trackers.
        is KeyAction.DeleteKeyAction -> {
            val ev = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
            context.keyboardSettings.textProcessor?.handleKeyEvent(context.ime, ev)
                ?: context.ime.currentInputConnection.sendKeyEvent(ev)
            // Update abbreviation buffer
            AbbreviationManager.getInstance(context.ime.applicationContext).onBackspace()
        }

        is KeyAction.DeleteCharacterAfterCursor -> {
            val ev = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_FORWARD_DEL)
            context.keyboardSettings.textProcessor?.handleKeyEvent(context.ime, ev)
                ?: context.ime.currentInputConnection.sendKeyEvent(ev)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleRemainingKeyActions(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        KeyAction.SelectAll -> {
            // Check here for the action #s:
            // https://developer.android.com/reference/android/R.id
            context.keyboardSettings.textProcessor?.handleFinishInput(context.ime)
            context.ime.currentInputConnection.performContextMenuAction(android.R.id.selectAll)
        }

        is KeyAction.ExpandAbbreviation -> {
            val text = action.text
            context.ime.currentInputConnection.commitText(text, 1)
        }

        KeyAction.HideKeyboard -> {
            context.ime.requestHideSelf(0)
        }

        KeyAction.Noop -> {}

        else -> {
            return false
        }
    }
    return true
}

internal fun handleModeAndOtherActions(
    action: KeyAction,
    context: KeyActionContext,
) {
    if (handleModeToggleAction(action, context)) return
    if (handleKeyboardControlAction(action, context)) return
    if (handleKeyboardPositionAction(action, context)) return
    handleOtherKeyAction(action, context)
}
