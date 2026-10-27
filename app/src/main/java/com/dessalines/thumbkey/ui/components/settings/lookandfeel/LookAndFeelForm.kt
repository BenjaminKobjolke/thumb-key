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
import com.dessalines.thumbkey.utils.KeyboardPosition
import com.dessalines.thumbkey.utils.ThemeColor
import com.dessalines.thumbkey.utils.ThemeMode
import com.dessalines.thumbkey.utils.toBool
import com.dessalines.thumbkey.utils.toInt

internal class LookAndFeelForm(
    settings: AppSettings?,
) {
    var themeState = ThemeMode.entries[settings?.theme ?: DEFAULT_THEME]
    var themeColorState = ThemeColor.entries[settings?.themeColor ?: DEFAULT_THEME_COLOR]
    var autoSizeKeysState = (settings?.autoSizeKeys ?: DEFAULT_AUTO_SIZE_KEYS).toBool()
    var nonSquareKeysState = (settings?.nonSquareKeys ?: DEFAULT_NON_SQUARE_KEYS).toBool()
    var keyHeightState = (settings?.keyHeight ?: DEFAULT_KEY_HEIGHT).toFloat()
    var keyWidthState = (settings?.keyWidth ?: DEFAULT_KEY_WIDTH).toFloat()
    var pushupSizeState = (settings?.pushupSize ?: DEFAULT_PUSHUP_SIZE).toFloat()
    var animationSpeedState = (settings?.animationSpeed ?: DEFAULT_ANIMATION_SPEED).toFloat()
    var animationHelperSpeedState = (settings?.animationHelperSpeed ?: DEFAULT_ANIMATION_HELPER_SPEED).toFloat()
    var keyPaddingState = (settings?.keyPadding ?: DEFAULT_KEY_PADDING).toFloat()
    var keyBorderWidthState = (settings?.keyBorderWidth ?: DEFAULT_KEY_BORDER_WIDTH).toFloat()
    var keyRadiusState = (settings?.keyRadius ?: DEFAULT_KEY_RADIUS).toFloat()
    var positionState = KeyboardPosition.entries[settings?.position ?: DEFAULT_POSITION]
    var positionPaddingState = (settings?.positionPadding ?: DEFAULT_POSITION_PADDING).toFloat()
    var vibrateOnTapState = (settings?.vibrateOnTap ?: DEFAULT_VIBRATE_ON_TAP).toBool()
    var vibrateOnSlideState = (settings?.vibrateOnSlide ?: DEFAULT_VIBRATE_ON_SLIDE).toBool()
    var soundOnTapState = (settings?.soundOnTap ?: DEFAULT_SOUND_ON_TAP).toBool()
    var hideLettersState = (settings?.hideLetters ?: DEFAULT_HIDE_LETTERS).toBool()
    var hideSymbolsState = (settings?.hideSymbols ?: DEFAULT_HIDE_SYMBOLS).toBool()
    var ignoreBottomPaddingState = (settings?.ignoreBottomPadding ?: DEFAULT_IGNORE_BOTTOM_PADDING).toBool()
    var showToastOnLayoutSwitchState = (settings?.showToastOnLayoutSwitch ?: DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH).toBool()
    var backdropEnabledState = (settings?.backdropEnabled ?: DEFAULT_BACKDROP_ENABLED).toBool()
    var disableFullscreenEditorState = (settings?.disableFullscreenEditor ?: DEFAULT_DISABLE_FULLSCREEN_EDITOR).toBool()

    val slideEnabled = settings?.slideEnabled?.toBool() == true

    fun toUpdate() =
        LookAndFeelUpdate(
            id = 1,
            pushupSize = pushupSizeState.toInt(),
            animationSpeed = animationSpeedState.toInt(),
            animationHelperSpeed = animationHelperSpeedState.toInt(),
            position = positionState.ordinal,
            positionPadding = positionPaddingState.toInt(),
            vibrateOnTap = vibrateOnTapState.toInt(),
            vibrateOnSlide = vibrateOnSlideState.toInt(),
            soundOnTap = soundOnTapState.toInt(),
            hideLetters = hideLettersState.toInt(),
            hideSymbols = hideSymbolsState.toInt(),
            ignoreBottomPadding = ignoreBottomPaddingState.toInt(),
            theme = themeState.ordinal,
            themeColor = themeColorState.ordinal,
            backdropEnabled = backdropEnabledState.toInt(),
            keyPadding = keyPaddingState.toInt(),
            keyBorderWidth = keyBorderWidthState.toInt(),
            keyRadius = keyRadiusState.toInt(),
            autoSizeKeys = autoSizeKeysState.toInt(),
            nonSquareKeys = nonSquareKeysState.toInt(),
            keyWidth = keyWidthState.toInt(),
            keyHeight = keyHeightState.toInt(),
            showToastOnLayoutSwitch = showToastOnLayoutSwitchState.toInt(),
            disableFullscreenEditor = disableFullscreenEditorState.toInt(),
        )
}
