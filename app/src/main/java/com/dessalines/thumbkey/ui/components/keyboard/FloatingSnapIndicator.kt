package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private val HORIZONTAL_INSET = 2.dp
private val VERTICAL_INSET = 12.dp
private val DOUBLE_VERTICAL_INSET = VERTICAL_INSET * 2
private val INDICATOR_WIDTH = 4.dp
private val MIN_INDICATOR_HEIGHT = 24.dp
private val CORNER_RADIUS = 2.dp

internal data class FloatingSnapIndicatorState(
    val edge: FloatingSnapEdge?,
    val gesture: FloatingGestureState,
    val controller: FloatingKeyboardGestureController,
    val dragShift: Offset,
    val density: Density,
)

@Composable
internal fun BoxScope.FloatingSnapIndicator(state: FloatingSnapIndicatorState) {
    val edge = state.edge ?: return
    if (state.gesture.panelSize.height <= 0) return
    val indicatorHeightDp = with(state.density) { state.gesture.panelSize.height.toDp() }
    val alignment = if (edge == FloatingSnapEdge.LEFT) Alignment.TopStart else Alignment.TopEnd
    Box(
        modifier =
            Modifier
                .align(alignment)
                .offset {
                    val top = (state.controller.currentOffset().y + state.dragShift.y).roundToInt().coerceAtLeast(0)
                    IntOffset(0, top)
                }.padding(horizontal = HORIZONTAL_INSET, vertical = VERTICAL_INSET)
                .width(INDICATOR_WIDTH)
                .height((indicatorHeightDp - DOUBLE_VERTICAL_INSET).coerceAtLeast(MIN_INDICATOR_HEIGHT))
                .clip(RoundedCornerShape(CORNER_RADIUS))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)),
    )
}
