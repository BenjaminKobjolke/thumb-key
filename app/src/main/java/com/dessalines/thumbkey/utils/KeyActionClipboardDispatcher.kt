package com.dessalines.thumbkey.utils

/** Routes clipboard and editor-action keys to their focused handlers. */
private fun handleClipboardAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        KeyAction.Cut -> {
            cutAction(context.ime, context.keyboardSettings)
        }

        KeyAction.Copy -> {
            copyAction(context.ime, context.keyboardSettings)
        }

        KeyAction.Paste -> {
            pasteAction(context.ime, context.keyboardSettings)
        }

        KeyAction.Undo -> {
            undoAction(context.ime, context.keyboardSettings)
        }

        KeyAction.Redo -> {
            redoAction(context.ime, context.keyboardSettings)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleImeAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        KeyAction.GotoSettings -> {
            gotoSettingsAction(context.ime)
        }

        KeyAction.IMECompleteAction -> {
            imeCompleteAction(context.ime, context.keyboardSettings)
        }

        KeyAction.SwitchIME -> {
            switchImeAction(context.ime)
        }

        KeyAction.SwitchIMEVoice -> {
            switchImeVoiceAction(context.ime)
        }

        KeyAction.SummeraDictate -> {
            summeraDictateAction(context.ime)
        }

        else -> {
            return false
        }
    }
    return true
}

internal fun handleClipboardAndImeActions(
    action: KeyAction,
    context: KeyActionContext,
): Boolean = handleClipboardAction(action, context) || handleImeAction(action, context)
