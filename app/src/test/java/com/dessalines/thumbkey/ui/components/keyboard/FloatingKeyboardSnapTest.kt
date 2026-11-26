package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.ui.geometry.Offset
import org.junit.Assert.assertEquals
import org.junit.Test

class FloatingKeyboardSnapTest {
    @Test(timeout = 30_000)
    fun resolveSnapTargetX_snapsToNearestEdgeWithinThreshold() {
        assertEquals(0f to FloatingSnapEdge.LEFT, resolveSnap(20f, 300f, 0f, 0f))
        assertEquals(300f to FloatingSnapEdge.RIGHT, resolveSnap(280f, 300f, 0f, 0f))
    }

    @Test(timeout = 30_000)
    fun resolveSnapTargetX_usesHorizontalFlingAndLeavesMiddlePositionUnsnapped() {
        assertEquals(0f to FloatingSnapEdge.LEFT, resolveSnap(150f, 300f, -600f, 100f))
        assertEquals(300f to FloatingSnapEdge.RIGHT, resolveSnap(150f, 300f, 600f, 100f))
        assertEquals(150f to null, resolveSnap(150f, 300f, 0f, 0f))
    }

    @Test(timeout = 30_000)
    fun resolveSnapTargetX_handlesNoAvailableHorizontalSlack() {
        assertEquals(0f to null, resolveSnap(50f, 0f, 600f, 0f))
    }

    @Test(timeout = 30_000)
    fun resolveFlipTargetX_movesToOppositeSide() {
        assertEquals(300f, resolveFlipTargetX(20f, 300f))
        assertEquals(0f, resolveFlipTargetX(280f, 300f))
        assertEquals(0f, resolveFlipTargetX(50f, 0f))
    }

    private fun resolveSnap(rawX: Float, slackX: Float, velocityX: Float, velocityY: Float) =
        resolveSnapTargetX(
            rawOffset = Offset(rawX, 0f),
            slackX = slackX,
            velocity = Offset(velocityX, velocityY),
            thresholds = FloatingSnapThresholds(snapPx = 28f, flingPxPerSecond = 550f),
        )
}
