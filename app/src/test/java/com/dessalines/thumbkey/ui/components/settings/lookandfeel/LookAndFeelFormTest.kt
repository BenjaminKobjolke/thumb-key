package com.dessalines.thumbkey.ui.components.settings.lookandfeel

import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.DEFAULT_ANIMATION_HELPER_SPEED
import com.dessalines.thumbkey.db.DEFAULT_ANIMATION_SPEED
import com.dessalines.thumbkey.db.DEFAULT_AUTO_SIZE_KEYS
import com.dessalines.thumbkey.db.DEFAULT_BACKDROP_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_DISABLE_FULLSCREEN_EDITOR
import com.dessalines.thumbkey.db.DEFAULT_HIDE_LETTERS
import com.dessalines.thumbkey.db.DEFAULT_HIDE_SYMBOLS
import com.dessalines.thumbkey.db.DEFAULT_IGNORE_BOTTOM_PADDING
import com.dessalines.thumbkey.db.DEFAULT_KEY_BORDER_WIDTH
import com.dessalines.thumbkey.db.DEFAULT_KEY_HEIGHT
import com.dessalines.thumbkey.db.DEFAULT_KEY_PADDING
import com.dessalines.thumbkey.db.DEFAULT_KEY_RADIUS
import com.dessalines.thumbkey.db.DEFAULT_KEY_WIDTH
import com.dessalines.thumbkey.db.DEFAULT_NON_SQUARE_KEYS
import com.dessalines.thumbkey.db.DEFAULT_POSITION
import com.dessalines.thumbkey.db.DEFAULT_POSITION_PADDING
import com.dessalines.thumbkey.db.DEFAULT_PUSHUP_SIZE
import com.dessalines.thumbkey.db.DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH
import com.dessalines.thumbkey.db.DEFAULT_SOUND_ON_TAP
import com.dessalines.thumbkey.db.DEFAULT_THEME
import com.dessalines.thumbkey.db.DEFAULT_THEME_COLOR
import com.dessalines.thumbkey.db.DEFAULT_VIBRATE_ON_SLIDE
import com.dessalines.thumbkey.db.DEFAULT_VIBRATE_ON_TAP
import com.dessalines.thumbkey.db.LookAndFeelUpdate
import org.junit.Assert.assertEquals
import org.junit.Test

class LookAndFeelFormTest {
    @Test(timeout = 30_000)
    fun nullAndStoredDefaultsUseLookAndFeelDefaults() {
        val expected =
            LookAndFeelUpdate(
                id = 1,
                pushupSize = DEFAULT_PUSHUP_SIZE,
                animationSpeed = DEFAULT_ANIMATION_SPEED,
                animationHelperSpeed = DEFAULT_ANIMATION_HELPER_SPEED,
                position = DEFAULT_POSITION,
                positionPadding = DEFAULT_POSITION_PADDING,
                vibrateOnTap = DEFAULT_VIBRATE_ON_TAP,
                vibrateOnSlide = DEFAULT_VIBRATE_ON_SLIDE,
                soundOnTap = DEFAULT_SOUND_ON_TAP,
                hideLetters = DEFAULT_HIDE_LETTERS,
                hideSymbols = DEFAULT_HIDE_SYMBOLS,
                ignoreBottomPadding = DEFAULT_IGNORE_BOTTOM_PADDING,
                theme = DEFAULT_THEME,
                themeColor = DEFAULT_THEME_COLOR,
                backdropEnabled = DEFAULT_BACKDROP_ENABLED,
                keyPadding = DEFAULT_KEY_PADDING,
                keyBorderWidth = DEFAULT_KEY_BORDER_WIDTH,
                keyRadius = DEFAULT_KEY_RADIUS,
                autoSizeKeys = DEFAULT_AUTO_SIZE_KEYS,
                nonSquareKeys = DEFAULT_NON_SQUARE_KEYS,
                keyWidth = DEFAULT_KEY_WIDTH,
                keyHeight = DEFAULT_KEY_HEIGHT,
                showToastOnLayoutSwitch = DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH,
                disableFullscreenEditor = DEFAULT_DISABLE_FULLSCREEN_EDITOR,
            )

        assertEquals(expected, LookAndFeelForm(null).toUpdate())
        assertEquals(expected, LookAndFeelForm(AppSettings(id = 1)).toUpdate())
    }

    @Test(timeout = 30_000)
    fun settingsRoundTripAllLookAndFeelFields() {
        assertEquals(expectedRoundTripUpdate(), LookAndFeelForm(roundTripSettings()).toUpdate())
    }

    private fun roundTripSettings() =
        AppSettings(
            id = 1,
            pushupSize = 37,
            animationSpeed = 111,
            animationHelperSpeed = 222,
            position = 1,
            positionPadding = -17,
            vibrateOnTap = 0,
            vibrateOnSlide = 0,
            soundOnTap = 1,
            hideLetters = 1,
            hideSymbols = 1,
            ignoreBottomPadding = 1,
            theme = 1,
            themeColor = 1,
            backdropEnabled = 1,
            keyPadding = 4,
            keyBorderWidth = 12,
            keyRadius = 56,
            autoSizeKeys = 0,
            nonSquareKeys = 1,
            keyWidth = 83,
            keyHeight = 97,
            showToastOnLayoutSwitch = 0,
            disableFullscreenEditor = 1,
        )

    private fun expectedRoundTripUpdate() =
        LookAndFeelUpdate(
            id = 1,
            pushupSize = 37,
            animationSpeed = 111,
            animationHelperSpeed = 222,
            position = 1,
            positionPadding = -17,
            vibrateOnTap = 0,
            vibrateOnSlide = 0,
            soundOnTap = 1,
            hideLetters = 1,
            hideSymbols = 1,
            ignoreBottomPadding = 1,
            theme = 1,
            themeColor = 1,
            backdropEnabled = 1,
            keyPadding = 4,
            keyBorderWidth = 12,
            keyRadius = 56,
            autoSizeKeys = 0,
            nonSquareKeys = 1,
            keyWidth = 83,
            keyHeight = 97,
            showToastOnLayoutSwitch = 0,
            disableFullscreenEditor = 1,
        )
}
