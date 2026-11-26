package com.dessalines.thumbkey.ui.components.keyboard

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset

internal data class FloatingKeyboardDragState(
    val controller: FloatingKeyboardGestureController,
    val gesture: FloatingGestureState,
    val dragShift: MutableState<Offset>,
    val snapIndicatorEdge: MutableState<FloatingSnapEdge?>,
    val isDragging: MutableState<Boolean>,
    val isHandlePressed: MutableState<Boolean>,
    val lastActivityTick: MutableIntState,
    val snapThresholds: FloatingSnapThresholds,
    val vibrateOnTap: Boolean,
    val view: View,
    val slideHapticConstant: Int,
)

internal fun floatingKeyboardDragCallbacks(state: FloatingKeyboardDragState): FloatingDragHandleCallbacks {
    val controller = state.controller
    val gesture = state.gesture
    val dragShift = state.dragShift
    return FloatingDragHandleCallbacks(
        onPressChanged = { pressed ->
            state.isHandlePressed.value = pressed
            if (pressed) state.lastActivityTick.intValue += 1
        },
        onDoubleTap = {
            controller.flipSide {
                if (state.vibrateOnTap) state.view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        },
        onDragStart = {
            gesture.snapJob?.cancel()
            gesture.snapJob = null
            gesture.animating = false
            val current = controller.currentOffset() + dragShift.value
            val clamped = Offset(current.x.coerceIn(0f, controller.slackX()), current.y.coerceIn(0f, controller.slackY()))
            controller.pin(clamped)
            dragShift.value = Offset.Zero
            gesture.active = true
            state.isDragging.value = true
        },
        onDragBy = { delta ->
            val base = controller.pinned() ?: return@FloatingDragHandleCallbacks
            val sx = controller.slackX()
            val sy = controller.slackY()
            val shift = dragShift.value
            val nextX = (base.x + shift.x + delta.x).coerceIn(0f, sx)
            val nextY = (base.y + shift.y + delta.y).coerceIn(0f, sy)
            dragShift.value = Offset(nextX - base.x, nextY - base.y)
            val edge = when {
                sx <= 0f -> null
                nextX <= state.snapThresholds.snapPx -> FloatingSnapEdge.LEFT
                sx - nextX <= state.snapThresholds.snapPx -> FloatingSnapEdge.RIGHT
                else -> null
            }
            if (edge != state.snapIndicatorEdge.value) {
                if (edge != null && state.vibrateOnTap) state.view.performHapticFeedback(state.slideHapticConstant)
                state.snapIndicatorEdge.value = edge
            }
        },
        onDragEnd = { velocity ->
            gesture.active = false
            state.isDragging.value = false
            state.snapIndicatorEdge.value = null
            val base = controller.pinned() ?: return@FloatingDragHandleCallbacks
            val shift = dragShift.value
            val sx = controller.slackX()
            val sy = controller.slackY()
            val raw = Offset((base.x + shift.x).coerceIn(0f, sx), (base.y + shift.y).coerceIn(0f, sy))
            val (targetX, snappedEdge) = resolveSnapTargetX(raw, sx, velocity, state.snapThresholds)
            if (snappedEdge != null && state.vibrateOnTap) {
                state.view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
            controller.animateToOffset(
                FloatingOffsetAnimation(base, shift, Offset(targetX, raw.y)),
                Offset(sx, sy),
            )
        },
    )
}
