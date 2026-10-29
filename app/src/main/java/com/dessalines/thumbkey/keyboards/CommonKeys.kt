@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val TOGGLE_CAPS_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
        capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.Copyright),
        action = ToggleCapsLock,
        swipeReturnAction = ToggleCurrentWordCapitalization(true),
        color = MUTED,
    )

val TOGGLE_SHIFT_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
        action = ToggleShiftMode(true),
        swipeReturnAction = ToggleCurrentWordCapitalization(true),
        color = MUTED,
    )

val TOGGLE_SHIFT_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
        action = ToggleShiftMode(false),
        swipeReturnAction = ToggleCurrentWordCapitalization(false),
        color = MUTED,
    )

val TOGGLE_CTRL_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardControlKey),
        action = ToggleCtrlMode(true),
        color = MUTED,
    )

val TOGGLE_CTRL_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardDoubleArrowDown),
        action = ToggleCtrlMode(false),
        color = MUTED,
    )

val TOGGLE_ALT_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardOptionKey),
        action = ToggleAltMode(true),
        color = MUTED,
    )

val TOGGLE_ALT_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardDoubleArrowDown),
        action = ToggleAltMode(false),
        color = MUTED,
    )

val BACKSPACE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
        action = DeleteKeyAction,
        size = LARGE,
        color = SECONDARY,
    )

val BACKSPACE_KEYC_MUTED =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
        action = DeleteKeyAction,
        color = MUTED,
    )
val BACKSPACE_TEXT_MANIPULATION_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
        action = KeyAction.DeleteViaTextManipulation,
        size = LARGE,
        color = SECONDARY,
    )
val DELETE_CHARACTER_AFTER_CURSOR_KEYC =
    KeyC(
        DeleteCharacterAfterCursor,
        display = null,
    )
val DELETE_WORD_BEFORE_CURSOR_KEYC =
    KeyC(
        DeleteWordBeforeCursor,
        display = null,
    )
val DELETE_WORD_AFTER_CURSOR_KEYC =
    KeyC(
        DeleteWordAfterCursor,
        display = null,
    )

val PREVIOUS_WORD_BEFORE_CURSOR_KEYC =
    KeyC(
        PreviousWordBeforeCursor,
        display = null,
    )
val NEXT_WORD_AFTER_CURSOR_KEYC =
    KeyC(
        NextWordAfterCursor,
        display = null,
    )

val BACKSPACE_KEY_ITEM =
    KeyItemC(
        center = BACKSPACE_KEYC,
        swipeType = TWO_WAY_HORIZONTAL,
        slideType = SlideType.DELETE,
        left = DELETE_WORD_BEFORE_CURSOR_KEYC,
        right = DELETE_WORD_AFTER_CURSOR_KEYC,
        backgroundColor = SURFACE_VARIANT,
        longPress = DeleteWordBeforeCursor,
    )

val BACKSPACE_WIDE_KEY_ITEM = BACKSPACE_KEY_ITEM.copy(widthMultiplier = 3)

val RETURN_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardReturn),
        action = IMECompleteAction,
        size = LARGE,
        color = SECONDARY,
    )
val RETURN_KEY_ITEM =
    KeyItemC(
        center = RETURN_KEYC,
        backgroundColor = SURFACE_VARIANT,
        longPress = CommitText("\n"),
    )

val RETURN_TOP_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ESCAPE,
                ),
            ),
        display = null,
    )
val RETURN_LEFT_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_MOVE_HOME,
                ),
            ),
        display = null,
    )
val RETURN_RIGHT_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_MOVE_END,
                ),
            ),
        display = null,
    )

val COMPOSE_COMBO_KEYC =
    KeyC(
        display = KeyDisplay.TextDisplay("♫"),
        action = StartComposeCombo,
        color = MUTED,
    )

val NOOP_KEYC =
    KeyC(
        action = Noop,
        display = null,
    )

val RETURN_PROGRAMMING_KEY_ITEM =
    KeyItemC(
        center = RETURN_KEYC,
        swipeType = FOUR_WAY_CROSS,
        top = RETURN_TOP_KEYC,
        left = RETURN_LEFT_KEYC,
        right = RETURN_RIGHT_KEYC,
        backgroundColor = SURFACE_VARIANT,
        longPress = CommitText("\n"),
    )

val SPACEBAR_TYPESPLIT_MIDDLE_KEY_ITEM =
    KeyItemC(
        center = SPACEBAR_CENTER_KEYC,
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.MOVE_CURSOR,
        left = SPACEBAR_LEFT_KEYC,
        right = SPACEBAR_RIGHT_KEYC,
        top = KeyC("'", color = MUTED),
        bottom = KeyC(",", color = MUTED),
        nextTapActions = SPACEBAR_NEXT_TAP_ACTIONS,
        backgroundColor = SURFACE_VARIANT,
    )
val SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM =
    SPACEBAR_TYPESPLIT_MIDDLE_KEY_ITEM.copy(
        swipeType = EIGHT_WAY,
        left = SPACEBAR_LEFT_KEYC,
        right = SPACEBAR_RIGHT_KEYC,
        top = KeyC("-", color = MUTED),
        bottom = KeyC(".", color = MUTED),
        bottomLeft = KeyC("*", color = MUTED),
    )

val SPACEBAR_ALL_SYMBOLS =
    SPACEBAR_TYPESPLIT_MIDDLE_KEY_ITEM.copy(
        left = KeyC(",", color = MUTED),
        right = KeyC("'", color = MUTED),
        top = KeyC("-", color = MUTED),
        bottom = KeyC(".", color = MUTED),
    )
val SPACEBAR_ALL_DIRECTIONS =
    SPACEBAR_TYPESPLIT_MIDDLE_KEY_ITEM.copy(
        left = SPACEBAR_LEFT_KEYC.copy(display = KeyDisplay.TextDisplay("←"), color = MUTED),
        right = SPACEBAR_RIGHT_KEYC.copy(display = KeyDisplay.TextDisplay("→"), color = MUTED),
        top = SPACEBAR_PROGRAMMING_TOP_KEYC.copy(display = KeyDisplay.TextDisplay("↑"), color = MUTED),
        bottom = SPACEBAR_PROGRAMMING_BOTTOM_KEYC.copy(display = KeyDisplay.TextDisplay("↓"), color = MUTED),
    )

val BACKSPACE_TYPESPLIT_KEY_ITEM =
    KeyItemC(
        center =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
                action =
                    SendEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DEL,
                        ),
                    ),
                size = LARGE,
                color = SECONDARY,
            ),
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.DELETE,
        left = DELETE_WORD_BEFORE_CURSOR_KEYC,
        right = DELETE_WORD_AFTER_CURSOR_KEYC,
        top =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                action = ToggleShiftMode(true),
                color = SECONDARY,
            ),
        widthMultiplier = 3,
        backgroundColor = SURFACE_VARIANT,
    )
val BACKSPACE_TYPESPLIT_SHIFTED_KEY_ITEM =
    BACKSPACE_TYPESPLIT_KEY_ITEM.copy(
        left = DELETE_WORD_BEFORE_CURSOR_KEYC,
        right = DELETE_WORD_AFTER_CURSOR_KEYC,
        top = TOGGLE_CAPS_KEYC.copy(color = SECONDARY),
        bottom = TOGGLE_SHIFT_FALSE_KEYC.copy(color = SECONDARY),
    )
