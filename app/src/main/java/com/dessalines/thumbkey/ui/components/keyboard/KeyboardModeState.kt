package com.dessalines.thumbkey.ui.components.keyboard

import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyboardMode

typealias ModeStateUpdate = ((KeyboardModeState) -> KeyboardModeState) -> Unit

/** Tracks mode and caps-lock changes shared by keyboard mode handlers. */
data class KeyboardModeState(
    val mode: KeyboardMode = KeyboardMode.MAIN,
    val capsLock: Boolean = false,
) {
    fun toggleShiftMode(enable: Boolean) =
        if (enable) copy(mode = KeyboardMode.SHIFTED) else copy(mode = KeyboardMode.MAIN, capsLock = false)

    fun toggleCtrlMode(enable: Boolean) = copy(mode = if (enable) KeyboardMode.CTRLED else KeyboardMode.MAIN)

    fun toggleAltMode(enable: Boolean) = copy(mode = if (enable) KeyboardMode.ALTED else KeyboardMode.MAIN)

    fun toggleNumericMode(enable: Boolean) =
        if (enable) copy(mode = KeyboardMode.NUMERIC) else copy(mode = KeyboardMode.MAIN, capsLock = false)

    fun toggleEmojiMode(enable: Boolean) = copy(mode = if (enable) KeyboardMode.EMOJI else KeyboardMode.MAIN)

    fun toggleClipboardMode(enable: Boolean) = copy(mode = if (enable) KeyboardMode.CLIPBOARD else KeyboardMode.MAIN)

    fun toggleTranscriptHistoryMode(enable: Boolean) = copy(mode = if (enable) KeyboardMode.TRANSCRIPTS else KeyboardMode.MAIN)

    fun toggleSketchMode(
        enable: Boolean,
        searching: Boolean,
    ) = if (searching) this else copy(mode = if (enable) KeyboardMode.SKETCH else KeyboardMode.MAIN)

    fun toggleCapsLock(): KeyboardModeState {
        val nextCapsLock = !capsLock
        return copy(capsLock = nextCapsLock, mode = if (nextCapsLock) KeyboardMode.SHIFTED else mode)
    }

    // Emoji picker controller keys toggle the flag but leave the picker mode visible.
    fun toggleCapsLockFromEmojiPicker() = copy(capsLock = !capsLock)

    fun onAutoCapitalize(enable: Boolean) =
        when {
            mode == KeyboardMode.NUMERIC -> this
            enable -> copy(mode = KeyboardMode.SHIFTED)
            !capsLock -> copy(mode = KeyboardMode.MAIN)
            else -> this
        }

    fun onLanguageSwitched() = copy(mode = KeyboardMode.MAIN)

    fun onKeyAction(
        action: KeyAction,
        switchToLettersAfterSpace: Boolean,
    ) = when {
        (mode == KeyboardMode.CTRLED || mode == KeyboardMode.ALTED) && action is KeyAction.SendEvent -> {
            copy(mode = KeyboardMode.MAIN)
        }

        mode == KeyboardMode.NUMERIC &&
            switchToLettersAfterSpace &&
            action is KeyAction.CommitText &&
            action.text == " " -> {
            copy(mode = KeyboardMode.MAIN, capsLock = false)
        }

        else -> {
            this
        }
    }
}
