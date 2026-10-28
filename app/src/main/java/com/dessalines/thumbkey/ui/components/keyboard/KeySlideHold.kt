package com.dessalines.thumbkey.ui.components.keyboard

import android.util.Log
import android.view.KeyEvent
import androidx.compose.ui.geometry.Offset
import com.dessalines.thumbkey.utils.SlideType
import com.dessalines.thumbkey.utils.SwipeDirection
import com.dessalines.thumbkey.utils.SwipeNWay
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.deleteWordBeforeCursor
import com.dessalines.thumbkey.utils.nextWordAfterCursor
import com.dessalines.thumbkey.utils.previousWordBeforeCursor
import com.dessalines.thumbkey.utils.swipeDirection
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val SLIDE_HOLD_SLOW_TICK_THRESHOLD = 4
private const val SLIDE_HOLD_SLOW_TICK_DELAY_MS = 300L
private const val SLIDE_HOLD_FAST_TICK_DELAY_MS = 100L

internal fun slideHoldDelayMs(tickCount: Int): Long =
    if (tickCount < SLIDE_HOLD_SLOW_TICK_THRESHOLD) {
        SLIDE_HOLD_SLOW_TICK_DELAY_MS
    } else {
        SLIDE_HOLD_FAST_TICK_DELAY_MS
    }

/** Runs cursor and delete repeat actions through one paced loop. */
internal suspend fun repeatSlideHold(
    ctx: KeyGestureContext,
    direction: SwipeDirection,
) {
    var tickCount = 0
    while (currentCoroutineContext().isActive) {
        delay(slideHoldDelayMs(tickCount))
        when (ctx.data.key.slideType) {
            SlideType.MOVE_CURSOR -> repeatCursorMovement(ctx, direction)
            SlideType.DELETE -> repeatDeleteAction(ctx)
            SlideType.NONE -> Unit
        }
        tickCount++
        ctx.state.slideHoldTickCount = tickCount
        ctx.feedback.slide()
    }
}

private fun repeatCursorMovement(
    ctx: KeyGestureContext,
    direction: SwipeDirection,
) {
    val keyCode =
        when (direction) {
            SwipeDirection.LEFT -> KeyEvent.KEYCODE_DPAD_LEFT
            SwipeDirection.RIGHT -> KeyEvent.KEYCODE_DPAD_RIGHT
            else -> Log.d(TAG, "ERROR: Invalid swipe direction: $direction")
        }
    if (ctx.state.slideHoldReturnDetected) {
        if (direction == SwipeDirection.RIGHT) nextWordAfterCursor(ctx.ime) else previousWordBeforeCursor(ctx.ime)
    } else {
        ctx.ime.currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        ctx.ime.currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }
}

private fun repeatDeleteAction(ctx: KeyGestureContext) {
    if (ctx.state.slideHoldReturnDetected) {
        deleteWordBeforeCursor(ctx.ime)
    } else {
        ctx.ime.currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        ctx.ime.currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
    }
}

internal fun handleSlideHold(
    ctx: KeyGestureContext,
    minSwipeLength: Int,
) {
    val state = ctx.state
    if (state.slideHoldDirection == null) {
        startSlideHold(ctx, minSwipeLength)
    } else {
        updateSlideHoldReturn(ctx, minSwipeLength, minSwipeLength * DRAG_RETURN_THRESHOLD_FACTOR)
    }
}

private fun startSlideHold(
    ctx: KeyGestureContext,
    minSwipeLength: Int,
) {
    val state = ctx.state
    val direction = swipeDirection(state.offsetX, state.offsetY, minSwipeLength, SwipeNWay.FOUR_WAY_CROSS) ?: return
    state.slideHoldDirection = direction
    val key = ctx.data.key
    val canRepeat =
        when (key.slideType) {
            SlideType.MOVE_CURSOR -> direction == SwipeDirection.LEFT || direction == SwipeDirection.RIGHT
            SlideType.DELETE -> direction == SwipeDirection.LEFT
            SlideType.NONE -> false
        }
    if (canRepeat) state.slideHoldRepeatJob = ctx.scope.launch { repeatSlideHold(ctx, direction) }
}

private fun updateSlideHoldReturn(
    ctx: KeyGestureContext,
    minSwipeLength: Int,
    returnThreshold: Float,
) {
    val state = ctx.state
    if (state.slideHoldReturnDetected || state.maxOffset.getDistance() < ctx.data.environment.config.minSwipeLength) return
    val currentOffset = state.positions.lastOrNull() ?: Offset(state.offsetX, state.offsetY)
    val closeEnough = currentOffset.getDistance() <= returnThreshold
    val currentDirection =
        swipeDirection(
            currentOffset.x,
            currentOffset.y,
            minSwipeLength,
            SwipeNWay.FOUR_WAY_CROSS,
        )
    val maxDirection =
        swipeDirection(
            state.maxOffset.x,
            state.maxOffset.y,
            minSwipeLength,
            SwipeNWay.FOUR_WAY_CROSS,
        )
    if (closeEnough || currentDirection != maxDirection) state.slideHoldReturnDetected = true
}
