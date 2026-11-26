package com.dessalines.thumbkey.utils

import androidx.compose.runtime.MutableState
import com.dessalines.thumbkey.IMEService

internal fun performKeyAction(
    action: KeyAction,
    context: KeyActionContext,
) {
    if (handleTextActions(action, context)) return
    if (handleClipboardAndImeActions(action, context)) return
    handleModeAndOtherActions(action, context)
}

fun buildTapActions(keyItem: KeyItemC): List<KeyAction> {
    val mutable = mutableListOf(keyItem.center.action)
    mutable.addAll(keyItem.nextTapActions.orEmpty())
    return mutable.toList()
}

internal data class KeyActionReleaseState(
    val pressed: MutableState<Boolean>,
    val releasedFeedback: MutableState<ReleasedKeyFeedback?>,
)

internal fun doneKeyAction(
    action: KeyAction,
    releaseState: KeyActionReleaseState,
    originDirection: SwipeDirection? = null,
) {
    releaseState.pressed.value = false
    val displayText =
        when (action) {
            is KeyAction.CommitText -> action.text
            is KeyAction.Sketch.CommitCandidate -> action.displayText
            else -> null
        } ?: return
    releaseState.releasedFeedback.value =
        ReleasedKeyFeedback(
            text = displayText,
            originDirection = originDirection,
            id = (releaseState.releasedFeedback.value?.id ?: 0L) + 1L,
        )
}

internal fun autoCapitalize(
    ime: IMEService,
    onAutoCapitalize: (enable: Boolean) -> Unit,
    autocapitalizers: AutoCapitalizers,
) {
    // Run language specific autocapitalizers
    autocapitalizers.forEach { fn ->
        fn(ime)
    }

    if (autoCapitalizeCheck(ime)) {
        onAutoCapitalize(true)
    } else {
        onAutoCapitalize(false)
    }
}
