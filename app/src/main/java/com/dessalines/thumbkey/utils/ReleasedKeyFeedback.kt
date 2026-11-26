package com.dessalines.thumbkey.utils

import androidx.compose.ui.geometry.Offset

/** Text briefly shown over a key after a text-producing action completes. */
internal data class ReleasedKeyFeedback(
    val text: String,
    val originDirection: SwipeDirection?,
    val id: Long,
)

internal fun KeyAnimationType.initialTranslationFraction(originDirection: SwipeDirection?): Offset =
    when (this) {
        KeyAnimationType.SlideFromTop -> Offset(0f, -0.5f)
        KeyAnimationType.SlideFromBottom -> Offset(0f, 0.5f)
        KeyAnimationType.SlideFromLeft -> Offset(-0.5f, 0f)
        KeyAnimationType.SlideFromRight -> Offset(0.5f, 0f)
        KeyAnimationType.Fade -> Offset.Zero
        KeyAnimationType.FromOrigin -> originDirection?.originTranslationFraction() ?: Offset.Zero
    }

private fun SwipeDirection.originTranslationFraction(): Offset =
    when (this) {
        SwipeDirection.TOP -> Offset(0f, -0.45f)
        SwipeDirection.BOTTOM -> Offset(0f, 0.45f)
        SwipeDirection.LEFT -> Offset(-0.45f, 0f)
        SwipeDirection.RIGHT -> Offset(0.45f, 0f)
        SwipeDirection.TOP_LEFT -> Offset(-0.42f, -0.42f)
        SwipeDirection.TOP_RIGHT -> Offset(0.42f, -0.42f)
        SwipeDirection.BOTTOM_LEFT -> Offset(-0.42f, 0.42f)
        SwipeDirection.BOTTOM_RIGHT -> Offset(0.42f, 0.42f)
    }
