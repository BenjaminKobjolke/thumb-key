package com.dessalines.thumbkey.utils

import androidx.compose.runtime.MutableState
import com.dessalines.thumbkey.IMEService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
    val scope: CoroutineScope,
    val pressed: MutableState<Boolean>,
    val releasedKey: MutableState<String?>,
    val animationHelperSpeed: Int,
)

internal fun doneKeyAction(
    action: KeyAction,
    releaseState: KeyActionReleaseState,
) {
    releaseState.pressed.value = false
    releaseState.scope.launch {
        delay(releaseState.animationHelperSpeed.toLong().milliseconds)
        releaseState.releasedKey.value = null
    }
    releaseState.releasedKey.value =
        when (action) {
            is KeyAction.CommitText -> action.text
            is KeyAction.Sketch.CommitCandidate -> action.displayText
            else -> null
        }
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
