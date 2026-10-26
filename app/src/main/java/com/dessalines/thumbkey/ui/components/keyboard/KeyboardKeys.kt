package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardC
import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.KeyboardDefinitionSettings
import com.dessalines.thumbkey.utils.KeyboardPosition
import kotlin.time.TimeMark

internal data class KeyboardCallbacks(
    val onToggleShiftMode: (Boolean) -> Unit,
    val onToggleCtrlMode: (Boolean) -> Unit,
    val onToggleAltMode: (Boolean) -> Unit,
    val onToggleNumericMode: (Boolean) -> Unit,
    val onToggleEmojiMode: (Boolean) -> Unit,
    val onToggleClipboardMode: (Boolean) -> Unit,
    val onToggleTranscriptHistoryMode: (Boolean) -> Unit,
    val onToggleSketchMode: (Boolean) -> Unit,
    val onToggleCapsLock: () -> Unit,
    val onToggleCapsLockFromEmojiPicker: () -> Unit,
    val onToggleHideLetters: () -> Unit,
    val onAutoCapitalize: (Boolean) -> Unit,
    val onSwitchLanguage: () -> Unit,
    val onChangePosition: ((KeyboardPosition) -> KeyboardPosition) -> Unit,
    val onKeyEvent: (KeyAction) -> Unit,
)

internal data class KeyboardEnvironment(
    val lastAction: MutableState<Pair<KeyAction, TimeMark>?>,
    val keyboardSettings: KeyboardDefinitionSettings,
    val config: KeyboardScreenConfig,
    val capsLock: Boolean,
    val legendHeight: Int,
    val legendWidth: Int,
    val keyHeight: Float,
    val keyWidth: Float,
    val keyBorderWidth: Float,
    val keyRadius: Float,
    val callbacks: KeyboardCallbacks,
)

internal data class ScreenKeyData(
    val key: KeyItemC,
    val environment: KeyboardEnvironment,
    val ghostKey: KeyItemC? = null,
    val oppositeCaseKey: KeyItemC? = null,
    val numericKey: KeyItemC? = null,
    val onSketchAction: (KeyAction.Sketch) -> Unit = {},
)

internal data class KeyboardEnvironmentInput(
    val config: KeyboardScreenConfig,
    val keyboardDefinition: KeyboardDefinition,
    val modeState: KeyboardModeState,
    val lastAction: MutableState<Pair<KeyAction, TimeMark>?>,
    val onModeStateChange: (KeyboardModeState) -> Unit,
    val sketchSearching: Boolean,
    val onSwitchLanguage: () -> Unit,
    val onChangePosition: ((KeyboardPosition) -> KeyboardPosition) -> Unit,
    val onToggleHideLetters: () -> Unit,
)

internal fun keyboardEnvironment(input: KeyboardEnvironmentInput): KeyboardEnvironment {
    val config = input.config
    val callbacks = keyboardCallbacks(input)
    val keyHeight = config.legendHeight.toFloat()
    val keyWidth = config.legendWidth.toFloat()
    return KeyboardEnvironment(
        lastAction = input.lastAction,
        keyboardSettings = input.keyboardDefinition.settings,
        config = config,
        capsLock = input.modeState.capsLock,
        legendHeight = config.legendHeight,
        legendWidth = config.legendWidth,
        keyHeight = keyHeight,
        keyWidth = keyWidth,
        keyBorderWidth = config.keyBorderWidth / 10.0f,
        keyRadius = (config.keyRadius / 100.0f) * ((keyWidth + keyHeight) / 4.0f),
        callbacks = callbacks,
    )
}

