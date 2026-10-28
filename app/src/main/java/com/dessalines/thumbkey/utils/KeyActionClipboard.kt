package com.dessalines.thumbkey.utils

import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R

/** Implements cut, copy, paste, undo, and redo key actions. */
fun cutAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    if (ime.currentInputConnection.getSelectedText(0).isNullOrEmpty()) {
        ime.currentInputConnection.performContextMenuAction(android.R.id.selectAll)
        val delayInMillis = 100L
        Handler(Looper.getMainLooper()).postDelayed({ performCut(ime) }, delayInMillis)
    } else {
        performCut(ime)
    }
}

private fun performCut(ime: IMEService) {
    if (ime.clipboardUsePrivate()) {
        val text = ime.currentInputConnection.getSelectedText(0).toString()
        ime.clipboardAddPrivateClip(text)?.let {
            ime.currentInputConnection.commitText("", 1)
        }
    } else {
        ime.currentInputConnection.performContextMenuAction(android.R.id.cut)
    }
}

fun copyAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    if (ime.currentInputConnection.getSelectedText(0).isNullOrEmpty()) {
        ime.currentInputConnection.performContextMenuAction(android.R.id.selectAll)
        val delayInMillis = 100L
        Handler(Looper.getMainLooper()).postDelayed({ performCopy(ime) }, delayInMillis)
    } else {
        performCopy(ime)
    }
}

private fun performCopy(ime: IMEService) {
    if (ime.clipboardUsePrivate()) {
        val text = ime.currentInputConnection.getSelectedText(0)
        if (text != null) {
            ime.clipboardAddPrivateClip(text.toString())?.let {
                val message = ime.getString(R.string.copy)
                Toast.makeText(ime, message, Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        ime.currentInputConnection.performContextMenuAction(android.R.id.copy)
        val message = ime.getString(R.string.copy)
        Toast.makeText(ime, message, Toast.LENGTH_SHORT).show()
    }
}

fun pasteAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    if (!ime.clipboardUsePrivate()) {
        ime.currentInputConnection.performContextMenuAction(android.R.id.paste)
    } else if (ime.clipboardWasLastCopyDoneViaSystem()) {
        ime.currentInputConnection.performContextMenuAction(android.R.id.paste)
    } else {
        val text = ime.clipboardGetLastClip()
        if (!text.isNullOrEmpty()) {
            ime.currentInputConnection.commitText(text, 1)
        }
    }
}

fun undoAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    ime.currentInputConnection.sendKeyEvent(
        KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON),
    )
}

fun redoAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    keyboardSettings.textProcessor?.handleFinishInput(ime)
    ime.currentInputConnection.sendKeyEvent(
        KeyEvent(
            0,
            0,
            KeyEvent.ACTION_DOWN,
            KeyEvent.KEYCODE_Z,
            0,
            (KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON),
        ),
    )
}
