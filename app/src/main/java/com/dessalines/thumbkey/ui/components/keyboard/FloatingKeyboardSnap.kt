package com.dessalines.thumbkey.ui.components.keyboard

import kotlin.math.abs
import androidx.compose.ui.geometry.Offset

internal enum class FloatingSnapEdge {
    LEFT,
    RIGHT,
}

internal data class FloatingSnapThresholds(
    val snapPx: Float,
    val flingPxPerSecond: Float,
)

internal fun resolveSnapTargetX(
    rawOffset: Offset,
    slackX: Float,
    velocity: Offset,
    thresholds: FloatingSnapThresholds,
): Pair<Float, FloatingSnapEdge?> {
    if (slackX <= 0f) return 0f to null
    if (abs(velocity.x) >= thresholds.flingPxPerSecond && abs(velocity.x) > abs(velocity.y) * 1.2f) {
        return if (velocity.x < 0f) {
            0f to FloatingSnapEdge.LEFT
        } else {
            slackX to FloatingSnapEdge.RIGHT
        }
    }
    return when {
        rawOffset.x <= thresholds.snapPx -> 0f to FloatingSnapEdge.LEFT
        slackX - rawOffset.x <= thresholds.snapPx -> slackX to FloatingSnapEdge.RIGHT
        else -> rawOffset.x.coerceIn(0f, slackX) to null
    }
}

internal fun resolveFlipTargetX(
    currentX: Float,
    slackX: Float,
): Float {
    if (slackX <= 0f) return 0f
    return if (currentX < slackX * 0.5f) slackX else 0f
}
