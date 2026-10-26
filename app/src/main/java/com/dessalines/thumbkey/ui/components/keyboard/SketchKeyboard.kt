package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardMode
import com.dessalines.thumbkey.utils.isLeftHandedLayout
import com.dessalines.thumbkey.utils.keyboardPositionToAlignment

internal data class SketchKeyboardData(
    val ime: IMEService,
    val state: SketchState,
    val rowCount: Int,
    val modeState: KeyboardModeState,
    val onModeStateChange: (KeyboardModeState) -> Unit,
)

@Composable
internal fun SketchKeyboard(
    data: SketchKeyboardData,
    environment: KeyboardEnvironment,
) {
    val config = environment.config
    val keyEnvironment = sketchKeyboardEnvironment(data, environment)
    KeyboardFrame(KeyboardFrameStyle(config.backdropEnabled, MaterialTheme.colorScheme.background)) {
        Box(
            contentAlignment = keyboardPositionToAlignment(config.position),
            modifier =
                Modifier
                    .keyboardBottomInset(config.ignoreBottomPadding, config.pushupSize.dp)
                    .fillMaxWidth()
                    .keyboardBackdropPadding(config.backdropEnabled, 6.dp),
        ) {
            SketchScreen(
                state = data.state,
                rowCount = data.rowCount,
                leftHanded = isLeftHandedLayout(config.layout.name),
                keyWidth = environment.keyWidth,
                keyHeight = environment.keyHeight,
                keyPadding = config.keyPadding,
                keyBorderWidth = environment.keyBorderWidth,
                cornerRadius = environment.keyRadius,
                vibrateOnTap = config.vibrateOnTap,
                soundOnTap = config.soundOnTap,
                onCommit = { commitSketchText(data.ime, it) },
                onSearch = {
                    data.state.searching = true
                    data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.MAIN))
                },
                key = { key, onSketchAction -> SketchKey(key, onSketchAction, keyEnvironment) },
            )
        }
    }
}

private fun sketchKeyboardEnvironment(
    data: SketchKeyboardData,
    environment: KeyboardEnvironment,
): KeyboardEnvironment {
    val callbacks = environment.callbacks
    return environment.copy(
        config = environment.config.copy(hideLetters = false, hideSymbols = false),
        capsLock = false,
        callbacks =
            callbacks.copy(
                onToggleShiftMode = {},
                onToggleCtrlMode = {},
                onToggleAltMode = {},
                onToggleNumericMode = {},
                onToggleEmojiMode = {},
                onToggleClipboardMode = {},
                onToggleTranscriptHistoryMode = {},
                onToggleSketchMode = { enable ->
                    if (!enable) data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.MAIN))
                },
                onToggleCapsLock = {},
                onAutoCapitalize = {},
                onKeyEvent = {},
            ),
    )
}

private fun commitSketchText(
    ime: IMEService,
    text: String,
) {
    ime.ignoreNextCursorMove()
    ime.appInputConnection.commitText(text, 1)
}

@Composable
private fun SketchKey(
    key: KeyItemC,
    onSketchAction: (KeyAction.Sketch) -> Unit,
    environment: KeyboardEnvironment,
) {
    ScreenKey(
        ScreenKeyData(
            key = key,
            environment = environment,
            onSketchAction = onSketchAction,
        ),
    )
}
