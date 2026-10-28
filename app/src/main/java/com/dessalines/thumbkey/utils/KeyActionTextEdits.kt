package com.dessalines.thumbkey.utils

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.dessalines.thumbkey.IMEService
import java.util.regex.Pattern

/** Keeps text-edit key actions separate from their main action dispatcher. */
fun deleteViaTextManipulation(ime: IMEService) {
    val ic = ime.currentInputConnection
    val editorInfo = ime.currentInputEditorInfo
    val inputType = editorInfo?.inputType ?: 0

    if (inputType == EditorInfo.TYPE_NULL) {
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
    } else {
        val selectedText = ic.getSelectedText(0)
        if (!selectedText.isNullOrEmpty()) {
            ic.commitText("", 1)
        } else {
            val textBefore = ic.getTextBeforeCursor(50, 0)
            if (!textBefore.isNullOrEmpty()) {
                val bi = java.text.BreakIterator.getCharacterInstance()
                bi.setText(textBefore.toString())
                val end = bi.last()
                val start = bi.previous()
                val deleteCount =
                    if (start == java.text.BreakIterator.DONE) {
                        textBefore.length
                    } else {
                        end - start
                    }
                ic.deleteSurroundingText(deleteCount, 0)
            }
        }
    }
}

fun deleteWordBeforeCursorAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    Log.d(TAG, "deleting last word")
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    deleteWordBeforeCursor(ime)
}

fun deleteWordAfterCursorAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    Log.d(TAG, "deleting next word")
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    deleteWordAfterCursor(ime)
}

fun previousWordBeforeCursorAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    Log.d(TAG, "Previous word")
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    previousWordBeforeCursor(ime)
}

fun nextWordAfterCursorAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    Log.d(TAG, "Next word")
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    nextWordAfterCursor(ime)
}

fun selectLineWithCursorAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    Log.d(TAG, "Select Line")
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    selectLineWithCursor(ime)
}

fun cursorToLineStartAction(ime: IMEService) {
    Log.d(TAG, "Cursor to line start")
    cursorToLineStart(ime)
}

fun cursorToLineEndAction(ime: IMEService) {
    Log.d(TAG, "Cursor to line end")
    cursorToLineEnd(ime)
}

fun cursorToTextStartAction(ime: IMEService) {
    Log.d(TAG, "Cursor to text start")
    cursorToTextStart(ime)
}

fun cursorToTextEndAction(ime: IMEService) {
    Log.d(TAG, "Cursor to text end")
    cursorToTextEnd(ime)
}

fun replaceLastTextAction(
    action: KeyAction.ReplaceLastText,
    ime: IMEService,
) {
    Log.d(TAG, "replacing last word")
    ime.ignoreNextCursorMove()
    ime.currentInputConnection.deleteSurroundingText(action.trimCount, 0)
    ime.currentInputConnection.commitText(action.text, 1)
}

fun replaceTrailingWhitespaceAction(
    action: KeyAction.ReplaceTrailingWhitespace,
    ime: IMEService,
) {
    Log.d(TAG, "replacing trailing whitespace")
    val ic = ime.currentInputConnection
    val textBeforeCursor = ic.getTextBeforeCursor(action.distanceBack, 0)?.toString() ?: ""
    val matcher = Pattern.compile("\\s+$").matcher(textBeforeCursor)

    if (matcher.find()) {
        ic.deleteSurroundingText(matcher.end() - matcher.start(), 0)
    }
    ic.commitText(action.text, 1)
}

fun smartQuotesAction(
    action: KeyAction.SmartQuotes,
    ime: IMEService,
) {
    val textBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(1, 0)?.toString() ?: ""
    val textNew = if (textBeforeCursor.matches(Regex("\\S"))) action.end else action.start
    ime.currentInputConnection.commitText(textNew, 1)
}

fun normalizeLastKeyAction(
    action: KeyAction.NormalizeLastKey,
    ime: IMEService,
) {
    Log.d(TAG, "combining last key")
    val textBefore = ime.currentInputConnection.getTextBeforeCursor(1, 0)
    val textNew = java.text.Normalizer.normalize("$textBefore${action.text}", action.form)

    if (textNew != textBefore) {
        ime.currentInputConnection.deleteSurroundingText(1, 0)
        ime.currentInputConnection.commitText(textNew, 1)
    }
}

fun toggleCurrentWordCapitalizationAction(
    action: KeyAction.ToggleCurrentWordCapitalization,
    ime: IMEService,
) {
    val maxLength = 100
    val wordBorderCharacters = ".,;:!?\"'()-—[]{}<>/\\|#$%^_+=~`"
    val textBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(maxLength, 0)
    if (!textBeforeCursor.isNullOrEmpty()) {
        val startWordIndex =
            textBeforeCursor
                .toString()
                .indexOfLast { it.isWhitespace() || wordBorderCharacters.contains(it) }
                .plus(1)
        if (startWordIndex < textBeforeCursor.length) {
            val replacementText =
                if (action.toggleUp) {
                    if (textBeforeCursor[startWordIndex].isUpperCase()) {
                        textBeforeCursor.substring(startWordIndex).uppercase()
                    } else {
                        textBeforeCursor
                            .substring(startWordIndex, startWordIndex + 1)
                            .uppercase() + textBeforeCursor.substring(startWordIndex + 1)
                    }
                } else {
                    textBeforeCursor.substring(startWordIndex).lowercase()
                }
            ime.currentInputConnection.deleteSurroundingText(
                textBeforeCursor.length - startWordIndex,
                0,
            )
            ime.currentInputConnection.commitText(replacementText, 1)
        }
    }
}
