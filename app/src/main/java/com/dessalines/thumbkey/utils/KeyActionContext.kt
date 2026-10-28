package com.dessalines.thumbkey.utils

import com.dessalines.thumbkey.IMEService

/** Keeps the editor state and callbacks needed to execute one key action together. */
internal data class KeyActionContext(
    val ime: IMEService,
    val autoCapitalize: Boolean,
    val keyboardSettings: KeyboardDefinitionSettings,
    val callbacks: KeyActionCallbacks,
    val abbreviationBufferEnabled: Boolean,
    val onSketchAction: (KeyAction.Sketch) -> Unit = {},
)

internal data class KeyActionCallbacks(
    val modeToggles: KeyActionModeCallbacks,
    val secondaryModeToggles: KeyActionSecondaryModeCallbacks,
    val other: KeyActionOtherCallbacks,
)

internal data class KeyActionModeCallbacks(
    val onToggleShiftMode: (Boolean) -> Unit,
    val onToggleCtrlMode: (Boolean) -> Unit,
    val onToggleAltMode: (Boolean) -> Unit,
    val onToggleNumericMode: (Boolean) -> Unit,
)

internal data class KeyActionSecondaryModeCallbacks(
    val onToggleEmojiMode: (Boolean) -> Unit,
    val onToggleClipboardMode: (Boolean) -> Unit,
    val onToggleTranscriptHistoryMode: (Boolean) -> Unit,
    val onToggleSketchMode: (Boolean) -> Unit,
)

internal data class KeyActionOtherCallbacks(
    val onToggleCapsLock: () -> Unit,
    val onToggleHideLetters: () -> Unit,
    val onAutoCapitalize: (Boolean) -> Unit,
    val onSwitchLanguage: () -> Unit,
    val onChangePosition: ((KeyboardPosition) -> KeyboardPosition) -> Unit,
    val onKeyEvent: (KeyAction) -> Unit,
)
