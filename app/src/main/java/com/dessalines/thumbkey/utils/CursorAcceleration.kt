package com.dessalines.thumbkey.utils

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

fun accelCurve(
    offset: Float,
    threshold: Float,
    exp: Float,
): Float {
    var x = abs(offset)
    val belowThreshold = min(offset, threshold)
    x = max(0.0f, x - belowThreshold)
    return x.pow(exp) + belowThreshold
}

fun acceleratingCursorDistanceThreshold(
    offsetX: Float,
    timeOfLastAccelerationInput: Long,
    acceleration: Int,
): Int {
    // val exp = 1.0f // Slow and we can cover  1 1/2 full lines, so perfect for most.
    // val exp = 1.5f // Slow and we can cover 2 full lines, so perfect for most.
    // val exp = 2.0f // 2.0 should be the default
    // val exp = 3.0f // 3.0 should be the upper limit for this
    // Convert user's chosen acceleration of 1-50 to the amount we need.
    val exp = 1.0f + ((acceleration * 4) / 100f) // Will give us a range from 1-3
    val threshold = 2.0f // The threshold before acceleration kicks in.

    val timeDifference = System.currentTimeMillis() - timeOfLastAccelerationInput
    // Prevent division by 0 error.
    var distance =
        if (timeDifference == 0L) {
            0f
        } else {
            abs(offsetX) / timeDifference
        }

    distance = accelCurve(distance, threshold, exp)
    if (offsetX < 0) {
        // Set the value back to negative.
        // A distance of -1 will move the cursor left by 1 character
        distance *= -1
    }
    // distance = offsetX / 10
    return distance.toInt()
}

fun slideCursorDistance(
    offsetX: Float,
    timeOfLastAccelerationInput: Long,
    accelerationMode: Int,
    acceleration: Int,
): Int {
    when (accelerationMode) {
        CursorAccelerationMode.CONSTANT.ordinal -> {
            // Do the same speed every time
            val settingsSliderMaxValue = 50

            return if (abs(offsetX) > (settingsSliderMaxValue - acceleration)) {
                if (offsetX > 0) {
                    1
                } else {
                    -1
                }
            } else {
                0
            }
        }

        CursorAccelerationMode.QUADRATIC.ordinal -> {
            return acceleratingCursorDistanceQuadratic(
                offsetX,
                timeOfLastAccelerationInput,
                acceleration,
            )
        }

        CursorAccelerationMode.LINEAR.ordinal -> {
            return acceleratingCursorDistanceLinear(
                offsetX,
                timeOfLastAccelerationInput,
                acceleration,
            )
        }

        CursorAccelerationMode.THRESHOLD.ordinal -> {
            return acceleratingCursorDistanceThreshold(
                offsetX,
                timeOfLastAccelerationInput,
                acceleration,
            )
        }

        else -> {
            // Default to this if there is no match.
            return acceleratingCursorDistanceLinear(
                offsetX,
                timeOfLastAccelerationInput,
                acceleration,
            )
        }
    }
}

fun acceleratingCursorDistanceLinear(
    offsetX: Float,
    timeOfLastAccelerationInput: Long,
    acceleration: Int,
): Int {
    val accelerationCurve = ((acceleration * 6) / 100f) // Will give us a range from 0-3
    val timeDifference = System.currentTimeMillis() - timeOfLastAccelerationInput
    // Prevent division by 0 error.
    var distance =
        if (timeDifference == 0L) {
            0f
        } else {
            abs(offsetX) / timeDifference
        }

    distance *= accelerationCurve
    if (offsetX < 0) {
        // Set the value back to negative.
        // A distance of -1 will move the cursor left by 1 character
        distance *= -1
    }

    return distance.toInt()
}

fun acceleratingCursorDistanceQuadratic(
    offsetX: Float,
    timeOfLastAccelerationInput: Long,
    acceleration: Int,
): Int {
    val accelerationCurve = 0.1f + ((acceleration * 6) / 1000f) // Will give us a range from 0.1-0.4
    val timeDifference = System.currentTimeMillis() - timeOfLastAccelerationInput
    // Prevent division by 0 error.
    var distance =
        if (timeDifference == 0L) {
            0f
        } else {
            abs(offsetX) / timeDifference
        }

    // Quadratic equation to make the swipe acceleration work along a curve.
    // val accelerationCurve = 0.3f // Fast and almost perfect.
    // var accelerationCurve = 0.2f // Fast and almost perfect.
    // var accelerationCurve = 0.1f // Slowish and moves almost a full line at a time.
    // var accelerationCurve = 0.01f // is slow, only 1 char at a time.
    distance = accelerationCurve * distance.pow(2)
    if (offsetX < 0) {
        // Set the value back to negative.
        // A distance of -1 will move the cursor left by 1 character
        distance *= -1
    }

    return distance.toInt()
}
