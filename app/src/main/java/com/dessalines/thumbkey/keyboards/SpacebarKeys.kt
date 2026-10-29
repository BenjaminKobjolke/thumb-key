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

val SPACEBAR_TOP_KEYC =
    KeyC(
        action = CursorToLineStart,
        swipeReturnAction = CursorToTextStart,
        display = null,
    )

val SPACEBAR_BOTTOM_KEYC =
    KeyC(
        action = CursorToLineEnd,
        swipeReturnAction = CursorToTextEnd,
        display = null,
    )

val SPACEBAR_LEFT_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DPAD_LEFT,
                ),
            ),
        display = null,
    )
val SPACEBAR_RIGHT_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DPAD_RIGHT,
                ),
            ),
        display = null,
    )
val SPACEBAR_NEXT_TAP_ACTIONS =
    listOf(
        ReplaceLastText(", ", trimCount = 1),
        ReplaceLastText(". "),
        ReplaceLastText("? "),
        ReplaceLastText("! "),
        ReplaceLastText(": "),
        ReplaceLastText("; "),
    )
val SPACEBAR_CENTER_KEYC = KeyC(" ")
val SPACEBAR_CENTER_KEYC_UNDERSCORE =
    KeyC(
        text = " ",
        display = KeyDisplay.IconDisplay(Icons.Default.SpaceBar),
        color = MUTED,
    )
val SPACEBAR_KEY_ITEM =
    KeyItemC(
        center = SPACEBAR_CENTER_KEYC,
        swipeType = SwipeNWay.EIGHT_WAY,
        slideType = SlideType.MOVE_CURSOR,
        top = SPACEBAR_TOP_KEYC,
        bottom = SPACEBAR_BOTTOM_KEYC,
        left = SPACEBAR_LEFT_KEYC,
        right = SPACEBAR_RIGHT_KEYC,
        bottomLeft = PREVIOUS_WORD_BEFORE_CURSOR_KEYC,
        bottomRight = NEXT_WORD_AFTER_CURSOR_KEYC,
        nextTapActions = SPACEBAR_NEXT_TAP_ACTIONS,
        backgroundColor = SURFACE_VARIANT,
        widthMultiplier = 3,
    )
val SPACEBAR_SKINNY_KEY_ITEM = SPACEBAR_KEY_ITEM.copy(widthMultiplier = 1)
val SPACEBAR_DOUBLE_KEY_ITEM = SPACEBAR_KEY_ITEM.copy(widthMultiplier = 2)

val SPACEBAR_PROGRAMMING_TOP_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DPAD_UP,
                ),
            ),
        display = null,
    )
val SPACEBAR_PROGRAMMING_BOTTOM_KEYC =
    KeyC(
        action =
            SendEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DPAD_DOWN,
                ),
            ),
        display = null,
    )
val SPACEBAR_PROGRAMMING_KEY_ITEM =
    KeyItemC(
        center = SPACEBAR_CENTER_KEYC,
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.MOVE_CURSOR,
        left = SPACEBAR_LEFT_KEYC,
        right = SPACEBAR_RIGHT_KEYC,
        top = SPACEBAR_PROGRAMMING_TOP_KEYC,
        bottom = SPACEBAR_PROGRAMMING_BOTTOM_KEYC,
        backgroundColor = SURFACE_VARIANT,
        widthMultiplier = 3,
    )

fun cursorMoveSpacebarKeyItem(widthMultiplier: Int) =
    KeyItemC(
        center = KeyC(" "),
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.MOVE_CURSOR,
        left =
            KeyC(
                display = KeyDisplay.TextDisplay("←"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)),
                color = MUTED,
            ),
        right =
            KeyC(
                display = KeyDisplay.TextDisplay("→"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)),
                color = MUTED,
            ),
        top =
            KeyC(
                display = KeyDisplay.TextDisplay("↑"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)),
                color = MUTED,
            ),
        bottom =
            KeyC(
                display = KeyDisplay.TextDisplay("↓"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)),
                color = MUTED,
            ),
        nextTapActions = SPACEBAR_NEXT_TAP_ACTIONS,
        backgroundColor = SURFACE_VARIANT,
        widthMultiplier = widthMultiplier,
    )
