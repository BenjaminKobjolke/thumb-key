package com.dessalines.thumbkey.utils

import android.view.KeyEvent
import com.dessalines.thumbkey.IMEService

fun deleteWordBeforeCursor(ime: IMEService) {
    val wordsBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(9999, 0)

    val pattern = Regex("(\\w+\\W?|[^\\s\\w]+)?\\s*$")
    val lastWordLength = wordsBeforeCursor?.let { pattern.find(it)?.value?.length } ?: 0

    ime.currentInputConnection.deleteSurroundingText(lastWordLength, 0)
}

fun deleteWordAfterCursor(ime: IMEService) {
    val wordsAfterCursor = ime.currentInputConnection.getTextAfterCursor(9999, 0)

    val pattern = Regex("^\\s?(\\w+\\W?|[^\\s\\w]+|\\s+)")
    val nextWordLength = wordsAfterCursor?.let { pattern.find(it)?.value?.length } ?: 0

    ime.currentInputConnection.deleteSurroundingText(0, nextWordLength)
}

fun moveCursor(
    ime: IMEService,
    delta: Int,
) {
    val selection = startSelection(ime)
    selection.right(delta)
    ime.currentInputConnection.setSelection(selection.end, selection.end)
}

fun cursorToLineStart(ime: IMEService) {
    ime.currentInputConnection.sendKeyEvent(
        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_HOME),
    )
}

fun cursorToLineEnd(ime: IMEService) {
    ime.currentInputConnection.sendKeyEvent(
        KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MOVE_END),
    )
}

fun cursorToTextStart(ime: IMEService) {
    ime.currentInputConnection.setSelection(0, 0)
}

fun cursorToTextEnd(ime: IMEService) {
    val ic = ime.currentInputConnection
    // Sum text before and after the cursor to find the absolute end position.
    // Using large limits to handle long documents; typical mobile content is well within range.
    val before = ic.getTextBeforeCursor(1_000_000, 0)?.length ?: return
    val after = ic.getTextAfterCursor(1_000_000, 0)?.length ?: return
    ic.setSelection(before + after, before + after)
}

fun previousWordBeforeCursor(ime: IMEService) {
    val wordsBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(9999, 0)

    val pattern = Regex("(\\w+\\W?|[^\\s\\w]+)?\\s*$")
    val lastWordLength = wordsBeforeCursor?.let { pattern.find(it)?.value?.length } ?: 0

    moveCursor(ime, -lastWordLength)
}

fun nextWordAfterCursor(ime: IMEService) {
    val wordsAfterCursor = ime.currentInputConnection.getTextAfterCursor(9999, 0)

    val pattern = Regex("^\\s?(\\w+\\W?|[^\\s\\w]+|\\s+)")
    val nextWordLength = wordsAfterCursor?.let { pattern.find(it)?.value?.length } ?: 0

    moveCursor(ime, nextWordLength)
}

fun selectLineWithCursor(ime: IMEService) {
    // Find line start
    val wordsBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(9999, 0)
    if ((wordsBeforeCursor?.length ?: 0) != 0) {
        // If we are at the beginning of a line nothing to do, else
        val lastChar = wordsBeforeCursor?.last() ?: ' '
        if (!(lastChar == '\n' || lastChar == '\r')) {
            val patternStart = Regex("^[^\\n\\r]*\\Z", RegexOption.MULTILINE)
            val previousLineStart = wordsBeforeCursor?.let { patternStart.find(it)?.value?.length } ?: 0

            // Move to line start
            moveCursor(ime, -previousLineStart)
        }
    }

    // Find length of line, with endline if present
    val wordsAfterCursor = ime.currentInputConnection.getTextAfterCursor(9999, 0)
    val patternLine = Regex("\\A[^\\n\\r]*(\\n|\\r)?", RegexOption.MULTILINE)
    val lineLength = wordsAfterCursor?.let { patternLine.find(it)?.value?.length } ?: 0

    val selection = startSelection(ime)
    selection.right(lineLength)
    ime.currentInputConnection.setSelection(selection.start, selection.end)
}

fun startSelection(ime: IMEService): Selection {
    val cursorPosition =
        ime.currentInputConnection
            .getTextBeforeCursor(
                Integer.MAX_VALUE,
                0,
            )?.length
    cursorPosition?.let {
        return Selection(it, it, true)
    }
    return Selection()
}
