package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.ui.theme.colorVariantToColor
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyActionReleaseState
import com.dessalines.thumbkey.utils.ReleasedKeyFeedback
import com.dessalines.thumbkey.utils.buildTapActions
import com.dessalines.thumbkey.utils.isPasswordField

internal fun keyboardKeyId(data: ScreenKeyData): String {
    val config = data.environment.config
    return data.key.toString() + data.ghostKey.toString() + config.animationHelperSpeed + config.animationSpeed +
        config.keyAnimationType.ordinal +
        config.autoCapitalize +
        config.vibrateOnTap + config.vibrateOnSlide + config.soundOnTap + data.environment.legendHeight + data.environment.legendWidth +
        config.minSwipeLength +
        config.slideSensitivity + config.slideEnabled + config.slideCursorMovementMode + config.slideSpacebarDeadzoneEnabled +
        config.slideBackspaceDeadzoneEnabled + config.dragReturnEnabled + config.circularDragEnabled +
        config.clockwiseDragAction.ordinal + config.counterclockwiseDragAction.ordinal + config.slideHoldEnabled
}

@Composable
internal fun KeyboardKey(data: ScreenKeyData) {
    val environment = data.environment
    val config = environment.config
    val key = data.key
    val state = rememberKeyboardKeyState(data)

    LaunchedEffect(key1 = state.isPressed) {
        if (state.isPressed) state.gestureContext.feedback.tap()
    }

    val backgroundColor =
        if (!(state.isDragged.value || state.isPressed)) {
            colorVariantToColor(colorVariant = key.backgroundColor)
        } else {
            MaterialTheme.colorScheme.inversePrimary
        }
    val keySize = (environment.keyHeight + environment.keyWidth) / 2.0
    val keyModifier =
        keyboardKeyModifier(
            state.gestureContext,
            KeyboardKeyModifierState(
                state.interactionSource,
                state.isDragged,
                backgroundColor,
                MaterialTheme.colorScheme.outline,
                state.tapActions,
            ),
        )
    val legendSizes = KeyLegendSizes(keySize.dp, environment.keyBorderWidth.dp, environment.keyRadius.dp)
    val legendDisplay = KeyLegendDisplay(config.hideLetters, config.hideSymbols, environment.capsLock)

    Box(modifier = keyModifier) {
        KeyLegends(key, legendSizes, legendDisplay)
        KeyReleaseAnimation(
            feedback = state.releasedFeedback.value,
            isPasswordField = state.isPasswordField,
            config =
                KeyReleaseAnimationConfig(
                    type = config.keyAnimationType,
                    speed = config.animationSpeed,
                    helperSpeed = config.animationHelperSpeed,
                    keySize = keySize,
                ),
        )
    }
}

/** Snapshot-backed dependencies shared by the key's renderer and input modifiers. */
private data class KeyboardKeyState(
    val isPasswordField: Boolean,
    val interactionSource: MutableInteractionSource,
    val isPressed: Boolean,
    val isDragged: MutableState<Boolean>,
    val releasedFeedback: MutableState<ReleasedKeyFeedback?>,
    val gestureContext: KeyGestureContext,
    val tapActions: List<KeyAction>,
)

@Composable
private fun rememberKeyboardKeyState(data: ScreenKeyData): KeyboardKeyState {
    val context = LocalContext.current
    val ime = context as IMEService
    val config = data.environment.config
    val scope = rememberCoroutineScope()
    val isPasswordField by remember { mutableStateOf(isPasswordField(ime)) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged = remember { mutableStateOf(false) }
    val releasedFeedback = remember { mutableStateOf<ReleasedKeyFeedback?>(null) }
    val releaseState = KeyActionReleaseState(isDragged, releasedFeedback)
    val gestureState = remember { KeyGestureState() }
    val key = data.key
    val tapActions = if (config.spacebarMultiTaps) buildTapActions(key) else listOf(key.center.action)
    val feedback =
        KeyFeedback(
            LocalView.current,
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
            config,
        )
    val gestureContext =
        KeyGestureContext(
            data,
            ime,
            keyActionContextFor(data, ime),
            releaseState,
            gestureState,
            feedback,
            scope,
        )

    return KeyboardKeyState(isPasswordField, interactionSource, isPressed, isDragged, releasedFeedback, gestureContext, tapActions)
}
