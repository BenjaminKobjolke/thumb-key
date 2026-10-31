package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val SKETCH_PEN_WIDTH_DP = 6f

// The dashed guide line, as a fraction of the pad height
private const val SKETCH_BASELINE = 0.72f

private data class SketchPadColors(
    val ink: androidx.compose.ui.graphics.Color,
    val guide: androidx.compose.ui.graphics.Color,
)

/** Records strokes in its own pixels. A tap is a stroke of one point, a dot. */
@Composable
internal fun SketchPad(
    strokes: List<List<Offset>>,
    hint: String,
    onStroke: (List<Offset>) -> Unit,
) {
    val current = remember { mutableStateListOf<Offset>() }
    val ink = MaterialTheme.colorScheme.onSurface
    val guide = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)

    Box(modifier = Modifier.fillMaxSize()) {
        SketchPadHint(strokes.isEmpty() && current.isEmpty(), hint, hintColor)
        SketchPadCanvas(strokes, current, SketchPadColors(ink, guide), onStroke)
    }
}

@Composable
private fun SketchPadHint(
    visible: Boolean,
    hint: String,
    hintColor: androidx.compose.ui.graphics.Color,
) {
    if (!visible) return
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Draw,
            contentDescription = null,
            tint = hintColor.copy(alpha = 0.35f),
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = hint,
            color = hintColor,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SketchPadCanvas(
    strokes: List<List<Offset>>,
    current: MutableList<Offset>,
    colors: SketchPadColors,
    onStroke: (List<Offset>) -> Unit,
) {
    val currentOnStroke by rememberUpdatedState(onStroke)
    Canvas(
        modifier =
            Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        current.clear()
                        current.add(down.position)
                        down.consume()
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            if (!change.pressed) break
                            change.historical.forEach { current.add(it.position) }
                            current.add(change.position)
                            change.consume()
                        }
                        val finished = current.toList()
                        current.clear()
                        currentOnStroke(finished)
                    }
                },
    ) {
        val baseline = size.height * SKETCH_BASELINE
        val inset = 14.dp.toPx()
        drawLine(
            color = colors.guide,
            start = Offset(inset, baseline),
            end = Offset(size.width - inset, baseline),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())),
        )
        val width = SKETCH_PEN_WIDTH_DP.dp.toPx()
        val pen = Stroke(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round)
        for (stroke in strokes + listOf(current.toList())) {
            drawStroke(stroke, colors.ink, width, pen)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStroke(
    stroke: List<Offset>,
    ink: androidx.compose.ui.graphics.Color,
    width: Float,
    pen: Stroke,
) {
    if (stroke.isEmpty()) return
    if (stroke.size == 1) {
        drawCircle(ink, radius = width / 2, center = stroke[0])
        return
    }
    val path = Path()
    path.moveTo(stroke[0].x, stroke[0].y)
    for (point in stroke.drop(1)) path.lineTo(point.x, point.y)
    drawPath(path, ink, style = pen)
}
