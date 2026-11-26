package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import com.dessalines.thumbkey.utils.FloatingHandlePosition
import kotlin.math.roundToInt

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
    val frameState = rememberFloatingKeyboardFrameState(config)
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInWindow()
                    frameState.gesture.frameOriginInWindow =
                        IntOffset(position.x.roundToInt(), position.y.roundToInt())
                },
    ) {
        FloatingKeyboardFrameLayout(
            input =
                FloatingKeyboardFrameLayoutInput(
                    config = config,
                    state = frameState,
                    boxWidthPx = constraints.maxWidth,
                    boxHeightPx = constraints.maxHeight,
                ),
            overlay = overlay,
            content = content,
        )
    }
}
