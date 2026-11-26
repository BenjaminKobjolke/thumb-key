package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private const val SNAP_EDGE_THRESHOLD_DP = 28f
private const val FLING_VELOCITY_THRESHOLD_DP_PER_SEC = 550f
internal const val SNAP_ANIMATION_MS = 160

internal data class FloatingKeyboardFrameLayoutInput(
    val config: FloatingKeyboardFrameConfig,
    val state: FloatingKeyboardFrameState,
    val boxWidthPx: Int,
    val boxHeightPx: Int,
)

private data class FloatingKeyboardFrameDimensions(
    val panelWidthPx: Int,
    val panelWidthDp: Float,
    val keyWidth: Float,
    val keyHeight: Float,
    val snapThresholdPx: Float,
    val flingVelocityThresholdPx: Float,
)

@Composable
internal fun BoxScope.FloatingKeyboardFrameLayout(
    input: FloatingKeyboardFrameLayoutInput,
    overlay: @Composable BoxScope.() -> Unit,
    content: @Composable (keyWidth: Float, keyHeight: Float) -> Unit,
) {
    val config = input.config
    val state = input.state
    val dimensions = floatingKeyboardFrameDimensions(config, state, input.boxWidthPx)
    val controller =
        FloatingKeyboardGestureController(
            gesture = state.gesture,
            dragPin = state.dragPin,
            dragShift = state.dragShift,
            boxWidthPx = input.boxWidthPx,
            boxHeightPx = input.boxHeightPx,
            panelWidthPx = dimensions.panelWidthPx,
            storedPosition = state.storedPosition,
            coroutineScope = state.coroutineScope,
            onMoved = config.onMoved,
            onBounds = config.onBounds,
        )
    FloatingSnapIndicator(
        FloatingSnapIndicatorState(
            state.snapIndicatorEdge.value,
            state.gesture,
            controller,
            state.dragShift.value,
            state.density,
        ),
    )

    FloatingKeyboardPanel(
        FloatingKeyboardPanelContent(
            frameConfig = config,
            dragState = createFloatingKeyboardDragState(config, state, controller, dimensions),
            panelWidthPx = dimensions.panelWidthPx,
            panelWidthDp = dimensions.panelWidthDp,
            keyWidth = dimensions.keyWidth,
            keyHeight = dimensions.keyHeight,
            overlay = overlay,
            content = content,
        ),
    )
}

private fun floatingKeyboardFrameDimensions(
    config: FloatingKeyboardFrameConfig,
    state: FloatingKeyboardFrameState,
    boxWidthPx: Int,
): FloatingKeyboardFrameDimensions {
    val boxWidthDp = with(state.density) { boxWidthPx.toDp().value }.coerceAtLeast(1f)
    val safeColumns = config.columns.coerceAtLeast(1)
    val maxKeyWidthDp = (boxWidthDp / safeColumns).coerceAtLeast(1f)
    val effectiveKeyWidth = config.keyWidth.coerceIn(1f, maxKeyWidthDp)
    val effectiveKeyHeight = config.keyHeight.coerceAtLeast(1f)
    val panelWidthDp = (effectiveKeyWidth * safeColumns).coerceAtMost(boxWidthDp)
    val panelWidthPx =
        with(state.density) { panelWidthDp.dp.roundToPx() }.coerceIn(1, boxWidthPx.coerceAtLeast(1))
    val snapThresholdPx = with(state.density) { SNAP_EDGE_THRESHOLD_DP.dp.toPx() }
    val flingVelocityThresholdPx =
        with(state.density) { FLING_VELOCITY_THRESHOLD_DP_PER_SEC.dp.toPx() }

    return FloatingKeyboardFrameDimensions(
        panelWidthPx = panelWidthPx,
        panelWidthDp = panelWidthDp,
        keyWidth = effectiveKeyWidth,
        keyHeight = effectiveKeyHeight,
        snapThresholdPx = snapThresholdPx,
        flingVelocityThresholdPx = flingVelocityThresholdPx,
    )
}

private fun createFloatingKeyboardDragState(
    config: FloatingKeyboardFrameConfig,
    state: FloatingKeyboardFrameState,
    controller: FloatingKeyboardGestureController,
    dimensions: FloatingKeyboardFrameDimensions,
): FloatingKeyboardDragState =
    FloatingKeyboardDragState(
        controller = controller,
        gesture = state.gesture,
        dragShift = state.dragShift,
        snapIndicatorEdge = state.snapIndicatorEdge,
        isDragging = state.isDragging,
        isHandlePressed = state.isHandlePressed,
        lastActivityTick = state.lastActivityTick,
        snapThresholds =
            FloatingSnapThresholds(dimensions.snapThresholdPx, dimensions.flingVelocityThresholdPx),
        vibrateOnTap = config.vibrateOnTap,
        view = state.view,
        slideHapticConstant = state.slideHapticConstant,
    )
