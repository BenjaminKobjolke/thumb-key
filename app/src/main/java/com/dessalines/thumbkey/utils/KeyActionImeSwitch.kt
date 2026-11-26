package com.dessalines.thumbkey.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodSubtype
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
    val enabledInputMethods = imeManager.enabledInputMethodList
    val selection =
        selectVoiceIme(
            packageName = ime.packageName,
            candidates =
                enabledInputMethods.map { inputMethod ->
                    VoiceImeCandidate(
                        packageName = inputMethod.packageName,
                        id = inputMethod.id,
                        subtypeModes =
                            (0 until inputMethod.subtypeCount).map { index ->
                                inputMethod.getSubtypeAt(index).mode
                            },
                    )
                },
        )

    if (selection == null) {
        Log.i(TAG, "No enabled voice input method found")
        return
    }

    val inputMethod = enabledInputMethods.firstOrNull { it.id == selection.id } ?: return
    val subtype = selection.subtypeIndex?.let(inputMethod::getSubtypeAt)
    switchToVoiceInputMethod(ime, imeManager, selection.id, subtype)
}

data class VoiceImeCandidate(
    val packageName: String,
    val id: String,
    val subtypeModes: List<String>,
)

data class VoiceImeSelection(
    val id: String,
    val subtypeIndex: Int?,
)

fun selectVoiceIme(
    packageName: String,
    candidates: List<VoiceImeCandidate>,
): VoiceImeSelection? {
    val otherImeCandidates = candidates.filter { it.packageName != packageName }

    for (candidate in otherImeCandidates) {
        val voiceSubtypeIndex =
            candidate.subtypeModes.indexOfFirst { it.equals("voice", ignoreCase = true) }
        if (voiceSubtypeIndex >= 0) {
            return VoiceImeSelection(candidate.id, voiceSubtypeIndex)
        }
    }

    val voiceIme =
        otherImeCandidates.firstOrNull { candidate ->
            VOICE_IME_ID_KEYWORDS.any { keyword -> candidate.id.contains(keyword, ignoreCase = true) }
        } ?: return null

    return VoiceImeSelection(voiceIme.id, voiceIme.subtypeModes.indices.firstOrNull())
}

private val VOICE_IME_ID_KEYWORDS = listOf("voice", "whisper", "transcribro")

private fun switchToVoiceInputMethod(
    ime: IMEService,
    imeManager: InputMethodManager,
    id: String,
    subtype: InputMethodSubtype?,
) {
    val switched =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                if (subtype != null) {
                    ime.switchInputMethod(id, subtype)
                } else {
                    ime.switchInputMethod(id)
                }
                true
            } catch (exception: RuntimeException) {
                Log.w(TAG, "Could not switch to $id, trying the window token", exception)
                false
            }
        } else {
            false
        }

    if (!switched) {
        val token =
            ime.window.window
                ?.attributes
                ?.token
        if (token == null) {
            Log.i(TAG, "No window token available to switch to $id")
        } else {
            try {
                if (subtype != null) {
                    imeManager.setInputMethodAndSubtype(token, id, subtype)
                } else {
                    @Suppress("DEPRECATION")
                    imeManager.setInputMethod(token, id)
                }
            } catch (exception: RuntimeException) {
                Log.w(TAG, "Could not switch to $id using the window token", exception)
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
