package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.dessalines.thumbkey.utils.Selection
import com.dessalines.thumbkey.utils.SwipeDirection
import kotlinx.coroutines.Job

/** Snapshot-backed values collected while one key is pressed or dragged. */
internal class KeyGestureState {
    var tapCount by mutableIntStateOf(0)
    var offsetX by mutableFloatStateOf(0f)
    var offsetY by mutableFloatStateOf(0f)
    var hasSlideMoveCursorTriggered by mutableStateOf(false)
    var timeOfLastAccelerationInput by mutableLongStateOf(0L)
    var positions by mutableStateOf(emptyList<Offset>())
    var maxOffset by mutableStateOf(Offset.Zero)
    var selection by mutableStateOf(Selection())
    var slideHoldDirection by mutableStateOf<SwipeDirection?>(null)
    var slideHoldReturnDetected by mutableStateOf(false)
    var slideHoldTickCount by mutableIntStateOf(0)
    var slideHoldRepeatJob by mutableStateOf<Job?>(null)

    fun recordDrag(dragAmount: Offset) {
        offsetX += dragAmount.x
        offsetY += dragAmount.y
        val offset = Offset(offsetX, offsetY)
        positions += offset
        if (offset.getDistanceSquared() > maxOffset.getDistanceSquared()) maxOffset = offset
    }

    fun resetSlideHold(): Int {
        val slideHoldTicks = slideHoldTickCount
        slideHoldRepeatJob?.cancel()
        slideHoldRepeatJob = null
        slideHoldDirection = null
        slideHoldReturnDetected = false
        slideHoldTickCount = 0
        return slideHoldTicks
    }

    fun resetDrag() {
        offsetX = 0f
        offsetY = 0f
        maxOffset = Offset.Zero
        positions = emptyList()
        selection = Selection()
    }
}
