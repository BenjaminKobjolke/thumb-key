package com.dessalines.thumbkey.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AppSettingsDefaultsTest {
    private val settings = AppSettings(id = 1)

    @Test
    fun layoutAndLookDefaultsMatchTheExistingSettings() {
        assertEquals(expectedLayoutAndLookDefaults(), actualLayoutAndLookDefaults())
        assertNull(settings.keyWidthDefunct)
        assertEquals("0", settings.keyboardLayouts)
    }

    private fun expectedLayoutAndLookDefaults() =
        listOf(
            DEFAULT_KEY_WIDTH,
            DEFAULT_ANIMATION_SPEED,
            DEFAULT_ANIMATION_HELPER_SPEED,
            DEFAULT_POSITION,
            DEFAULT_AUTO_CAPITALIZE,
            DEFAULT_KEYBOARD_LAYOUT,
            DEFAULT_VIBRATE_ON_TAP,
            DEFAULT_SOUND_ON_TAP,
            DEFAULT_THEME,
            DEFAULT_THEME_COLOR,
            DEFAULT_MIN_SWIPE_LENGTH,
            DEFAULT_PUSHUP_SIZE,
            DEFAULT_HIDE_LETTERS,
            DEFAULT_KEY_BORDERS,
            DEFAULT_HIDE_SYMBOLS,
            DEFAULT_BACKDROP_ENABLED,
            DEFAULT_KEY_PADDING,
            DEFAULT_KEY_BORDER_WIDTH,
            DEFAULT_KEY_RADIUS,
            DEFAULT_AUTO_SIZE_KEYS,
            DEFAULT_NON_SQUARE_KEYS,
            DEFAULT_KEY_WIDTH,
            DEFAULT_KEY_HEIGHT,
            DEFAULT_IGNORE_BOTTOM_PADDING,
            DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH,
            DEFAULT_DISABLE_FULLSCREEN_EDITOR,
        )

    private fun actualLayoutAndLookDefaults() =
        listOf(
            settings.keySizeDefunct,
            settings.animationSpeed,
            settings.animationHelperSpeed,
            settings.position,
            settings.autoCapitalize,
            settings.keyboardLayout,
            settings.vibrateOnTap,
            settings.soundOnTap,
            settings.theme,
            settings.themeColor,
            settings.minSwipeLength,
            settings.pushupSize,
            settings.hideLetters,
            settings.keyBorders,
            settings.hideSymbols,
            settings.backdropEnabled,
            settings.keyPadding,
            settings.keyBorderWidth,
            settings.keyRadius,
            settings.autoSizeKeys,
            settings.nonSquareKeys,
            settings.keyWidth,
            settings.keyHeight,
            settings.ignoreBottomPadding,
            settings.showToastOnLayoutSwitch,
            settings.disableFullscreenEditor,
        )

    @Test
    fun behaviorAndSlideDefaultsMatchTheExistingSettings() {
        assertEquals(
            listOf(
                DEFAULT_SLIDE_ENABLED,
                DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE,
                DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED,
                DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED,
                DEFAULT_SLIDE_SENSITIVITY,
                DEFAULT_SLIDE_HOLD_ENABLED,
                DEFAULT_VIBRATE_ON_SLIDE,
                DEFAULT_SPACEBAR_MULTITAPS,
                DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE,
                DEFAULT_DRAG_RETURN_ENABLED,
                DEFAULT_CIRCULAR_DRAG_ENABLED,
                DEFAULT_CLOCKWISE_DRAG_ACTION,
                DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION,
                DEFAULT_GHOST_KEYS_ENABLED,
                DEFAULT_SWITCH_IME_MODE,
            ),
            listOf(
                settings.slideEnabled,
                settings.slideCursorMovementMode,
                settings.slideSpacebarDeadzoneEnabled,
                settings.slideBackspaceDeadzoneEnabled,
                settings.slideSensitivity,
                settings.slideHoldEnabled,
                settings.vibrateOnSlide,
                settings.spacebarMultiTaps,
                settings.switchToLettersAfterSpace,
                settings.dragReturnEnabled,
                settings.circularDragEnabled,
                settings.clockwiseDragAction,
                settings.counterclockwiseDragAction,
                settings.ghostKeysEnabled,
                settings.switchImeMode,
            ),
        )
    }

    @Test
    fun clipboardAndSuggestionDefaultsMatchTheExistingSettings() {
        assertEquals(
            listOf(
                DEFAULT_ABBREVIATION_BUFFER_ENABLED,
                DEFAULT_WORD_SUGGESTIONS_ENABLED,
                DEFAULT_WORD_COMBINATIONS_ENABLED,
                DEFAULT_CLIPBOARD_HISTORY_ENABLED,
                DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED,
                DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES,
                DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED,
                DEFAULT_CLIPBOARD_MAX_SIZE,
                DEFAULT_USE_PRIVATE_CLIPBOARD,
                DEFAULT_SHOW_ON_SCREEN_KEYBOARD,
                DEFAULT_POSITION_PADDING,
            ),
            listOf(
                settings.abbreviationBufferEnabled,
                settings.wordSuggestionsEnabled,
                settings.wordCombinationsEnabled,
                settings.clipboardHistoryEnabled,
                settings.clipboardAutoCleanupEnabled,
                settings.clipboardCleanupAfterMinutes,
                settings.clipboardSizeLimitEnabled,
                settings.clipboardMaxSize,
                settings.usePrivateClipboard,
                settings.showOnScreenKeyboard,
                settings.positionPadding,
            ),
        )
        assertEquals("", settings.keyModifications)
    }
}
