package com.dessalines.thumbkey.ui.components.keyboard

import android.media.AudioManager
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.utils.KeyActionCallbacks
import com.dessalines.thumbkey.utils.KeyActionContext
import com.dessalines.thumbkey.utils.KeyActionModeCallbacks
import com.dessalines.thumbkey.utils.KeyActionOtherCallbacks
import com.dessalines.thumbkey.utils.KeyActionReleaseState
import com.dessalines.thumbkey.utils.KeyActionSecondaryModeCallbacks
import kotlinx.coroutines.CoroutineScope

/** Dependencies shared by the drag handlers for one rendered key. */
internal data class KeyGestureContext(
    val data: ScreenKeyData,
    val ime: IMEService,
    val keyActionContext: KeyActionContext,
    val releaseState: KeyActionReleaseState,
    val state: KeyGestureState,
    val feedback: KeyFeedback,
    val scope: CoroutineScope,
)

/** Keeps key haptic and sound effects consistent across tap and slide input. */
internal class KeyFeedback(
    private val view: View,
    private val audioManager: AudioManager,
    private val config: KeyboardScreenConfig,
) {
    fun tap() {
        if (config.vibrateOnTap) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        if (config.soundOnTap) audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, .1f)
    }

    fun slide() {
        if (config.vibrateOnSlide) view.performHapticFeedback(slideHapticConstant())
    }

    fun slideDeleteReject() {
        if (config.vibrateOnSlide && Build.VERSION.SDK_INT >= 30) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        }
    }

    private fun slideHapticConstant(): Int =
        if (Build.VERSION.SDK_INT >= 27) {
            HapticFeedbackConstants.TEXT_HANDLE_MOVE
        } else {
            HapticFeedbackConstants.KEYBOARD_TAP
        }
}

internal fun keyActionContextFor(
    data: ScreenKeyData,
    ime: IMEService,
): KeyActionContext {
    val callbacks = data.environment.callbacks
    return KeyActionContext(
        ime = ime,
        autoCapitalize = data.environment.config.autoCapitalize,
        keyboardSettings = data.environment.keyboardSettings,
        callbacks =
            KeyActionCallbacks(
                modeToggles =
                    KeyActionModeCallbacks(
                        callbacks.onToggleShiftMode,
                        callbacks.onToggleCtrlMode,
                        callbacks.onToggleAltMode,
                        callbacks.onToggleNumericMode,
                    ),
                secondaryModeToggles =
                    KeyActionSecondaryModeCallbacks(
                        callbacks.onToggleEmojiMode,
                        callbacks.onToggleClipboardMode,
                        callbacks.onToggleTranscriptHistoryMode,
                        callbacks.onToggleSketchMode,
                    ),
                other =
                    KeyActionOtherCallbacks(
                        callbacks.onToggleCapsLock,
                        callbacks.onToggleHideLetters,
                        callbacks.onAutoCapitalize,
                        callbacks.onSwitchLanguage,
                        callbacks.onChangePosition,
                        callbacks.onKeyEvent,
                    ),
            ),
        abbreviationBufferEnabled = data.environment.config.abbreviationBufferEnabled,
        onSketchAction = data.onSketchAction,
    )
}
