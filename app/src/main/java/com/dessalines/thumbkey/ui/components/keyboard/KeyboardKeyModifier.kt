package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.doneKeyAction
import com.dessalines.thumbkey.utils.performKeyAction
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/** Values needed to build the tap and slide input chain for one keyboard key. */
internal data class KeyboardKeyModifierState(
    val interactionSource: MutableInteractionSource,
    val isDragged: MutableState<Boolean>,
    val backgroundColor: Color,
    val borderColor: Color,
    val tapActions: List<KeyAction>,
)

/** Builds the tap and slide input chain for one keyboard key. */
@OptIn(ExperimentalFoundationApi::class)
internal fun keyboardKeyModifier(
    gestureContext: KeyGestureContext,
    state: KeyboardKeyModifierState,
): Modifier {
    val data = gestureContext.data
    val environment = data.environment
    val config = environment.config
    val key = data.key
    val baseModifier =
        Modifier
            .height(environment.keyHeight.dp)
            .width(environment.keyWidth.dp * key.widthMultiplier)
            .padding(config.keyPadding.dp)
            .clip(RoundedCornerShape(environment.keyRadius.dp))
            .then(
                if (environment.keyBorderWidth > 0.0) {
                    Modifier.border(
                        environment.keyBorderWidth.dp,
                        state.borderColor,
                        shape = RoundedCornerShape(environment.keyRadius.dp),
                    )
                } else {
                    Modifier
                },
            ).background(color = state.backgroundColor)

    return keyboardKeyDragModifier(
        keyboardKeyTapModifier(baseModifier, gestureContext, state),
        gestureContext,
        state,
    )
}

@OptIn(ExperimentalFoundationApi::class)
private fun keyboardKeyTapModifier(
    modifier: Modifier,
    gestureContext: KeyGestureContext,
    state: KeyboardKeyModifierState,
): Modifier =
    modifier.combinedClickable(
            interactionSource = state.interactionSource,
            indication = null,
            hapticFeedbackEnabled = false,
            onClick = {
                val action = gestureContext.data.key.center.action
                gestureContext.data.environment.lastAction.value?.let { (lastAction, time) ->
                    if (time.elapsedNow() < 1.seconds && lastAction == action && !gestureContext.ime.didCursorMove()) {
                        gestureContext.state.tapCount += 1
                    } else {
                        gestureContext.state.tapCount = 0
                    }
                }
                gestureContext.data.environment.lastAction.value =
                    Pair(action, TimeSource.Monotonic.markNow())

                val selectedAction = state.tapActions[gestureContext.state.tapCount % state.tapActions.size]
                performKeyAction(selectedAction, gestureContext.keyActionContext)
                doneKeyAction(selectedAction, gestureContext.releaseState)
            },
            onLongClick = {
                gestureContext.data.key.longPress?.let { action ->
                    performKeyAction(action, gestureContext.keyActionContext)
                    doneKeyAction(action, gestureContext.releaseState)
                    gestureContext.feedback.tap()
                }
            },
        )

@OptIn(ExperimentalFoundationApi::class)
private fun keyboardKeyDragModifier(
    modifier: Modifier,
    gestureContext: KeyGestureContext,
    state: KeyboardKeyModifierState,
): Modifier {
    val data = gestureContext.data
    val config = data.environment.config
    val environment = data.environment
    val keySize = (data.environment.keyHeight + data.environment.keyWidth) / 2.0
    return modifier.pointerInput(key1 = keyboardKeyId(data)) {
            detectDragGestures(
                onDragStart = { state.isDragged.value = true },
                onDrag = { change, dragAmount ->
                    change.consume()
                    handleKeyDrag(
                        gestureContext,
                        dragAmount,
                        SlideTriggers(
                            slide = (keySize.dp.toPx() * 0.75f) + config.minSwipeLength,
                            selection = (keySize.dp.toPx() * 1.25f) + config.minSwipeLength,
                        ),
                    )
                },
                onDragEnd = {
                    val state = gestureContext.state
                    val action =
                        handleKeyDragEnd(
                            gestureContext,
                            SwipeKeys(data.key, data.ghostKey, data.oppositeCaseKey, data.numericKey),
                            SwipeGesture(state.positions, state.maxOffset, state.offsetX, state.offsetY),
                        )
                    state.tapCount = 0
                    environment.lastAction.value = Pair(action, TimeSource.Monotonic.markNow())
                    state.resetDrag()
                },
            )
        }
}
