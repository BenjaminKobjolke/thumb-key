package com.dessalines.thumbkey.textprocessors

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.utils.TAG

class KoreanTextProcessor : TextProcessor {
    internal var state: CompositionState = CompositionState.EMPTY
    internal var leading = ""
    internal var medialVowel = ""
    internal var trailing = ""
    internal var standaloneVowel = ""
    internal var composedText = ""
    internal var selectionStart = 0
    internal var selectionEnd = 0

    override fun handleCommitText(
        ime: IMEService,
        input: CharSequence,
    ) {
        if (input.isEmpty()) return

        val ic = ime.currentInputConnection
        val inputChar = input[0]

        if (KoreanLetters.isConsonant(inputChar)) {
            processConsonant(ic, inputChar)
        } else if (KoreanLetters.isVowel(inputChar)) {
            processVowel(ic, inputChar)
        } else {
            processNonHangul(ic, input)
        }
        Log.d(TAG, "TextProcessor: state $state, cursor position $selectionStart")
    }

    override fun handleKeyEvent(
        ime: IMEService,
        ev: KeyEvent,
    ) {
        val ic = ime.currentInputConnection

        if (state == CompositionState.EMPTY) {
            ic.sendKeyEvent(ev)
            return
        }

        when (ev.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                deleteKeyAction(ime, ev)
            }

            else -> {
                // in any other case (ENTER, DEL, DPAD movement) it just needs to finish job
                handleFinishInput(ime)
                ic.sendKeyEvent(ev)
            }
        }
    }

    override fun handleFinishInput(ime: IMEService) {
        // if in EMPTY state it means processor has been reset
        if (state != CompositionState.EMPTY) {
            // finishComposingText doesn't move cursor
            ime.currentInputConnection.finishComposingText()
            resetState()
        }
    }

    override fun handleCursorUpdate(
        ime: IMEService,
        update: CursorSelectionUpdate,
    ) {
        // after committing each letter selectionStart is incremented to predict cursor position
        if (update.oldSelectionStart == update.newSelectionStart || selectionStart == update.newSelectionStart) return
        if (update.newSelectionStart != update.newSelectionEnd) {
            resetState()
        }

        Log.d(TAG, "TextProcessor: cursor moved -> ${update.newSelectionStart}")

        selectionStart = update.newSelectionStart
        selectionEnd = update.newSelectionEnd

        if (state != CompositionState.EMPTY) {
            handleFinishInput(ime)
        }
    }

    override fun updateCursorPosition(ime: IMEService) {
        val extracted = ime.currentInputConnection.getExtractedText(ExtractedTextRequest(), 0)
        selectionStart = extracted.selectionStart
        selectionEnd = extracted.selectionEnd
    }

    private fun processNonHangul(
        ic: InputConnection,
        input: CharSequence,
    ) {
        ic.commitText(composedText, 1)
        resetState()
        ic.commitText(input, 1)
        selectionStart += 1
        selectionEnd += 1
    }
}
