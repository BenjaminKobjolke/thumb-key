package com.dessalines.thumbkey.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppSettings(
    @PrimaryKey(autoGenerate = true) val id: Int,
    // These columns exist in the database schema because we can't drop them without recreating tables.
    // They still have to be handled nearly identically to active columns, but they are actually defunct.
    @ColumnInfo(
        name = "key_size_defunct",
        defaultValue = DEFAULT_KEY_WIDTH.toString(),
    )
    val keySizeDefunct: Int = DEFAULT_KEY_WIDTH,
    @ColumnInfo(
        name = "key_width_defunct",
    )
    val keyWidthDefunct: Int? = null,
    @ColumnInfo(
        name = "animation_speed",
        defaultValue = DEFAULT_ANIMATION_SPEED.toString(),
    )
    val animationSpeed: Int = DEFAULT_ANIMATION_SPEED,
    @ColumnInfo(
        name = "animation_helper_speed",
        defaultValue = DEFAULT_ANIMATION_HELPER_SPEED.toString(),
    )
    val animationHelperSpeed: Int = DEFAULT_ANIMATION_HELPER_SPEED,
    @ColumnInfo(
        name = "position",
        defaultValue = DEFAULT_POSITION.toString(),
    )
    val position: Int = DEFAULT_POSITION,
    @ColumnInfo(
        name = "auto_capitalize",
        defaultValue = DEFAULT_AUTO_CAPITALIZE.toString(),
    )
    val autoCapitalize: Int = DEFAULT_AUTO_CAPITALIZE,
    @ColumnInfo(
        name = "keyboard_layout",
        defaultValue = DEFAULT_KEYBOARD_LAYOUT.toString(),
    )
    val keyboardLayout: Int = DEFAULT_KEYBOARD_LAYOUT,
    @ColumnInfo(
        name = "vibrate_on_tap",
        defaultValue = DEFAULT_VIBRATE_ON_TAP.toString(),
    )
    val vibrateOnTap: Int = DEFAULT_VIBRATE_ON_TAP,
    @ColumnInfo(
        name = "slide_enabled",
        defaultValue = DEFAULT_SLIDE_ENABLED.toString(),
    )
    val slideEnabled: Int = DEFAULT_SLIDE_ENABLED,
    @ColumnInfo(
        name = "slide_cursor_movement_mode",
        defaultValue = DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE.toString(),
    )
    val slideCursorMovementMode: Int = DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE,
    @ColumnInfo(
        name = "slide_spacebar_deadzone_enabled",
        defaultValue = DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED.toString(),
    )
    val slideSpacebarDeadzoneEnabled: Int = DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED,
    @ColumnInfo(
        name = "slide_backspace_deadzone_enabled",
        defaultValue = DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED.toString(),
    )
    val slideBackspaceDeadzoneEnabled: Int = DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED,
    @ColumnInfo(
        name = "sound_on_tap",
        defaultValue = DEFAULT_SOUND_ON_TAP.toString(),
    )
    val soundOnTap: Int = DEFAULT_SOUND_ON_TAP,
    @ColumnInfo(
        name = "theme",
        defaultValue = DEFAULT_THEME.toString(),
    )
    val theme: Int = DEFAULT_THEME,
    @ColumnInfo(
        name = "theme_color",
        defaultValue = DEFAULT_THEME_COLOR.toString(),
    )
    val themeColor: Int = DEFAULT_THEME_COLOR,
    // TODO get rid of this column next time you regenerate the app
    @ColumnInfo(
        name = "viewed_changelog",
        defaultValue = "0",
    )
    val viewedChangelog: Int = 0,
    @ColumnInfo(
        name = "min_swipe_length",
        defaultValue = DEFAULT_MIN_SWIPE_LENGTH.toString(),
    )
    val minSwipeLength: Int = DEFAULT_MIN_SWIPE_LENGTH,
    @ColumnInfo(
        name = "slide_sensitivity",
        defaultValue = DEFAULT_SLIDE_SENSITIVITY.toString(),
    )
    val slideSensitivity: Int = DEFAULT_SLIDE_SENSITIVITY,
    @ColumnInfo(
        name = "pushup_size",
        defaultValue = DEFAULT_PUSHUP_SIZE.toString(),
    )
    val pushupSize: Int = DEFAULT_PUSHUP_SIZE,
    @ColumnInfo(
        name = "hide_letters",
        defaultValue = DEFAULT_HIDE_LETTERS.toString(),
    )
    val hideLetters: Int = DEFAULT_HIDE_LETTERS,
    @ColumnInfo(
        name = "keyboard_layouts",
        defaultValue = "$DEFAULT_KEYBOARD_LAYOUT",
    )
    val keyboardLayouts: String = "$DEFAULT_KEYBOARD_LAYOUT",
    // TODO this needs to be gotten rid of in the next DB update
    @ColumnInfo(
        name = "key_borders",
        defaultValue = DEFAULT_KEY_BORDERS.toString(),
    )
    val keyBorders: Int = DEFAULT_KEY_BORDERS,
    @ColumnInfo(
        name = "spacebar_multitaps",
        defaultValue = DEFAULT_SPACEBAR_MULTITAPS.toString(),
    )
    val spacebarMultiTaps: Int = DEFAULT_SPACEBAR_MULTITAPS,
    @ColumnInfo(
        name = "hide_symbols",
        defaultValue = DEFAULT_HIDE_SYMBOLS.toString(),
    )
    val hideSymbols: Int = DEFAULT_HIDE_SYMBOLS,
    @ColumnInfo(
        name = "last_version_code_viewed",
        defaultValue = "0",
    )
    val lastVersionCodeViewed: Int = 0,
    @ColumnInfo(
        name = "backdrop_enabled",
        defaultValue = DEFAULT_BACKDROP_ENABLED.toString(),
    )
    val backdropEnabled: Int = DEFAULT_BACKDROP_ENABLED,
    @ColumnInfo(
        name = "key_padding",
        defaultValue = DEFAULT_KEY_PADDING.toString(),
    )
    val keyPadding: Int = DEFAULT_KEY_PADDING,
    @ColumnInfo(
        name = "key_border_width",
        defaultValue = DEFAULT_KEY_BORDER_WIDTH.toString(),
    )
    val keyBorderWidth: Int = DEFAULT_KEY_BORDER_WIDTH,
    @ColumnInfo(
        name = "key_radius",
        defaultValue = DEFAULT_KEY_RADIUS.toString(),
    )
    val keyRadius: Int = DEFAULT_KEY_RADIUS,
    @ColumnInfo(
        name = "drag_return_enabled",
        defaultValue = DEFAULT_DRAG_RETURN_ENABLED.toString(),
    )
    val dragReturnEnabled: Int = DEFAULT_DRAG_RETURN_ENABLED,
    @ColumnInfo(
        name = "circular_drag_enabled",
        defaultValue = DEFAULT_CIRCULAR_DRAG_ENABLED.toString(),
    )
    val circularDragEnabled: Int = DEFAULT_CIRCULAR_DRAG_ENABLED,
    @ColumnInfo(
        name = "clockwise_drag_action",
        defaultValue = DEFAULT_CLOCKWISE_DRAG_ACTION.toString(),
    )
    val clockwiseDragAction: Int = DEFAULT_CLOCKWISE_DRAG_ACTION,
    @ColumnInfo(
        name = "counterclockwise_drag_action",
        defaultValue = DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION.toString(),
    )
    val counterclockwiseDragAction: Int = DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION,
    @ColumnInfo(
        name = "ghost_keys_enabled",
        defaultValue = DEFAULT_GHOST_KEYS_ENABLED.toString(),
    )
    val ghostKeysEnabled: Int = DEFAULT_GHOST_KEYS_ENABLED,
    @ColumnInfo(
        name = "slide_hold_enabled",
        defaultValue = DEFAULT_SLIDE_HOLD_ENABLED.toString(),
    )
    val slideHoldEnabled: Int = DEFAULT_SLIDE_HOLD_ENABLED,
    @ColumnInfo(
        name = "key_modifications",
        defaultValue = "",
    )
    val keyModifications: String = "",
    @ColumnInfo(
        name = "auto_size_keys",
        defaultValue = DEFAULT_AUTO_SIZE_KEYS.toString(),
    )
    val autoSizeKeys: Int = DEFAULT_AUTO_SIZE_KEYS,
    @ColumnInfo(
        name = "non_square_keys",
        defaultValue = DEFAULT_NON_SQUARE_KEYS.toString(),
    )
    val nonSquareKeys: Int = DEFAULT_NON_SQUARE_KEYS,
    @ColumnInfo(
        name = "key_width_v18",
        defaultValue = DEFAULT_KEY_WIDTH.toString(),
    )
    val keyWidth: Int = DEFAULT_KEY_WIDTH,
    @ColumnInfo(
        name = "key_height_v18",
        defaultValue = DEFAULT_KEY_HEIGHT.toString(),
    )
    val keyHeight: Int = DEFAULT_KEY_HEIGHT,
    @ColumnInfo(
        name = "ignore_bottom_padding",
        defaultValue = DEFAULT_IGNORE_BOTTOM_PADDING.toString(),
    )
    val ignoreBottomPadding: Int = DEFAULT_IGNORE_BOTTOM_PADDING,
    @ColumnInfo(
        name = "show_toast_on_layout_switch",
        defaultValue = DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH.toString(),
    )
    val showToastOnLayoutSwitch: Int = DEFAULT_SHOW_TOAST_ON_LAYOUT_SWITCH,
    @ColumnInfo(
        name = "disable_fullscreen_editor",
        defaultValue = DEFAULT_DISABLE_FULLSCREEN_EDITOR.toString(),
    )
    val disableFullscreenEditor: Int = DEFAULT_DISABLE_FULLSCREEN_EDITOR,
    @ColumnInfo(
        name = "abbreviation_buffer_enabled",
        defaultValue = DEFAULT_ABBREVIATION_BUFFER_ENABLED.toString(),
    )
    val abbreviationBufferEnabled: Int = DEFAULT_ABBREVIATION_BUFFER_ENABLED,
    @ColumnInfo(
        name = "word_suggestions_enabled",
        defaultValue = DEFAULT_WORD_SUGGESTIONS_ENABLED.toString(),
    )
    val wordSuggestionsEnabled: Int = DEFAULT_WORD_SUGGESTIONS_ENABLED,
    @ColumnInfo(
        name = "word_combinations_enabled",
        defaultValue = DEFAULT_WORD_COMBINATIONS_ENABLED.toString(),
    )
    val wordCombinationsEnabled: Int = DEFAULT_WORD_COMBINATIONS_ENABLED,
    @ColumnInfo(
        name = "vibrate_on_slide",
        defaultValue = DEFAULT_VIBRATE_ON_SLIDE.toString(),
    )
    val vibrateOnSlide: Int = DEFAULT_VIBRATE_ON_SLIDE,
    @ColumnInfo(
        name = "clipboard_history_enabled",
        defaultValue = DEFAULT_CLIPBOARD_HISTORY_ENABLED.toString(),
    )
    val clipboardHistoryEnabled: Int = DEFAULT_CLIPBOARD_HISTORY_ENABLED,
    @ColumnInfo(
        name = "clipboard_auto_cleanup_enabled",
        defaultValue = DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED.toString(),
    )
    val clipboardAutoCleanupEnabled: Int = DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED,
    @ColumnInfo(
        name = "clipboard_cleanup_after_minutes",
        defaultValue = DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES.toString(),
    )
    val clipboardCleanupAfterMinutes: Int = DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES,
    @ColumnInfo(
        name = "clipboard_size_limit_enabled",
        defaultValue = DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED.toString(),
    )
    val clipboardSizeLimitEnabled: Int = DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED,
    @ColumnInfo(
        name = "clipboard_max_size",
        defaultValue = DEFAULT_CLIPBOARD_MAX_SIZE.toString(),
    )
    val clipboardMaxSize: Int = DEFAULT_CLIPBOARD_MAX_SIZE,
    @ColumnInfo(
        name = "position_padding",
        defaultValue = DEFAULT_POSITION_PADDING.toString(),
    )
    val positionPadding: Int = DEFAULT_POSITION_PADDING,
    @ColumnInfo(
        name = "use_private_clipboard",
        defaultValue = DEFAULT_USE_PRIVATE_CLIPBOARD.toString(),
    )
    val usePrivateClipboard: Int = DEFAULT_USE_PRIVATE_CLIPBOARD,
    @ColumnInfo(
        name = "show_on_screen_keyboard",
        defaultValue = DEFAULT_SHOW_ON_SCREEN_KEYBOARD.toString(),
    )
    val showOnScreenKeyboard: Int = DEFAULT_SHOW_ON_SCREEN_KEYBOARD,
    @ColumnInfo(
        name = "switch_to_letters_after_space",
        defaultValue = DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE.toString(),
    )
    val switchToLettersAfterSpace: Int = DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE,
    @ColumnInfo(
        name = "switch_ime_mode",
        defaultValue = DEFAULT_SWITCH_IME_MODE.toString(),
    )
    val switchImeMode: Int = DEFAULT_SWITCH_IME_MODE,
)
