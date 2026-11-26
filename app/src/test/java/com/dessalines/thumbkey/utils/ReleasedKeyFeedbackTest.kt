package com.dessalines.thumbkey.utils

import androidx.compose.runtime.mutableStateOf
import org.junit.Assert.assertEquals
import org.junit.Test

class ReleasedKeyFeedbackTest {
    @Test
    fun committedTextCreatesFeedbackWithOriginAndIncreasingId() {
        val pressed = mutableStateOf(true)
        val feedback = mutableStateOf<ReleasedKeyFeedback?>(null)

        val releaseState = KeyActionReleaseState(pressed, feedback)
        doneKeyAction(KeyAction.CommitText("a"), releaseState, SwipeDirection.TOP_RIGHT)

        assertEquals(false, pressed.value)
        assertEquals(ReleasedKeyFeedback("a", SwipeDirection.TOP_RIGHT, 1L), feedback.value)

        doneKeyAction(KeyAction.Sketch.CommitCandidate("á", "◌́"), releaseState)

        assertEquals(ReleasedKeyFeedback("◌́", null, 2L), feedback.value)
    }

    @Test
    fun actionsWithoutDisplayedTextKeepExistingFeedback() {
        val pressed = mutableStateOf(true)
        val previous = ReleasedKeyFeedback("a", SwipeDirection.LEFT, 1L)
        val feedback = mutableStateOf<ReleasedKeyFeedback?>(previous)

        doneKeyAction(KeyAction.Noop, KeyActionReleaseState(pressed, feedback))

        assertEquals(false, pressed.value)
        assertEquals(previous, feedback.value)
    }

    @Test
    fun animationTypeUsesItsConfiguredOrSwipeOriginTranslation() {
        assertEquals(
            androidx.compose.ui.geometry
                .Offset(0f, -0.5f),
            KeyAnimationType.SlideFromTop.initialTranslationFraction(SwipeDirection.RIGHT),
        )
        assertEquals(
            androidx.compose.ui.geometry
                .Offset(0.42f, -0.42f),
            KeyAnimationType.FromOrigin.initialTranslationFraction(SwipeDirection.TOP_RIGHT),
        )
        assertEquals(
            androidx.compose.ui.geometry.Offset.Zero,
            KeyAnimationType.FromOrigin.initialTranslationFraction(null),
        )
    }
}
