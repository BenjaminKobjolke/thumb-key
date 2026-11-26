package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

internal data class FloatingPin(
    val offset: Offset,
    val boxWidthPx: Int,
    val panelWidthPx: Int,
)

internal data class FloatingOffsetAnimation(
    val base: Offset,
    val startShift: Offset,
    val target: Offset,
)

internal class FloatingGestureState {
    var active = false
    var animating = false
    var panelSize = IntSize.Zero
    var frameOriginInWindow = IntOffset.Zero
    var bounds: IntRect? = null
    var pendingPosition: Offset? = null
    var snapJob: Job? = null
}

internal class FloatingKeyboardGestureController(
    private val gesture: FloatingGestureState,
    private val dragPin: MutableState<FloatingPin?>,
    val dragShift: MutableState<Offset>,
    private val boxWidthPx: Int,
    private val boxHeightPx: Int,
    private val panelWidthPx: Int,
    private val storedPosition: Offset,
    private val coroutineScope: CoroutineScope,
    private val onMoved: (Float, Float) -> Unit,
    private val onBounds: (IntRect) -> Unit,
) {
    fun slackX(panelWidth: Int = gesture.panelSize.width): Float = (boxWidthPx - panelWidth).coerceAtLeast(0).toFloat()

    fun slackY(panelHeight: Int = gesture.panelSize.height): Float = (boxHeightPx - panelHeight).coerceAtLeast(0).toFloat()

    fun pinned(): Offset? =
        dragPin.value
            ?.takeIf { it.boxWidthPx == boxWidthPx && it.panelWidthPx == panelWidthPx }
            ?.offset

    fun pin(offset: Offset) {
        dragPin.value = FloatingPin(offset, boxWidthPx, panelWidthPx)
    }

    fun currentOffset(
        panelWidth: Int = gesture.panelSize.width,
        panelHeight: Int = gesture.panelSize.height,
    ): Offset {
        val sx = slackX(panelWidth)
        val sy = slackY(panelHeight)
        val raw = pinned() ?: Offset(storedPosition.x * sx, storedPosition.y * sy)
        return Offset(raw.x.coerceIn(0f, sx), raw.y.coerceIn(0f, sy))
    }

    fun publishBounds() {
        gesture.bounds?.let(onBounds)
    }

    private fun persistPosition(
        end: Offset,
        sx: Float,
        sy: Float,
    ) {
        val moved =
            Offset(
                if (sx > 0f) (end.x / sx).coerceIn(0f, 1f) else 0.5f,
                if (sy > 0f) (end.y / sy).coerceIn(0f, 1f) else 1.0f,
            )
        if (moved != storedPosition) gesture.pendingPosition = moved
        onMoved(moved.x, moved.y)
    }

    private fun settleAt(
        target: Offset,
        sx: Float,
        sy: Float,
    ) {
        pin(target)
        dragShift.value = Offset.Zero
        gesture.bounds = IntRect(gesture.frameOriginInWindow + IntOffset(target.x.roundToInt(), target.y.roundToInt()), gesture.panelSize)
        publishBounds()
        persistPosition(target, sx, sy)
    }

    fun animateToOffset(
        animation: FloatingOffsetAnimation,
        slack: Offset,
    ) {
        val (base, startShift, target) = animation
        if ((target - (base + startShift)).getDistance() < 1f) {
            settleAt(target, slack.x, slack.y)
            return
        }
        gesture.snapJob?.cancel()
        gesture.snapJob =
            coroutineScope.launch {
                gesture.animating = true
                try {
                    val anim = Animatable(0f)
                    anim.animateTo(1f, tween(durationMillis = SNAP_ANIMATION_MS, easing = FastOutSlowInEasing)) {
                        val fraction = value
                        dragShift.value =
                            Offset(
                                startShift.x + (target.x - base.x - startShift.x) * fraction,
                                startShift.y + (target.y - base.y - startShift.y) * fraction,
                            )
                    }
                    settleAt(target, slack.x, slack.y)
                } finally {
                    gesture.animating = false
                }
            }
    }

    fun flipSide(onVibrate: () -> Unit) {
        if (gesture.active) return
        val sx = slackX()
        val sy = slackY()
        if (sx <= 0f) return
        gesture.snapJob?.cancel()
        val currentPos = currentOffset() + dragShift.value
        val clamped = Offset(currentPos.x.coerceIn(0f, sx), currentPos.y.coerceIn(0f, sy))
        val targetX = resolveFlipTargetX(clamped.x, sx)
        pin(clamped)
        dragShift.value = Offset.Zero
        onVibrate()
        animateToOffset(
            FloatingOffsetAnimation(clamped, Offset.Zero, Offset(targetX, clamped.y)),
            Offset(sx, sy),
        )
    }
}
