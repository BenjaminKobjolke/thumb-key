package com.dessalines.thumbkey.ui.components.keyboard

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.MAX_KEYBOARD_OPACITY
import com.dessalines.thumbkey.db.MIN_KEYBOARD_OPACITY
import com.dessalines.thumbkey.utils.FloatingHandlePosition
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private const val SNAP_EDGE_THRESHOLD_DP = 28f
private const val FLING_VELOCITY_THRESHOLD_DP_PER_SEC = 550f
internal const val SNAP_ANIMATION_MS = 160

data class FloatingKeyboardFrameConfig(
    val keyWidth: Float,
    val keyHeight: Float,
    val columns: Int,
    val keyboardOpacity: Int,
    val handlePosition: FloatingHandlePosition,
    val floatingXFraction: Float,
    val floatingYFraction: Float,
    val vibrateOnTap: Boolean,
    val onMoved: (Float, Float) -> Unit,
    val onBounds: (IntRect) -> Unit,
)

@Composable
fun FloatingKeyboardFrame(
    config: FloatingKeyboardFrameConfig,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable (keyWidth: Float, keyHeight: Float) -> Unit,
) {
    val columns = config.columns
    val floatingXFraction = config.floatingXFraction
    val floatingYFraction = config.floatingYFraction
    val vibrateOnTap = config.vibrateOnTap
    val onMoved = config.onMoved
    val onBounds = config.onBounds
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

    val storedPosition = Offset(floatingXFraction.coerceIn(0f, 1f), floatingYFraction.coerceIn(0f, 1f))
    LaunchedEffect(storedPosition) {
        val pending = gesture.pendingPosition
        when {
            pending != null && abs(pending.x - storedPosition.x) < 0.002f && abs(pending.y - storedPosition.y) < 0.002f -> {
                gesture.pendingPosition = null
            }

            pending == null && !gesture.active && !gesture.animating -> {
                dragPin.value = null
            }
        }
    }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInWindow()
                    gesture.frameOriginInWindow = IntOffset(pos.x.roundToInt(), pos.y.roundToInt())
                },
    ) {
        val boxWidthPx = constraints.maxWidth
        val boxHeightPx = constraints.maxHeight
        val boxWidthDp = with(density) { boxWidthPx.toDp().value }.coerceAtLeast(1f)
        val safeColumns = columns.coerceAtLeast(1)
        val maxKeyWidthDp = (boxWidthDp / safeColumns).coerceAtLeast(1f)
        val effectiveKeyWidth = config.keyWidth.coerceIn(1f, maxKeyWidthDp)
        val effectiveKeyHeight = config.keyHeight.coerceAtLeast(1f)
        val panelWidthDp = (effectiveKeyWidth * safeColumns).coerceAtMost(boxWidthDp)
        val panelWidthPx = with(density) { panelWidthDp.dp.roundToPx() }.coerceIn(1, boxWidthPx.coerceAtLeast(1))
        val snapThresholdPx = with(density) { SNAP_EDGE_THRESHOLD_DP.dp.toPx() }
        val flingVelocityThresholdPx = with(density) { FLING_VELOCITY_THRESHOLD_DP_PER_SEC.dp.toPx() }

        val controller =
            FloatingKeyboardGestureController(
                gesture = gesture,
                dragPin = dragPin,
                dragShift = dragShift,
                boxWidthPx = boxWidthPx,
                boxHeightPx = boxHeightPx,
                panelWidthPx = panelWidthPx,
                storedPosition = storedPosition,
                coroutineScope = coroutineScope,
                onMoved = onMoved,
                onBounds = onBounds,
            )
        FloatingSnapIndicator(FloatingSnapIndicatorState(snapIndicatorEdge.value, gesture, controller, dragShift.value, density))

        val dragState = FloatingKeyboardDragState(
            controller = controller,
            gesture = gesture,
            dragShift = dragShift,
            snapIndicatorEdge = snapIndicatorEdge,
            isDragging = isDragging,
            isHandlePressed = isHandlePressed,
            lastActivityTick = lastActivityTick,
            snapThresholds = FloatingSnapThresholds(snapThresholdPx, flingVelocityThresholdPx),
            vibrateOnTap = vibrateOnTap,
            view = view,
            slideHapticConstant = slideHapticConstant,
        )
        FloatingKeyboardPanel(
            FloatingKeyboardPanelContent(
                frameConfig = config,
                dragState = dragState,
                panelWidthPx = panelWidthPx,
                panelWidthDp = panelWidthDp,
                keyWidth = effectiveKeyWidth,
                keyHeight = effectiveKeyHeight,
                overlay = overlay,
                content = content,
            ),
        )
    }
}
