package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.ui.geometry.Offset
import com.dessalines.thumbkey.utils.Selection
import com.dessalines.thumbkey.utils.SlideType
import com.dessalines.thumbkey.utils.slideCursorDistance
import com.dessalines.thumbkey.utils.startSelection
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/** Thresholds calculated in the pointer input scope for one key. */
internal data class SlideTriggers(
    val slide: Float,
    val selection: Float,
)

internal fun shouldStartCursorSlide(
    deadzoneEnabled: Boolean,
    beyondDeadzone: Boolean,
    alreadyTriggered: Boolean,
): Boolean = alreadyTriggered || !deadzoneEnabled || beyondDeadzone

/** Applies drag state first, then dispatches slide-hold, cursor, or delete behavior in that order. */
internal fun handleKeyDrag(
    ctx: KeyGestureContext,
    dragAmount: Offset,
    triggers: SlideTriggers,
) {
    ctx.state.recordDrag(dragAmount)
    val key = ctx.data.key
    val config = ctx.data.environment.config
    when {
        (key.slideType == SlideType.MOVE_CURSOR || key.slideType == SlideType.DELETE) && config.slideHoldEnabled -> {
            handleSlideHold(ctx, config.minSwipeLength)
        }

        key.slideType == SlideType.MOVE_CURSOR && config.slideEnabled -> {
            handleCursorSlide(ctx, triggers)
        }

        key.slideType == SlideType.DELETE && config.slideEnabled -> {
            handleDeleteSlide(ctx, triggers.slide)
        }
    }
}

private fun handleCursorSlide(
    ctx: KeyGestureContext,
    triggers: SlideTriggers,
) {
    val state = ctx.state
    if (abs(state.offsetY) > triggers.selection) {
        selectBySpacebarSlide(ctx)
    } else if (
        shouldStartCursorSlide(
            ctx.data.environment.config.slideSpacebarDeadzoneEnabled,
            abs(state.offsetX) > triggers.slide,
            state.hasSlideMoveCursorTriggered,
        )
    ) {
        moveSpacebarCursor(ctx)
    }
}

private fun selectBySpacebarSlide(ctx: KeyGestureContext) {
    val state = ctx.state
    val config = ctx.data.environment.config
    state.hasSlideMoveCursorTriggered = true
    if (!state.selection.active) state.selection = startSelection(ctx.ime)
    val movement =
        slideCursorDistance(
            state.offsetX,
            state.timeOfLastAccelerationInput,
            config.slideCursorMovementMode,
            config.slideSensitivity,
        )
    state.timeOfLastAccelerationInput = System.currentTimeMillis()
    if (movement >= 1 || movement <= -1) moveSelectionBy(ctx, movement)
}

private fun moveSpacebarCursor(ctx: KeyGestureContext) {
    val state = ctx.state
    val config = ctx.data.environment.config
    if (!state.hasSlideMoveCursorTriggered) {
        state.offsetX = 0f
        state.hasSlideMoveCursorTriggered = true
    }
    val selectedLength = collapseKeyboardSelection(ctx)
    var movement =
        slideCursorDistance(
            state.offsetX,
            state.timeOfLastAccelerationInput,
            config.slideCursorMovementMode,
            config.slideSensitivity,
        )
    state.timeOfLastAccelerationInput = System.currentTimeMillis()
    if (movement > 0) movement += 1
    if (movement >= 2 || movement <= -1) {
        if (selectedLength > 0) {
            state.selection = startSelection(ctx.ime)
            ctx.ime.currentInputConnection.setSelection(state.selection.start, state.selection.end)
            state.selection = Selection()
            if (movement >= 2) movement += selectedLength
        }
        ctx.ime.currentInputConnection.commitText("", movement)
        state.offsetX = 0f
        ctx.feedback.slide()
    }
}

private fun collapseKeyboardSelection(ctx: KeyGestureContext): Int {
    val selection = ctx.state.selection
    if (selection.active) {
        val location = if (ctx.state.offsetX < 0) min(selection.start, selection.end) else max(selection.start, selection.end)
        ctx.state.selection = Selection(location, location, false)
        ctx.ime.currentInputConnection.setSelection(location, location)
        return 0
    }
    val selectedText = ctx.ime.currentInputConnection.getSelectedText(0)
    return if (selectedText.isNullOrEmpty()) 0 else selectedText.length
}

private fun handleDeleteSlide(
    ctx: KeyGestureContext,
    slideTrigger: Float,
) {
    if (ctx.state.selection.active) {
        extendDeleteSelection(ctx)
    } else {
        beginDeleteSelection(ctx, slideTrigger)
    }
}

private fun beginDeleteSelection(
    ctx: KeyGestureContext,
    slideTrigger: Float,
) {
    val state = ctx.state
    val config = ctx.data.environment.config
    state.timeOfLastAccelerationInput = System.currentTimeMillis()
    if (config.slideBackspaceDeadzoneEnabled && abs(state.offsetX) <= slideTrigger) return
    state.offsetX = 0f
    state.selection = startSelection(ctx.ime)
}

private fun extendDeleteSelection(ctx: KeyGestureContext) {
    val state = ctx.state
    val config = ctx.data.environment.config
    val movement =
        slideCursorDistance(
            state.offsetX,
            state.timeOfLastAccelerationInput,
            config.slideCursorMovementMode,
            config.slideSensitivity,
        )
    state.timeOfLastAccelerationInput = System.currentTimeMillis()
    if (movement >= 1 || movement <= -1) moveSelectionBy(ctx, movement)
}

/** Extends a selection and performs the shared reset and feedback after the IME update. */
internal fun moveSelectionBy(
    ctx: KeyGestureContext,
    cursorMovement: Int,
) {
    val selection = ctx.state.selection
    if (cursorMovement < 0) selection.left(abs(cursorMovement)) else selection.right(abs(cursorMovement))
    ctx.ime.currentInputConnection.setSelection(selection.start, selection.end)
    ctx.state.offsetX = 0f
    ctx.feedback.slide()
}
