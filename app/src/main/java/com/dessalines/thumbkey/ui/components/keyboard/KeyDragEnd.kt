package com.dessalines.thumbkey.ui.components.keyboard

import android.view.KeyEvent
import androidx.compose.ui.geometry.Offset
import com.dessalines.thumbkey.utils.CircularDirection
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.SlideType
import com.dessalines.thumbkey.utils.SwipeNWay
import com.dessalines.thumbkey.utils.circularDirection
import com.dessalines.thumbkey.utils.doneKeyAction
import com.dessalines.thumbkey.utils.performKeyAction
import com.dessalines.thumbkey.utils.swipeDirection

private enum class DragEndEffect { NONE, DISPATCH_ACTION, DELETE_SELECTION, FINISH_CURSOR_SLIDE }

private data class DragEndResult(
    val action: KeyAction,
    val effect: DragEndEffect,
)

/** Finishes a drag and returns the action recorded for subsequent multi-tap handling. */
internal fun handleKeyDragEnd(
    ctx: KeyGestureContext,
    keys: SwipeKeys,
    gesture: SwipeGesture,
): KeyAction {
    val result = resolveDragEnd(ctx, keys, gesture)
    applyDragEndEffect(ctx, result)
    doneKeyAction(result.action, ctx.releaseState)
    return result.action
}

private fun resolveDragEnd(
    ctx: KeyGestureContext,
    keys: SwipeKeys,
    gesture: SwipeGesture,
): DragEndResult {
    val slideHoldTicks = ctx.state.resetSlideHold()
    return when {
        isFinishedSlideHold(ctx, slideHoldTicks) -> {
            DragEndResult(KeyAction.Noop, DragEndEffect.NONE)
        }

        needsSwipeResolution(ctx) -> {
            ctx.state.hasSlideMoveCursorTriggered = false
            DragEndResult(resolveSwipeAction(keys, gesture, SwipeSettings.from(ctx.data.environment.config)), DragEndEffect.DISPATCH_ACTION)
        }

        ctx.data.key.slideType == SlideType.DELETE -> {
            DragEndResult(KeyAction.SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)), DragEndEffect.DELETE_SELECTION)
        }

        else -> {
            ctx.state.hasSlideMoveCursorTriggered = false
            DragEndResult(KeyAction.SendEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT)), DragEndEffect.FINISH_CURSOR_SLIDE)
        }
    }
}

private fun isFinishedSlideHold(
    ctx: KeyGestureContext,
    ticks: Int,
): Boolean =
    (ctx.data.key.slideType == SlideType.MOVE_CURSOR || ctx.data.key.slideType == SlideType.DELETE) &&
        ctx.data.environment.config.slideHoldEnabled && ticks > 0

private fun needsSwipeResolution(ctx: KeyGestureContext): Boolean {
    val key = ctx.data.key
    val state = ctx.state
    return key.slideType == SlideType.NONE || !ctx.data.environment.config.slideEnabled ||
        (key.slideType == SlideType.DELETE && !state.selection.active) ||
        (key.slideType == SlideType.MOVE_CURSOR && !state.hasSlideMoveCursorTriggered)
}

private fun applyDragEndEffect(
    ctx: KeyGestureContext,
    result: DragEndResult,
) {
    when (result.effect) {
        DragEndEffect.NONE, DragEndEffect.FINISH_CURSOR_SLIDE -> Unit
        DragEndEffect.DISPATCH_ACTION -> performKeyAction(result.action, ctx.keyActionContext)
        DragEndEffect.DELETE_SELECTION -> deleteSelectionIfPresent(ctx, result.action)
    }
}

private fun deleteSelectionIfPresent(
    ctx: KeyGestureContext,
    action: KeyAction,
) {
    val selectedText = ctx.ime.currentInputConnection.getSelectedText(0) ?: return
    if (selectedText.isNotEmpty()) performKeyAction(action, ctx.keyActionContext)
    ctx.feedback.slideDeleteReject()
}

// Resolves recorded pointer movement into the key action selected on release.

// Magic number found from trial and error.
internal const val DRAG_RETURN_THRESHOLD_FACTOR = 0.71f

/** Key variants consulted while resolving swipe and circular-drag actions. */
internal data class SwipeKeys(
    val key: KeyItemC,
    val ghostKey: KeyItemC? = null,
    val oppositeCaseKey: KeyItemC? = null,
    val numericKey: KeyItemC? = null,
)

