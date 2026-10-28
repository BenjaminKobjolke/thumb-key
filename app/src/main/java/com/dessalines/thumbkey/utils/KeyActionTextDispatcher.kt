package com.dessalines.thumbkey.utils

import android.util.Log
import android.view.KeyEvent

/** Handles key actions that commit, replace, compose, or navigate through text. */
private fun handleCommitTextAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.CommitText -> {
            commitTextAction(action.text, context)
            updateAutoCapitalization(context)
            context.callbacks.other.onKeyEvent(action)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun commitTextAction(
    text: String,
    context: KeyActionContext,
) {
    Log.d(TAG, "committing key text: $text")
    context.ime.ignoreNextCursorMove()
    val abbreviationManager = AbbreviationManager.getInstance(context.ime.applicationContext)
    if (text == " ") {
        commitSpace(context, abbreviationManager)
    } else {
        commitNonSpace(text, context, abbreviationManager)
    }
}

private fun commitSpace(
    context: KeyActionContext,
    abbreviationManager: AbbreviationManager,
) {
    val textBeforeCursor =
        context.ime.currentInputConnection
            .getTextBeforeCursor(1000, 0)
            ?.toString() ?: ""
    val result =
        if (context.abbreviationBufferEnabled) {
            abbreviationManager.checkAndExpand(textBeforeCursor)
        } else {
            abbreviationManager.checkAndExpandText(textBeforeCursor)
        }
    if (result.shouldExpand) {
        applyExpansion(context.ime.currentInputConnection, result)
        abbreviationManager.clearBuffer()
    } else {
        learnTypedWord(context.ime, textBeforeCursor)
        context.keyboardSettings.textProcessor?.handleCommitText(context.ime, " ")
            ?: context.ime.currentInputConnection.commitText(" ", 1)
        abbreviationManager.onCharacterTyped(" ")
    }
}

private fun commitNonSpace(
    text: String,
    context: KeyActionContext,
    abbreviationManager: AbbreviationManager,
) {
    if (text == "\n") {
        val textBeforeCursor =
            context.ime.currentInputConnection
                .getTextBeforeCursor(1000, 0)
                ?.toString() ?: ""
        learnTypedWord(context.ime, textBeforeCursor)
    }
    context.keyboardSettings.textProcessor?.handleCommitText(context.ime, text)
        ?: context.ime.currentInputConnection.commitText(text, 1)
    abbreviationManager.onCharacterTyped(text)
}

private fun updateAutoCapitalization(context: KeyActionContext) {
    if (context.autoCapitalize && context.keyboardSettings.autoShift) {
        autoCapitalize(
            ime = context.ime,
            onAutoCapitalize = context.callbacks.other.onAutoCapitalize,
            autocapitalizers = context.keyboardSettings.autoCapitalizers,
        )
    } else {
        context.callbacks.other.onAutoCapitalize(false)
    }
}

private fun handleTextNavigationAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean = handleWordNavigationAction(action, context) || handleCursorNavigationAction(action, context)

private fun handleWordNavigationAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.DeleteViaTextManipulation -> {
            deleteViaTextManipulation(context.ime)
        }

        is KeyAction.DeleteWordBeforeCursor -> {
            deleteWordBeforeCursorAction(context.ime, context.keyboardSettings)
        }

        is KeyAction.DeleteWordAfterCursor -> {
            deleteWordAfterCursorAction(context.ime, context.keyboardSettings)
        }

        is KeyAction.PreviousWordBeforeCursor -> {
            previousWordBeforeCursorAction(context.ime, context.keyboardSettings)
        }

        is KeyAction.NextWordAfterCursor -> {
            nextWordAfterCursorAction(context.ime, context.keyboardSettings)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleCursorNavigationAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.SelectLineWithCursor -> {
            selectLineWithCursorAction(context.ime, context.keyboardSettings)
        }

        is KeyAction.CursorToLineStart -> {
            cursorToLineStartAction(context.ime)
        }

        is KeyAction.CursorToLineEnd -> {
            cursorToLineEndAction(context.ime)
        }

        is KeyAction.CursorToTextStart -> {
            cursorToTextStartAction(context.ime)
        }

        is KeyAction.CursorToTextEnd -> {
            cursorToTextEndAction(context.ime)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleTextReplacementAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.ReplaceLastText -> {
            replaceLastTextAction(action, context.ime)
            if (context.autoCapitalize && context.keyboardSettings.autoShift) {
                autoCapitalize(
                    ime = context.ime,
                    onAutoCapitalize = context.callbacks.other.onAutoCapitalize,
                    autocapitalizers = context.keyboardSettings.autoCapitalizers,
                )
            }
        }

        is KeyAction.ReplaceTrailingWhitespace -> {
            replaceTrailingWhitespaceAction(action, context.ime)
        }

        is KeyAction.SmartQuotes -> {
            smartQuotesAction(action, context.ime)
        }

        is KeyAction.ToggleCurrentWordCapitalization -> {
            toggleCurrentWordCapitalizationAction(action, context.ime)
        }

        else -> {
            return false
        }
    }
    return true
}

private fun handleComposeAction(
    action: KeyAction,
    context: KeyActionContext,
): Boolean {
    when (action) {
        is KeyAction.StartComposeCombo -> {
            Log.d(TAG, "starting compose combo")
            context.keyboardSettings.textProcessor?.handleComposeStart(context.ime)
        }

        is KeyAction.ComposeLastKey -> {
            Log.d(TAG, "composing last key")
            val text = action.text
            val textBefore = context.ime.currentInputConnection.getTextBeforeCursor(1, 0)

            val textNew = composeLastKey(text, textBefore)

            if (textNew != textBefore) {
                context.ime.currentInputConnection.deleteSurroundingText(1, 0)
                context.ime.currentInputConnection.commitText(textNew, 1)
            }
        }

        is KeyAction.NormalizeLastKey -> {
            normalizeLastKeyAction(action, context.ime)
        }

        else -> {
            return false
        }
    }
    return true
}

internal fun handleTextActions(
    action: KeyAction,
    context: KeyActionContext,
): Boolean =
    handleCommitTextAction(action, context) ||
        handleTextNavigationAction(action, context) ||
        handleTextReplacementAction(action, context) ||
        handleComposeAction(action, context)
