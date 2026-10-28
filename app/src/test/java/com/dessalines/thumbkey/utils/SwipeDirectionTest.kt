package com.dessalines.thumbkey.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SwipeDirectionTest {
    @Test
    fun eightWayRecognizesEveryCardinalAndDiagonalVector() {
        assertSwipe(SwipeDirection.TOP, 0f, -10f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.TOP_RIGHT, 1f, -1f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.RIGHT, 10f, 0f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.BOTTOM_RIGHT, 1f, 1f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.BOTTOM, 0f, 10f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.BOTTOM_LEFT, -1f, 1f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.LEFT, -10f, 0f, SwipeNWay.EIGHT_WAY)
        assertSwipe(SwipeDirection.TOP_LEFT, -1f, -1f, SwipeNWay.EIGHT_WAY)
    }

    @Test
    fun fourWayCrossRecognizesCardinalAndDiagonalVectors() {
        assertSwipe(SwipeDirection.RIGHT, 10f, 0f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.RIGHT, 1f, 1f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.TOP, 0f, -10f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.TOP, -1f, -1f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.LEFT, -10f, 0f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.LEFT, -1f, 1f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.BOTTOM, 0f, 10f, SwipeNWay.FOUR_WAY_CROSS)
        assertSwipe(SwipeDirection.RIGHT, 1f, -1f, SwipeNWay.FOUR_WAY_CROSS)
    }

    @Test
    fun fourWayCrossUsesFirstMatchingRangeAtSharedBoundaries() {
        assertSwipe(SwipeDirection.RIGHT, 1f, 1f, SwipeNWay.FOUR_WAY_CROSS) // 45 degrees
        assertSwipe(SwipeDirection.RIGHT, 1f, -1f, SwipeNWay.FOUR_WAY_CROSS) // 135 degrees
        assertSwipe(SwipeDirection.TOP, -1f, -1f, SwipeNWay.FOUR_WAY_CROSS) // 225 degrees
        assertSwipe(SwipeDirection.LEFT, -1f, 1f, SwipeNWay.FOUR_WAY_CROSS) // 315 degrees
    }

    @Test
    fun fourWayDiagonalRecognizesEveryQuadrant() {
        assertSwipe(SwipeDirection.BOTTOM_RIGHT, 0f, 10f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.BOTTOM_RIGHT, 1f, 1f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.BOTTOM_RIGHT, 10f, 0f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.TOP_RIGHT, 1f, -1f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.TOP_RIGHT, 0f, -10f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.TOP_LEFT, -1f, -1f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.TOP_LEFT, -10f, 0f, SwipeNWay.FOUR_WAY_DIAGONAL)
        assertSwipe(SwipeDirection.BOTTOM_LEFT, -1f, 1f, SwipeNWay.FOUR_WAY_DIAGONAL)
    }

    @Test
    fun twoWayHorizontalRecognizesHorizontalAndDiagonalVectors() {
        assertSwipe(SwipeDirection.RIGHT, 10f, 0f, SwipeNWay.TWO_WAY_HORIZONTAL)
        assertSwipe(SwipeDirection.RIGHT, 1f, 1f, SwipeNWay.TWO_WAY_HORIZONTAL)
        assertSwipe(SwipeDirection.LEFT, -10f, 0f, SwipeNWay.TWO_WAY_HORIZONTAL)
        assertSwipe(SwipeDirection.LEFT, -1f, -1f, SwipeNWay.TWO_WAY_HORIZONTAL)
    }

    @Test
    fun twoWayVerticalRecognizesVerticalAndDiagonalVectors() {
        assertSwipe(SwipeDirection.TOP, 0f, -10f, SwipeNWay.TWO_WAY_VERTICAL)
        assertSwipe(SwipeDirection.TOP, 1f, -1f, SwipeNWay.TWO_WAY_VERTICAL)
        assertSwipe(SwipeDirection.BOTTOM, 0f, 10f, SwipeNWay.TWO_WAY_VERTICAL)
        assertSwipe(SwipeDirection.BOTTOM, -1f, 1f, SwipeNWay.TWO_WAY_VERTICAL)
    }

    @Test
    fun vectorExactlyAtMinimumSwipeLengthIsRejected() {
        assertEquals(null, swipeDirection(3f, 4f, 5, SwipeNWay.EIGHT_WAY))
        assertEquals(SwipeDirection.BOTTOM_RIGHT, swipeDirection(3f, 4f, 4, SwipeNWay.EIGHT_WAY))
    }

    private fun assertSwipe(
        expected: SwipeDirection,
        x: Float,
        y: Float,
        swipeType: SwipeNWay,
    ) {
        assertEquals("$swipeType for vector ($x, $y)", expected, swipeDirection(x, y, 0, swipeType))
    }
}
