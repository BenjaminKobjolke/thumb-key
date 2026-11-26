package com.dessalines.thumbkey.ui.components.keyboard

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import kotlin.math.abs

internal data class FloatingKeyboardFrameState(
    val gesture: FloatingGestureState,
    val dragPin: MutableState<FloatingPin?>,
    val dragShift: MutableState<Offset>,
    val snapIndicatorEdge: MutableState<FloatingSnapEdge?>,
    val isDragging: MutableState<Boolean>,
    val isHandlePressed: MutableState<Boolean>,
    val lastActivityTick: MutableIntState,
    val storedPosition: Offset,
    val density: Density,
    val view: View,
    val coroutineScope: CoroutineScope,
    val slideHapticConstant: Int,
)

@Composable
internal fun rememberFloatingKeyboardFrameState(config: FloatingKeyboardFrameConfig): FloatingKeyboardFrameState {
    val density = LocalDensity.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    val slideHapticConstant =
        if (Build.VERSION.SDK_INT >= 27) {
            HapticFeedbackConstants.TEXT_HANDLE_MOVE
        } else {
            HapticFeedbackConstants.KEYBOARD_TAP
        }

    val gesture = remember { FloatingGestureState() }
    val dragPin = remember { mutableStateOf<FloatingPin?>(null) }
    val dragShift = remember { mutableStateOf(Offset.Zero) }
    val snapIndicatorEdge = remember { mutableStateOf<FloatingSnapEdge?>(null) }
    val isDragging = remember { mutableStateOf(false) }
    val isHandlePressed = remember { mutableStateOf(false) }
    val lastActivityTick = remember { mutableIntStateOf(0) }
    val storedPosition =
        Offset(
            config.floatingXFraction.coerceIn(0f, 1f),
            config.floatingYFraction.coerceIn(0f, 1f),
        )

    LaunchedEffect(storedPosition) {
        val pending = gesture.pendingPosition
        when {
            pending != null &&
                abs(pending.x - storedPosition.x) < 0.002f &&
                abs(pending.y - storedPosition.y) < 0.002f -> {
                gesture.pendingPosition = null
            }

            pending == null && !gesture.active && !gesture.animating -> {
                dragPin.value = null
            }
        }
    }

    return FloatingKeyboardFrameState(
        gesture = gesture,
        dragPin = dragPin,
        dragShift = dragShift,
        snapIndicatorEdge = snapIndicatorEdge,
        isDragging = isDragging,
        isHandlePressed = isHandlePressed,
        lastActivityTick = lastActivityTick,
        storedPosition = storedPosition,
        density = density,
        view = view,
        coroutineScope = coroutineScope,
        slideHapticConstant = slideHapticConstant,
    )
}