/** Pointer positions and accumulated offsets for a single key gesture. */
internal data class SwipeGesture(
    val positions: List<Offset>,
    val maxOffset: Offset,
    val offsetX: Float,
    val offsetY: Float,
)

/** Swipe preferences that affect action selection. */
internal data class SwipeSettings(
    val minSwipeLength: Int,
    val dragReturnEnabled: Boolean,
    val circularDragEnabled: Boolean,
    val clockwiseDragAction: CircularDragAction,
    val counterclockwiseDragAction: CircularDragAction,
) {
    companion object {
        fun from(config: KeyboardScreenConfig) =
            SwipeSettings(
                config.minSwipeLength,
                config.dragReturnEnabled,
                config.circularDragEnabled,
                config.clockwiseDragAction,
                config.counterclockwiseDragAction,
            )
    }
}

internal fun resolveSwipeAction(
    keys: SwipeKeys,
    gesture: SwipeGesture,
    settings: SwipeSettings,
): KeyAction {
    val finalOffsetThreshold = settings.minSwipeLength * DRAG_RETURN_THRESHOLD_FACTOR
    val maxOffsetBigEnough = gesture.maxOffset.getDistance() >= settings.minSwipeLength
    val finalOffsetSmallEnough = isFinalOffsetSmallEnough(keys, gesture, settings, finalOffsetThreshold)

    return if (maxOffsetBigEnough && finalOffsetSmallEnough) {
        circularAction(keys, gesture, settings, finalOffsetThreshold)
            ?: dragReturnAction(keys, gesture, settings)
            ?: keys.key.center.action
    } else {
        swipeAction(keys, gesture, settings) ?: keys.key.center.action
    }
}

private fun isFinalOffsetSmallEnough(
    keys: SwipeKeys,
    gesture: SwipeGesture,
    settings: SwipeSettings,
    finalOffsetThreshold: Float,
): Boolean {
    val finalOffset = gesture.positions.last()
    if (finalOffset.getDistance() <= finalOffsetThreshold) return true
    val swipeType = keys.key.swipeType
    val finalDirection = swipeDirection(finalOffset.x, finalOffset.y, settings.minSwipeLength, swipeType)
    val maxDirection = swipeDirection(gesture.maxOffset.x, gesture.maxOffset.y, settings.minSwipeLength, swipeType)
    return finalDirection?.equals(maxDirection) != true
}

private fun circularAction(
    keys: SwipeKeys,
    gesture: SwipeGesture,
    settings: SwipeSettings,
    finalOffsetThreshold: Float,
): KeyAction? {
    if (!settings.circularDragEnabled) return null
    val direction = circularDirection(gesture.positions, finalOffsetThreshold, settings.minSwipeLength) ?: return null
    val configuredAction =
        when (direction) {
            CircularDirection.Clockwise -> settings.clockwiseDragAction
            CircularDirection.Counterclockwise -> settings.counterclockwiseDragAction
        }
    return circularKeyAction(keys, configuredAction)
}

private fun circularKeyAction(
    keys: SwipeKeys,
    action: CircularDragAction,
): KeyAction? =
    when (action) {
        CircularDragAction.OppositeCase -> keys.oppositeCaseKey?.center?.action
        CircularDragAction.Numeric -> keys.numericKey?.center?.action
    }

private fun dragReturnAction(
    keys: SwipeKeys,
    gesture: SwipeGesture,
    settings: SwipeSettings,
): KeyAction? {
    if (!settings.dragReturnEnabled) return null
    val direction =
        swipeDirection(gesture.maxOffset.x, gesture.maxOffset.y, settings.minSwipeLength, swipeType(keys))
    return keys.key.getSwipe(direction)?.swipeReturnAction
        ?: keys.oppositeCaseKey?.getSwipe(direction)?.action
        ?: keys.ghostKey?.getSwipe(direction)?.swipeReturnAction
}

private fun swipeAction(
    keys: SwipeKeys,
    gesture: SwipeGesture,
    settings: SwipeSettings,
): KeyAction? {
    val direction = swipeDirection(gesture.offsetX, gesture.offsetY, settings.minSwipeLength, swipeType(keys))
    return keys.key.getSwipe(direction)?.action ?: keys.ghostKey?.getSwipe(direction)?.action
}

private fun swipeType(keys: SwipeKeys) = if (keys.ghostKey == null) keys.key.swipeType else SwipeNWay.EIGHT_WAY
