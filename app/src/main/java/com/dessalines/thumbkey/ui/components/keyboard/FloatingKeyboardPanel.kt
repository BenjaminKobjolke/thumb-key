package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.db.MAX_KEYBOARD_OPACITY
import com.dessalines.thumbkey.db.MIN_KEYBOARD_OPACITY
import com.dessalines.thumbkey.utils.FloatingHandlePosition
import kotlin.math.roundToInt

internal data class FloatingKeyboardPanelContent(
    val frameConfig: FloatingKeyboardFrameConfig,
    val dragState: FloatingKeyboardDragState,
    val panelWidthPx: Int,
    val panelWidthDp: Float,
    val keyWidth: Float,
    val keyHeight: Float,
    val overlay: @Composable BoxScope.() -> Unit,
    val content: @Composable (Float, Float) -> Unit,
)

@Composable
internal fun FloatingKeyboardPanel(panel: FloatingKeyboardPanelContent) {
    val drag = panel.dragState
    val handleCallbacks = floatingKeyboardDragCallbacks(drag)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .layout { measurable, constraints ->
                    val width = panel.panelWidthPx.coerceIn(1, constraints.maxWidth.coerceAtLeast(1))
                    val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                    drag.gesture.panelSize = IntSize(placeable.width, placeable.height)
                    layout(placeable.width, placeable.height) {
                        val position = drag.controller.currentOffset(placeable.width, placeable.height)
                        placeable.place(position.x.roundToInt(), position.y.roundToInt())
                    }
                }.graphicsLayer {
                    translationX = drag.dragShift.value.x
                    translationY = drag.dragShift.value.y
                }.onGloballyPositioned { coordinates ->
                    if (!drag.gesture.active && !drag.gesture.animating) {
                        val position = coordinates.positionInWindow()
                        drag.gesture.bounds = IntRect(
                            offset = IntOffset(position.x.roundToInt(), position.y.roundToInt()),
                            size = coordinates.size,
                        )
                        drag.controller.publishBounds()
                    }
                },
    ) {
        val dragHandle: @Composable (Boolean) -> Unit = { atTop ->
            FloatingDragHandle(
                atTop = atTop,
                panelWidthDp = panel.panelWidthDp,
                activity = FloatingDragHandleActivity(
                    isPressed = drag.isHandlePressed.value,
                    isDragging = drag.isDragging.value,
                    activityTick = drag.lastActivityTick,
                ),
                callbacks = handleCallbacks,
            )
        }
        if (panel.frameConfig.handlePosition == FloatingHandlePosition.Top) dragHandle(true)
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            if (event.changes.any { it.changedToDownIgnoreConsumed() || it.changedToUpIgnoreConsumed() }) {
                                drag.lastActivityTick.intValue += 1
                            }
                        }
                    }
                },
        ) {
            val opacity = panel.frameConfig.keyboardOpacity
            val opacityModifier = if (opacity < MAX_KEYBOARD_OPACITY) {
                Modifier.graphicsLayer {
                    alpha = (opacity / 100f).coerceIn(MIN_KEYBOARD_OPACITY / 100f, 1f)
                    compositingStrategy = CompositingStrategy.Offscreen
                }
            } else Modifier
            Box(modifier = opacityModifier) { panel.content(panel.keyWidth, panel.keyHeight) }
            panel.overlay(this)
        }
        if (panel.frameConfig.handlePosition == FloatingHandlePosition.Bottom) dragHandle(false)
    }
}
