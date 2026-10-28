package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.ui.theme.colorVariantToColor
import com.dessalines.thumbkey.utils.KeyActionReleaseState
import com.dessalines.thumbkey.utils.SlideType
import com.dessalines.thumbkey.utils.buildTapActions
import com.dessalines.thumbkey.utils.doneKeyAction
import com.dessalines.thumbkey.utils.isPasswordField
import com.dessalines.thumbkey.utils.performKeyAction
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

internal fun keyboardKeyId(data: ScreenKeyData): String {
    val config = data.environment.config
    return data.key.toString() + data.ghostKey.toString() + config.animationHelperSpeed + config.animationSpeed + config.autoCapitalize +
        config.vibrateOnTap + config.vibrateOnSlide + config.soundOnTap + data.environment.legendHeight + data.environment.legendWidth +
        config.minSwipeLength +
        config.slideSensitivity + config.slideEnabled + config.slideCursorMovementMode + config.slideSpacebarDeadzoneEnabled +
        config.slideBackspaceDeadzoneEnabled + config.dragReturnEnabled + config.circularDragEnabled +
        config.clockwiseDragAction.ordinal + config.counterclockwiseDragAction.ordinal + config.slideHoldEnabled
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun KeyboardKey(data: ScreenKeyData) {
    val environment = data.environment
    val config = environment.config
    val key = data.key
    val ghostKey = data.ghostKey
    val oppositeCaseKey = data.oppositeCaseKey
    val numericKey = data.numericKey
    val lastAction = environment.lastAction
    val capsLock = environment.capsLock
    val keyHeight = environment.keyHeight
    val keyWidth = environment.keyWidth
    val keyBorderWidth = environment.keyBorderWidth
    val keyRadius = environment.keyRadius
    val animationHelperSpeed = config.animationHelperSpeed
    val animationSpeed = config.animationSpeed
    val spacebarMultiTaps = config.spacebarMultiTaps
    val hideLetters = config.hideLetters
    val hideSymbols = config.hideSymbols
    val keyPadding = config.keyPadding
    val minSwipeLength = config.minSwipeLength
    val slideHoldEnabled = config.slideHoldEnabled

    val ctx = LocalContext.current
    val ime = ctx as IMEService
    val id = keyboardKeyId(data)
    val scope = rememberCoroutineScope()

    // Don't show animations for password fields
    val isPasswordField by remember { mutableStateOf(isPasswordField(ime)) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val isDragged = remember { mutableStateOf(false) }
    val releasedKey = remember { mutableStateOf<String?>(null) }
    val releaseState = KeyActionReleaseState(scope, isDragged, releasedKey, animationHelperSpeed)

    val gestureState = remember { KeyGestureState() }
    val tapActions =
        if (spacebarMultiTaps) {
            buildTapActions(key)
        } else {
            listOf(key.center.action)
        }

    var tapCount by gestureState::tapCount
    var positions by gestureState::positions
    var maxOffset by gestureState::maxOffset

    val backgroundColor =
        if (!(isDragged.value || isPressed)) {
            colorVariantToColor(colorVariant = key.backgroundColor)
        } else {
            MaterialTheme.colorScheme.inversePrimary
        }

    val keyBorderColour = MaterialTheme.colorScheme.outline
    val keySize = (keyHeight + keyWidth) / 2.0
    val view = LocalView.current
    val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val feedback = KeyFeedback(view, audioManager, config)
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
    val keyActionContext = gestureContext.keyActionContext

    LaunchedEffect(key1 = isPressed) {
        if (isPressed) feedback.tap()
    }

    val keyboardKeyModifier =
        Modifier
            .height(keyHeight.dp)
            .width(keyWidth.dp * key.widthMultiplier)
            .padding(keyPadding.dp)
            .clip(RoundedCornerShape(keyRadius.dp))
            .then(
                if (keyBorderWidth > 0.0) {
                    Modifier.border(
                        keyBorderWidth.dp,
                        keyBorderColour,
                        shape = RoundedCornerShape(keyRadius.dp),
                    )
                } else {
                    (Modifier)
                },
            ).background(color = backgroundColor)
            // Note: pointerInput has a delay when switching keyboards, so you must use this
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                hapticFeedbackEnabled = false, // We manually trigger haptics instead
                onClick = {
                    // Set the last key info, and the tap count
                    val cAction = key.center.action
                    lastAction.value?.let { (lastAction, time) ->
                        if (time.elapsedNow() < 1.seconds && lastAction == cAction && !ime.didCursorMove()) {
                            tapCount += 1
                        } else {
                            tapCount = 0
                        }
                    }
                    lastAction.value = Pair(cAction, TimeSource.Monotonic.markNow())

                    // Set the correct action
                    val action = tapActions[tapCount % tapActions.size]
                    performKeyAction(action, keyActionContext)
                    doneKeyAction(action, releaseState)
                },
                onLongClick = {
                    key.longPress?.let { action ->
                        performKeyAction(action, keyActionContext)
                        doneKeyAction(action, releaseState)
                        feedback.tap()
                    }
                },
            )
            // The key1 is necessary, otherwise new swipes wont work
            .pointerInput(key1 = id) {
                detectDragGestures(
                    onDragStart = {
                        isDragged.value = true
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        handleKeyDrag(
                            gestureContext,
                            dragAmount,
                            SlideTriggers(
                                slide = (keySize.dp.toPx() * 0.75f) + minSwipeLength,
                                selection = (keySize.dp.toPx() * 1.25f) + minSwipeLength,
                            ),
                        )
                    },
                    onDragEnd = {
                        val action =
                            handleKeyDragEnd(
                                gestureContext,
                                SwipeKeys(key, ghostKey, oppositeCaseKey, numericKey),
                                SwipeGesture(positions, maxOffset, gestureState.offsetX, gestureState.offsetY),
                            )
                        tapCount = 0
                        lastAction.value = Pair(action, TimeSource.Monotonic.markNow())
                        gestureState.resetDrag()
                    },
                )
            }

    val legendSizes = KeyLegendSizes(keySize.dp, keyBorderWidth.dp, keyRadius.dp)
    val legendDisplay = KeyLegendDisplay(hideLetters, hideSymbols, capsLock)

    Box(modifier = keyboardKeyModifier) {
        KeyLegends(key, legendSizes, legendDisplay)
        KeyReleaseAnimation(releasedKey.value, isPasswordField, animationSpeed, keySize.dp)
    }
}
