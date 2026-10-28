package com.dessalines.thumbkey.utils

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * If this doesn't meet the minimum swipe length, it returns null
 */
fun swipeDirection(
    x: Float,
    y: Float,
    minSwipeLength: Int,
    swipeType: SwipeNWay = SwipeNWay.EIGHT_WAY,
): SwipeDirection? {
    val xD = x.toDouble()
    val yD = y.toDouble()

    val swipeLength = sqrt(xD.pow(2) + yD.pow(2))

    if (swipeLength > minSwipeLength) {
        val angleDir = (atan2(xD, yD) / Math.PI * 180)
        val angle =
            if (angleDir < 0) {
                360 + angleDir
            } else {
                angleDir
            }

        return when (swipeType) {
            SwipeNWay.EIGHT_WAY -> eightWaySwipeDirection(angle)
            SwipeNWay.FOUR_WAY_CROSS -> fourWayCrossSwipeDirection(angle)
            SwipeNWay.FOUR_WAY_DIAGONAL -> fourWayDiagonalSwipeDirection(angle)
            SwipeNWay.TWO_WAY_HORIZONTAL -> twoWayHorizontalSwipeDirection(angle)
            SwipeNWay.TWO_WAY_VERTICAL -> twoWayVerticalSwipeDirection(angle)
        }
    } else {
        return null
    }
}

private fun eightWaySwipeDirection(angle: Double): SwipeDirection =
    when (angle) {
        in 22.5..67.5 -> SwipeDirection.BOTTOM_RIGHT
        in 67.5..112.5 -> SwipeDirection.RIGHT
        in 112.5..157.5 -> SwipeDirection.TOP_RIGHT
        in 157.5..202.5 -> SwipeDirection.TOP
        in 202.5..247.5 -> SwipeDirection.TOP_LEFT
        in 247.5..292.5 -> SwipeDirection.LEFT
        in 292.5..337.5 -> SwipeDirection.BOTTOM_LEFT
        else -> SwipeDirection.BOTTOM
    }

private fun fourWayCrossSwipeDirection(angle: Double): SwipeDirection =
    when (angle) {
        in 45.0..135.0 -> SwipeDirection.RIGHT
        in 135.0..225.0 -> SwipeDirection.TOP
        in 225.0..315.0 -> SwipeDirection.LEFT
        else -> SwipeDirection.BOTTOM
    }

private fun fourWayDiagonalSwipeDirection(angle: Double): SwipeDirection =
    when (angle) {
        in 0.0..90.0 -> SwipeDirection.BOTTOM_RIGHT
        in 90.0..180.0 -> SwipeDirection.TOP_RIGHT
        in 180.0..270.0 -> SwipeDirection.TOP_LEFT
        else -> SwipeDirection.BOTTOM_LEFT
    }

private fun twoWayHorizontalSwipeDirection(angle: Double): SwipeDirection =
    when (angle) {
        in 0.0..180.0 -> SwipeDirection.RIGHT
        else -> SwipeDirection.LEFT
    }

private fun twoWayVerticalSwipeDirection(angle: Double): SwipeDirection =
    when (angle) {
        in 90.0..270.0 -> SwipeDirection.TOP
        else -> SwipeDirection.BOTTOM
    }

fun circularDirection(
    positions: List<Offset>,
    circleCompletionTolerance: Float,
    minSwipeLength: Int,
): CircularDirection? {
    // first filter out all run-ups to the start of the circle:
    // throw away all positions that consecutively get closer to the endpoint of the circle
    // so that an initial offset of the circle can be accounted for.
    // This allows for spiralling circles and makes detection quite a bit better
    val filteredPositions =
        positions.dropWhileIndexed { index, position ->
            index == 0 || position.getDistanceTo(positions.last()) <= positions[index - 1].getDistanceTo(positions.last())
        }

    return if (filteredPositions.isNotEmpty()) {
        val center = filteredPositions.reduce(Offset::plus) / filteredPositions.count().toFloat()
        val radii = filteredPositions.map { it.getDistanceTo(center) }
        val maxRadius = radii.reduce { acc, it -> if (it > acc) it else acc }
        val minRadius = radii.reduce { acc, it -> if (it < acc) it else acc }

        val isValidCircle = minRadius > (minSwipeLength / 2)

        if (isValidCircle) {
            val spannedAngle =
                filteredPositions
                    .asSequence()
                    .map { it - center }
                    .windowed(2)
                    .map { (a, b) ->
                        val (xa, ya) = a
                        val (xb, yb) = b
                        atan2(
                            xa * yb - ya * xb,
                            xa * xb + ya * yb,
                        )
                    }.sum()

            val averageRadius = (minRadius + maxRadius) / 2
            val angleThreshold = 2 * PI * (1 - circleCompletionTolerance / averageRadius)

            when {
                spannedAngle >= angleThreshold -> CircularDirection.Clockwise
                spannedAngle <= -angleThreshold -> CircularDirection.Counterclockwise
                else -> null
            }
        } else {
            null
        }
    } else {
        null
    }
}

/** Drops the leading elements whose indexed values satisfy [predicate]. */
inline fun <T> List<T>.dropWhileIndexed(predicate: (index: Int, T) -> Boolean): List<T> {
    for (i in indices) {
        if (!predicate(i, this[i])) return subList(i, size)
    }
    return emptyList()
}

fun Offset.getDistanceTo(other: Offset) = (other - this).getDistance()
