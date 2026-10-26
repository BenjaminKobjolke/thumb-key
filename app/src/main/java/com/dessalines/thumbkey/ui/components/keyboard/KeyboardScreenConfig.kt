package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.utils.CircularDragAction
import com.dessalines.thumbkey.utils.KeyboardLayout
import com.dessalines.thumbkey.utils.KeyboardPosition
import com.dessalines.thumbkey.utils.getAutoKeyWidth
import com.dessalines.thumbkey.utils.toBool

/** Values resolved from saved settings before the keyboard draws. */
data class KeyboardScreenConfig(
    val layout: KeyboardLayout,
    val keyMods: String?,
    val position: KeyboardPosition,
    val positionPadding: Int,
    val pushupSize: Int,
    val ignoreBottomPadding: Boolean,
    val autoCapitalize: Boolean,
    val spacebarMultiTaps: Boolean,
    val switchToLettersAfterSpace: Boolean,
    val slideEnabled: Boolean,
    val slideCursorMovementMode: Int,
    val slideSpacebarDeadzoneEnabled: Boolean,
    val slideBackspaceDeadzoneEnabled: Boolean,
    val keyBorderWidth: Int,
    val vibrateOnTap: Boolean,
    val vibrateOnSlide: Boolean,
    val soundOnTap: Boolean,
    val hideLetters: Boolean,
    val hideSymbols: Boolean,
    val backdropEnabled: Boolean,
    val keyPadding: Int,
    val legendWidth: Int,
    val legendHeight: Int,
    val keyRadius: Int,
    val dragReturnEnabled: Boolean,
    val circularDragEnabled: Boolean,
    val clockwiseDragAction: CircularDragAction,
    val counterclockwiseDragAction: CircularDragAction,
    val ghostKeysEnabled: Boolean,
    val abbreviationBufferEnabled: Boolean,
    val wordSuggestionsEnabled: Boolean,
    val wordCombinationsEnabled: Boolean,
    val slideHoldEnabled: Boolean,
    val animationSpeed: Int,
    val animationHelperSpeed: Int,
    val minSwipeLength: Int,
    val slideSensitivity: Int,
    val clipboardHistoryEnabled: Boolean,
) {
    private data class KeyDimensions(
        val width: Int,
        val height: Int,
    )

    companion object {
        private fun keyDimensions(
            settings: AppSettings,
            context: Context,
        ): KeyDimensions {
            val width =
                if (settings.autoSizeKeys.toBool()) {
                    getAutoKeyWidth(settings.keyboardLayout, settings.keyPadding, KeyboardPosition.entries[settings.position], context)
                } else {
                    settings.keyWidth
                }
            val height = if (settings.nonSquareKeys.toBool()) settings.keyHeight else width
            return KeyDimensions(width, height)
        }

        fun from(
            settings: AppSettings,
            context: Context,
        ): KeyboardScreenConfig {
            val keyboardLayout = settings.keyboardLayout
            val position = KeyboardPosition.entries[settings.position]
            val keyPadding = settings.keyPadding
            val dimensions = keyDimensions(settings, context)

            return KeyboardScreenConfig(
                layout = KeyboardLayout.entries[keyboardLayout],
                keyMods = settings.keyModifications,
                position = position,
                positionPadding = settings.positionPadding,
                pushupSize = settings.pushupSize,
                ignoreBottomPadding = settings.ignoreBottomPadding.toBool(),
                autoCapitalize = settings.autoCapitalize.toBool(),
                spacebarMultiTaps = settings.spacebarMultiTaps.toBool(),
                switchToLettersAfterSpace = settings.switchToLettersAfterSpace.toBool(),
                slideEnabled = settings.slideEnabled.toBool(),
                slideCursorMovementMode = settings.slideCursorMovementMode,
                slideSpacebarDeadzoneEnabled = settings.slideSpacebarDeadzoneEnabled.toBool(),
                slideBackspaceDeadzoneEnabled = settings.slideBackspaceDeadzoneEnabled.toBool(),
                keyBorderWidth = settings.keyBorderWidth,
                vibrateOnTap = settings.vibrateOnTap.toBool(),
                vibrateOnSlide = settings.vibrateOnSlide.toBool(),
                soundOnTap = settings.soundOnTap.toBool(),
                hideLetters = settings.hideLetters.toBool(),
                hideSymbols = settings.hideSymbols.toBool(),
                backdropEnabled = settings.backdropEnabled.toBool(),
                keyPadding = keyPadding,
                legendWidth = dimensions.width,
                legendHeight = dimensions.height,
                keyRadius = settings.keyRadius,
                dragReturnEnabled = settings.dragReturnEnabled.toBool(),
                circularDragEnabled = settings.circularDragEnabled.toBool(),
                clockwiseDragAction = CircularDragAction.entries[settings.clockwiseDragAction],
                counterclockwiseDragAction = CircularDragAction.entries[settings.counterclockwiseDragAction],
                ghostKeysEnabled = settings.ghostKeysEnabled.toBool(),
                abbreviationBufferEnabled = settings.abbreviationBufferEnabled.toBool(),
                wordSuggestionsEnabled = settings.wordSuggestionsEnabled.toBool(),
                wordCombinationsEnabled = settings.wordCombinationsEnabled.toBool(),
                slideHoldEnabled = settings.slideHoldEnabled.toBool(),
                animationSpeed = settings.animationSpeed,
                animationHelperSpeed = settings.animationHelperSpeed,
                minSwipeLength = settings.minSwipeLength,
                slideSensitivity = settings.slideSensitivity,
                clipboardHistoryEnabled = settings.clipboardHistoryEnabled.toBool(),
            )
        }
    }
}