private fun keyboardCallbacks(input: KeyboardEnvironmentInput): KeyboardCallbacks {
    val modeState = input.modeState
    val update: (KeyboardModeState) -> Unit = input.onModeStateChange
    return KeyboardCallbacks(
        onToggleShiftMode = { update(modeState.toggleShiftMode(it)) },
        onToggleCtrlMode = { update(modeState.toggleCtrlMode(it)) },
        onToggleAltMode = { update(modeState.toggleAltMode(it)) },
        onToggleNumericMode = { update(modeState.toggleNumericMode(it)) },
        onToggleEmojiMode = { update(modeState.toggleEmojiMode(it)) },
        onToggleClipboardMode = { update(modeState.toggleClipboardMode(it)) },
        onToggleTranscriptHistoryMode = { update(modeState.toggleTranscriptHistoryMode(it)) },
        onToggleSketchMode = { update(modeState.toggleSketchMode(it, input.sketchSearching)) },
        onToggleCapsLock = { update(modeState.toggleCapsLock()) },
        onToggleCapsLockFromEmojiPicker = { update(modeState.toggleCapsLockFromEmojiPicker()) },
        onToggleHideLetters = input.onToggleHideLetters,
        onAutoCapitalize = { update(modeState.onAutoCapitalize(it)) },
        onSwitchLanguage = {
            input.onSwitchLanguage()
            update(modeState.onLanguageSwitched())
        },
        onChangePosition = input.onChangePosition,
        onKeyEvent = { update(modeState.onKeyAction(it, input.config.switchToLettersAfterSpace)) },
    )
}

internal fun keyboardKeyAt(
    keyboard: KeyboardC?,
    row: Int,
    column: Int,
): KeyItemC? = keyboard?.arr?.getOrNull(row)?.getOrNull(column)

@Composable
internal fun ScreenKey(data: ScreenKeyData) {
    val config = data.environment.config
    val callbacks = data.environment.callbacks
    KeyboardKey(
        key = data.key,
        ghostKey = data.ghostKey,
        lastAction = data.environment.lastAction,
        legendHeight = data.environment.legendHeight,
        legendWidth = data.environment.legendWidth,
        keyHeight = data.environment.keyHeight,
        keyWidth = data.environment.keyWidth,
        keyPadding = config.keyPadding,
        keyBorderWidth = data.environment.keyBorderWidth,
        keyRadius = data.environment.keyRadius,
        autoCapitalize = config.autoCapitalize,
        keyboardSettings = data.environment.keyboardSettings,
        spacebarMultiTaps = config.spacebarMultiTaps,
        vibrateOnTap = config.vibrateOnTap,
        vibrateOnSlide = config.vibrateOnSlide,
        soundOnTap = config.soundOnTap,
        hideLetters = config.hideLetters,
        hideSymbols = config.hideSymbols,
        capsLock = data.environment.capsLock,
        animationSpeed = config.animationSpeed,
        animationHelperSpeed = config.animationHelperSpeed,
        minSwipeLength = config.minSwipeLength,
        slideSensitivity = config.slideSensitivity,
        slideEnabled = config.slideEnabled,
        slideCursorMovementMode = config.slideCursorMovementMode,
        slideSpacebarDeadzoneEnabled = config.slideSpacebarDeadzoneEnabled,
        slideBackspaceDeadzoneEnabled = config.slideBackspaceDeadzoneEnabled,
        onToggleShiftMode = callbacks.onToggleShiftMode,
        onToggleCtrlMode = callbacks.onToggleCtrlMode,
        onToggleAltMode = callbacks.onToggleAltMode,
        onToggleNumericMode = callbacks.onToggleNumericMode,
        onToggleEmojiMode = callbacks.onToggleEmojiMode,
        onToggleClipboardMode = callbacks.onToggleClipboardMode,
        onToggleTranscriptHistoryMode = callbacks.onToggleTranscriptHistoryMode,
        onToggleSketchMode = callbacks.onToggleSketchMode,
        onToggleCapsLock = callbacks.onToggleCapsLock,
        onToggleHideLetters = callbacks.onToggleHideLetters,
        onAutoCapitalize = callbacks.onAutoCapitalize,
        onSwitchLanguage = callbacks.onSwitchLanguage,
        onChangePosition = callbacks.onChangePosition,
        onKeyEvent = callbacks.onKeyEvent,
        oppositeCaseKey = data.oppositeCaseKey,
        numericKey = data.numericKey,
        dragReturnEnabled = config.dragReturnEnabled,
        circularDragEnabled = config.circularDragEnabled,
        clockwiseDragAction = config.clockwiseDragAction,
        counterclockwiseDragAction = config.counterclockwiseDragAction,
        abbreviationBufferEnabled = config.abbreviationBufferEnabled,
        slideHoldEnabled = config.slideHoldEnabled,
        onSketchAction = data.onSketchAction,
    )
}
