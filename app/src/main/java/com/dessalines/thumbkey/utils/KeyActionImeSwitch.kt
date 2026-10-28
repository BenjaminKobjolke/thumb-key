package com.dessalines.thumbkey.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.MainActivity
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.summera.SummeraAccount
import com.dessalines.thumbkey.summera.SummeraDictation
import com.dessalines.thumbkey.summera.hasMicrophonePermission

/** Implements editor completion and IME navigation key actions. */
fun getImeActionCode(ime: IMEService): Int {
    val ei = ime.currentInputEditorInfo ?: return EditorInfo.IME_ACTION_NONE

    return if ((ei.imeOptions and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
        EditorInfo.IME_ACTION_NONE
    } else if (ei.actionLabel != null) {
        IME_ACTION_CUSTOM_LABEL
    } else {
        // Note: this is different from editorInfo.actionId, hence "ImeOptionsActionId"
        ei.imeOptions and EditorInfo.IME_MASK_ACTION
    }
}

fun imeCompleteAction(
    ime: IMEService,
    keyboardSettings: KeyboardDefinitionSettings,
) {
    val textBeforeCursor = ime.currentInputConnection.getTextBeforeCursor(1000, 0)?.toString() ?: ""
    learnTypedWord(ime, textBeforeCursor)
    // A lot of apps like discord and slack use weird IME actions,
    // so its best to only check the none case
    when (val imeAction = getImeActionCode(ime)) {
        IME_ACTION_CUSTOM_LABEL -> {
            ime.currentInputConnection.performEditorAction(ime.currentInputEditorInfo.actionId)
        }

        EditorInfo.IME_ACTION_NONE -> {
            val ev = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            keyboardSettings.textProcessor?.handleKeyEvent(ime, ev)
                ?: ime.currentInputConnection.sendKeyEvent(ev)
        }

        else -> {
            ime.currentInputConnection.performEditorAction(imeAction)
        }
    }
}

fun gotoSettingsAction(ime: IMEService) {
    val mainActivityIntent = Intent(ime, MainActivity::class.java)
    mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    mainActivityIntent.putExtra("startRoute", "settings")
    ime.startActivity(mainActivityIntent)
}

fun switchImeAction(ime: IMEService) {
    when (ime.switchImeMode()) {
        ImeSwitchMode.Picker -> {
            val imeManager = ime.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imeManager.showInputMethodPicker()
        }

        ImeSwitchMode.Previous -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ime.switchToPreviousInputMethod()
            } else {
                ime.window.window?.attributes?.token?.let { token ->
                    val imeManager = ime.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    @Suppress("DEPRECATION")
                    imeManager.switchToLastInputMethod(token)
                }
            }
        }

        ImeSwitchMode.Next -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ime.switchToNextInputMethod(false)
            } else {
                ime.window.window?.attributes?.token?.let { token ->
                    val imeManager = ime.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    @Suppress("DEPRECATION")
                    imeManager.switchToNextInputMethod(token, false)
                }
            }
        }
    }
}

fun switchImeVoiceAction(ime: IMEService) {
    val imeManager = ime.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val list: List<InputMethodInfo> = imeManager.enabledInputMethodList
    for (el in list) {
        for (i in 0 until el.subtypeCount) {
            if (el.getSubtypeAt(i).mode != "voice") continue
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ime.switchInputMethod(el.id)
            } else {
                ime.window.window?.let { window ->
                    @Suppress("DEPRECATION")
                    imeManager.setInputMethod(window.attributes.token, el.id)
                }
            }
        }
    }
}

fun summeraDictateAction(ime: IMEService) {
    val blocker =
        when {
            SummeraAccount.credentials(ime) == null -> R.string.summera_sign_in_first
            !hasMicrophonePermission(ime) -> R.string.summera_microphone_permission
            else -> null
        }
    if (blocker != null) {
        Toast.makeText(ime, blocker, Toast.LENGTH_LONG).show()
    } else {
        SummeraDictation.toggle(ime)
    }
}
