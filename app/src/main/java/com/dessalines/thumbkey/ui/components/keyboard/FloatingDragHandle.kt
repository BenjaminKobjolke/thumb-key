package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.IntState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.MAX_KEYBOARD_OPACITY
import com.dessalines.thumbkey.db.MIN_KEYBOARD_OPACITY
import com.dessalines.thumbkey.utils.FloatingHandlePosition
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


private const val HANDLE_IDLE_TIMEOUT_MS = 2500L
private const val HANDLE_ALPHA_IDLE = 0.15f
private const val HANDLE_ALPHA_ACTIVE = 0.55f
private const val HANDLE_ALPHA_HELD = 1.0f
private const val HANDLE_HIGHLIGHTED_MAX_WIDTH_DP = 54f
private const val HANDLE_HIGHLIGHTED_MIN_WIDTH_DP = 20f
private const val HANDLE_HIGHLIGHTED_WIDTH_RATIO = 0.85f
private const val HANDLE_IDLE_MAX_WIDTH_DP = 44f
private const val HANDLE_IDLE_MIN_WIDTH_DP = 16f
private const val HANDLE_IDLE_WIDTH_RATIO = 0.65f
internal val HANDLE_TOUCH_HEIGHT = 22.dp
private val HANDLE_PILL_GAP = 4.dp
private val HANDLE_PILL_HEIGHT = 4.dp

internal data class FloatingDragHandleCallbacks(
    val onPressChanged: (Boolean) -> Unit,
    val onDoubleTap: () -> Unit,
    val onDragStart: () -> Unit,
    val onDragBy: (Offset) -> Unit,
    val onDragEnd: (Offset) -> Unit,
)

internal data class FloatingDragHandleActivity(
    val isPressed: Boolean,
    val isDragging: Boolean,
    val activityTick: IntState,
)

@Composable
private fun rememberKeyboardActive(activity: FloatingDragHandleActivity): androidx.compose.runtime.State<Boolean> {
    var isActive by remember { mutableStateOf(true) }
    LaunchedEffect(activity.activityTick, activity.isPressed, activity.isDragging) {
        snapshotFlow { Triple(activity.activityTick.intValue, activity.isPressed, activity.isDragging) }
            .collectLatest { (_, pressed, dragging) ->
                isActive = true
                if (!pressed && !dragging) {
                    delay(HANDLE_IDLE_TIMEOUT_MS)
                    isActive = false
                }
            }
    }
    return rememberUpdatedState(isActive)
}

private fun floatingHandleTargetAlpha(highlighted: Boolean, active: Boolean): Float =
    when {
        highlighted -> HANDLE_ALPHA_HELD
        active -> HANDLE_ALPHA_ACTIVE
        else -> HANDLE_ALPHA_IDLE
    }

private fun floatingHandleTargetWidth(panelWidthDp: Float, highlighted: Boolean): Float {
    val maxWidth = if (highlighted) HANDLE_HIGHLIGHTED_MAX_WIDTH_DP else HANDLE_IDLE_MAX_WIDTH_DP
    val widthRatio = if (highlighted) HANDLE_HIGHLIGHTED_WIDTH_RATIO else HANDLE_IDLE_WIDTH_RATIO
    val minWidth = if (highlighted) HANDLE_HIGHLIGHTED_MIN_WIDTH_DP else HANDLE_IDLE_MIN_WIDTH_DP
    return minOf(maxWidth, (panelWidthDp * widthRatio).coerceAtLeast(minWidth)).coerceAtMost(panelWidthDp)
}

@Composable
private fun FloatingHandlePill(atTop: Boolean, width: androidx.compose.ui.unit.Dp, alpha: Float, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .padding(
                top = if (atTop) 0.dp else HANDLE_PILL_GAP,
                bottom = if (atTop) HANDLE_PILL_GAP else 0.dp,
            )
            .width(width)
            .height(HANDLE_PILL_HEIGHT)
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(HANDLE_PILL_HEIGHT / 2))
            .background(color),
    )
}

private fun Modifier.floatingDragGesture(callbacks: FloatingDragHandleCallbacks): Modifier =
    pointerInput(Unit) {
        val doubleTapTimeout = viewConfiguration.doubleTapTimeoutMillis
        var lastTapUpTime = 0L
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            callbacks.onPressChanged(true)
            try {
                val velocityTracker = VelocityTracker()
                var cumulative = Offset.Zero
                var dragStarted = false
                var pointerCancelled = false
                val afterSlop = awaitTouchSlopOrCancellation(down.id) { change, over ->
                    change.consume()
                    if (!dragStarted) {
                        dragStarted = true
                        lastTapUpTime = 0L
                        callbacks.onDragStart()
                    }
                    cumulative += over
                    velocityTracker.addPosition(change.uptimeMillis, cumulative)
                    callbacks.onDragBy(over)
                }
                if (dragStarted) {
                    if (afterSlop != null) {
                        val finished = drag(afterSlop.id) { change ->
                            val delta = change.positionChange()
                            change.consume()
                            cumulative += delta
                            velocityTracker.addPosition(change.uptimeMillis, cumulative)
                            callbacks.onDragBy(delta)
                        }
                        if (!finished) pointerCancelled = true
                    }
                    val velocity = if (pointerCancelled) Offset.Zero else velocityTracker.calculateVelocity().let { Offset(it.x, it.y) }
                    callbacks.onDragEnd(velocity)
                } else if (afterSlop == null) {
                    val upChange = currentEvent.changes.firstOrNull { it.id == down.id }
                    if (upChange != null && !upChange.pressed) {
                        val now = upChange.uptimeMillis
                        if (lastTapUpTime != 0L && now - lastTapUpTime <= doubleTapTimeout) {
                            lastTapUpTime = 0L
                            callbacks.onDoubleTap()
                        } else {
                            lastTapUpTime = now
                        }
                    }
                }
            } finally {
                callbacks.onPressChanged(false)
            }
        }
    }

@Composable
internal fun FloatingDragHandle(
    atTop: Boolean,
    panelWidthDp: Float,
    activity: FloatingDragHandleActivity,
    callbacks: FloatingDragHandleCallbacks,
) {
    val latestCallbacks by rememberUpdatedState(callbacks)
    val isKeyboardActive by rememberKeyboardActive(activity)
    val highlighted = activity.isPressed || activity.isDragging
    val targetAlpha = floatingHandleTargetAlpha(highlighted, isKeyboardActive)
    val pillAlpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = if (highlighted || isKeyboardActive) 150 else 400),
        label = "floatingHandleAlpha",
    )
    val targetWidthDp = floatingHandleTargetWidth(panelWidthDp, highlighted)
    val pillWidth by animateDpAsState(
        targetValue = targetWidthDp.dp,
        animationSpec = tween(durationMillis = 150),
        label = "floatingHandleWidth",
    )
    val pillColor by animateColorAsState(
        targetValue = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(durationMillis = 150),
        label = "floatingHandleColor",
    )
    val dragDescription = stringResource(R.string.floating_drag_handle)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(HANDLE_TOUCH_HEIGHT)
                .semantics { contentDescription = dragDescription }
                .floatingDragGesture(latestCallbacks),
        contentAlignment = if (atTop) Alignment.BottomCenter else Alignment.TopCenter,
    ) {
        FloatingHandlePill(atTop, pillWidth, pillAlpha, pillColor)
    }
}
