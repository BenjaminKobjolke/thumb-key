package com.dessalines.thumbkey.db

import androidx.room.ColumnInfo

data class LayoutsUpdate(
    val id: Int,
    @ColumnInfo(
        name = "keyboard_layout",
    )
    val keyboardLayout: Int,
    @ColumnInfo(
        name = "keyboard_layouts",
    )
    val keyboardLayouts: String,
)

data class LookAndFeelUpdate(
    val id: Int,
    @ColumnInfo(
        name = "animation_speed",
    )
    val animationSpeed: Int,
    @ColumnInfo(
        name = "animation_helper_speed",
    )
    val animationHelperSpeed: Int,
    @ColumnInfo(
        name = "position",
    )
    val position: Int,
    @ColumnInfo(
        name = "vibrate_on_tap",
    )
    val vibrateOnTap: Int,
    @ColumnInfo(
        name = "sound_on_tap",
    )
    val soundOnTap: Int,
    @ColumnInfo(
        name = "theme",
    )
    val theme: Int,
    @ColumnInfo(
        name = "theme_color",
    )
    val themeColor: Int,
    @ColumnInfo(
        name = "pushup_size",
    )
    val pushupSize: Int,
    @ColumnInfo(
        name = "hide_letters",
    )
    val hideLetters: Int,
    @ColumnInfo(
        name = "hide_symbols",
    )
    val hideSymbols: Int,
    @ColumnInfo(
        name = "backdrop_enabled",
    )
    val backdropEnabled: Int,
    @ColumnInfo(
        name = "key_padding",
    )
    val keyPadding: Int,
    @ColumnInfo(
        name = "key_border_width",
    )
    val keyBorderWidth: Int,
    @ColumnInfo(
        name = "key_radius",
    )
    val keyRadius: Int,
    @ColumnInfo(
        name = "auto_size_keys",
    )
    val autoSizeKeys: Int,
    @ColumnInfo(
        name = "non_square_keys",
    )
    val nonSquareKeys: Int,
    @ColumnInfo(
        name = "key_width_v18",
    )
    val keyWidth: Int,
    @ColumnInfo(
        name = "key_height_v18",
    )
    val keyHeight: Int,
    @ColumnInfo(
        name = "ignore_bottom_padding",
    )
    val ignoreBottomPadding: Int,
    @ColumnInfo(
        name = "show_toast_on_layout_switch",
    )
    val showToastOnLayoutSwitch: Int,
    @ColumnInfo(
        name = "disable_fullscreen_editor",
    )
    val disableFullscreenEditor: Int,
    @ColumnInfo(
        name = "vibrate_on_slide",
    )
    val vibrateOnSlide: Int,
    @ColumnInfo(
        name = "position_padding",
    )
    val positionPadding: Int,
)

data class BehaviorUpdate(
    val id: Int,
    @ColumnInfo(name = "min_swipe_length")
    val minSwipeLength: Int,
    @ColumnInfo(name = "slide_sensitivity")
    val slideSensitivity: Int,
    @ColumnInfo(name = "slide_enabled")
    val slideEnabled: Int,
    @ColumnInfo(name = "slide_cursor_movement_mode")
    val slideCursorMovementMode: Int,
    @ColumnInfo(name = "slide_spacebar_deadzone_enabled")
    val slideSpacebarDeadzoneEnabled: Int,
    @ColumnInfo(name = "slide_backspace_deadzone_enabled")
    val slideBackspaceDeadzoneEnabled: Int,
    @ColumnInfo(name = "auto_capitalize")
    val autoCapitalize: Int,
    @ColumnInfo(name = "spacebar_multitaps")
    val spacebarMultiTaps: Int,
    @ColumnInfo(name = "switch_to_letters_after_space")
    val switchToLettersAfterSpace: Int = DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE,
    @ColumnInfo(name = "drag_return_enabled")
    val dragReturnEnabled: Int,
    @ColumnInfo(name = "circular_drag_enabled")
    val circularDragEnabled: Int,
    @ColumnInfo(name = "clockwise_drag_action")
    val clockwiseDragAction: Int,
    @ColumnInfo(name = "counterclockwise_drag_action")
    val counterclockwiseDragAction: Int,
    @ColumnInfo(name = "ghost_keys_enabled")
    val ghostKeysEnabled: Int,
    @ColumnInfo(name = "slide_hold_enabled")
    val slideHoldEnabled: Int,
    @ColumnInfo(name = "switch_ime_mode")
    val switchImeMode: Int = DEFAULT_SWITCH_IME_MODE,
)

data class KeyModificationsUpdate(
    val id: Int,
    @ColumnInfo(
        name = "key_modifications",
    )
    val keyModifications: String,
)

data class ClipboardSettingsUpdate(
    val id: Int,
    @ColumnInfo(name = "clipboard_history_enabled")
    val clipboardHistoryEnabled: Int,
    @ColumnInfo(name = "clipboard_auto_cleanup_enabled")
    val clipboardAutoCleanupEnabled: Int,
    @ColumnInfo(name = "clipboard_cleanup_after_minutes")
    val clipboardCleanupAfterMinutes: Int,
    @ColumnInfo(name = "clipboard_size_limit_enabled")
    val clipboardSizeLimitEnabled: Int,
    @ColumnInfo(name = "clipboard_max_size")
    val clipboardMaxSize: Int,
    @ColumnInfo(
        name = "use_private_clipboard",
    )
    val usePrivateClipboard: Int,
)

data class OtherSettingsUpdate(
    val id: Int,
    @ColumnInfo(name = "show_on_screen_keyboard")
    val showOnScreenKeyboard: Int,
)
