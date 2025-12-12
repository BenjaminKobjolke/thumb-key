@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_DE_TYPESPLITBEN_MAIN =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("e", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("q"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("q"),
                        ),
                    bottom = KeyC("w"),
                ),
                KeyItemC(
                    center = KeyC("r", size = LARGE),
                ),
                EMOJI_KEY_ITEM_ALT,
                KeyItemC(
                    center = KeyC("t", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("z"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("z"),
                        ),
                    bottom = KeyC("u"),
                    top = KeyC("ü", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("i", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("p"),
                        ),
                    left = KeyC("p"),
                    bottom = KeyC("o"),
                    top = KeyC("ö", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("a", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = null,
                            action = CommitText("ä"),
                        ),
                    top = KeyC("ä", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("s", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("ß", color = MUTED),
                    top =
                        KeyC(
                            display = null,
                            action = CommitText("ß"),
                        ),
                ),
                KeyItemC(
                    center = KeyC(" "),
                    swipeType = FOUR_WAY_CROSS,
                    slideType = SlideType.DELETE,
                    left =
                        KeyC(
                            DeleteWordBeforeCursor,
                            display = KeyDisplay.TextDisplay("←×"),
                        ),
                    right =
                        KeyC(
                            DeleteWordAfterCursor,
                            display = KeyDisplay.TextDisplay("×→"),
                        ),
                    top = KeyC("'", color = MUTED),
                    bottom = KeyC(",", color = MUTED),
                    nextTapActions =
                        listOf(
                            ReplaceLastText(", ", trimCount = 1),
                            ReplaceLastText(". "),
                            ReplaceLastText("? "),
                            ReplaceLastText("! "),
                            ReplaceLastText(": "),
                            ReplaceLastText("; "),
                        ),
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("d", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("f"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("f"),
                        ),
                    bottom = KeyC("g"),
                ),
                KeyItemC(
                    center = KeyC("h", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("k"),
                        ),
                    left = KeyC("k"),
                    bottom = KeyC("l"),
                    top = KeyC("j"),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("c", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("x"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("x"),
                        ),
                    bottom = KeyC("y"),
                ),
                KeyItemC(
                    center = KeyC("b", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("v"),
                ),
                SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM.copy(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Mic),
                            action = SwitchIMEVoice,
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center = KeyC("n", size = LARGE),
                ),
                KeyItemC(
                    center = KeyC("m", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    backgroundColor = SURFACE_VARIANT,
                    swipeType = EIGHT_WAY,
                    center = NUMERIC_KEY_ITEM.center,
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Settings),
                            action = GotoSettings,
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Keyboard),
                            action = SwitchIME,
                            color = MUTED,
                        ),
                    left =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Language),
                            action = SwitchLanguage,
                            color = MUTED,
                        ),
                    right =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.LinearScale),
                            action = MoveKeyboard.CycleRight,
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
                            action = DeleteKeyAction,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = FOUR_WAY_CROSS,
                    slideType = SlideType.MOVE_CURSOR,
                    left =
                        KeyC(
                            display = KeyDisplay.TextDisplay("←"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_DPAD_LEFT,
                                    ),
                                ),
                            color = MUTED,
                        ),
                    right =
                        KeyC(
                            display = KeyDisplay.TextDisplay("→"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_DPAD_RIGHT,
                                    ),
                                ),
                            color = MUTED,
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            action = ToggleShiftMode(true),
                            color = MUTED,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    widthMultiplier = 3,
                    longPress = DeleteWordBeforeCursor,
                ),
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_DE_TYPESPLITBEN_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("E", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("Q"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("Q"),
                        ),
                    bottom = KeyC("W"),
                ),
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                ),
                EMOJI_KEY_ITEM_ALT,
                KeyItemC(
                    center = KeyC("T", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("Z"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("Z"),
                        ),
                    bottom = KeyC("U"),
                    top = KeyC("Ü", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("P"),
                        ),
                    left = KeyC("P"),
                    bottom = KeyC("O"),
                    top = KeyC("Ö", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("A", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = null,
                            action = CommitText("Ä"),
                        ),
                    top = KeyC("Ä", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("S", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("ẞ", color = MUTED),
                    top =
                        KeyC(
                            display = null,
                            action = CommitText("ẞ"),
                        ),
                ),
                KeyItemC(
                    center = KeyC(" "),
                    swipeType = FOUR_WAY_CROSS,
                    slideType = SlideType.DELETE,
                    left =
                        KeyC(
                            DeleteWordBeforeCursor,
                            display = KeyDisplay.TextDisplay("←×"),
                        ),
                    right =
                        KeyC(
                            DeleteWordAfterCursor,
                            display = KeyDisplay.TextDisplay("×→"),
                        ),
                    top = KeyC("'", color = MUTED),
                    bottom = KeyC(",", color = MUTED),
                    nextTapActions =
                        listOf(
                            ReplaceLastText(", ", trimCount = 1),
                            ReplaceLastText(". "),
                            ReplaceLastText("? "),
                            ReplaceLastText("! "),
                            ReplaceLastText(": "),
                            ReplaceLastText("; "),
                        ),
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("D", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("F"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("F"),
                        ),
                    bottom = KeyC("G"),
                ),
                KeyItemC(
                    center = KeyC("H", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("K"),
                        ),
                    left = KeyC("K"),
                    bottom = KeyC("L"),
                    top = KeyC("J"),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("C", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("X"),
                    left =
                        KeyC(
                            display = null,
                            action = CommitText("X"),
                        ),
                    bottom = KeyC("Y"),
                ),
                KeyItemC(
                    center = KeyC("B", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom = KeyC("V"),
                ),
                SPACEBAR_TYPESPLIT_BOTTOM_KEY_ITEM.copy(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Mic),
                            action = SwitchIMEVoice,
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center = KeyC("N", size = LARGE),
                ),
                KeyItemC(
                    center = KeyC("M", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    backgroundColor = SURFACE_VARIANT,
                    swipeType = EIGHT_WAY,
                    center = NUMERIC_KEY_ITEM.center,
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Settings),
                            action = GotoSettings,
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Keyboard),
                            action = SwitchIME,
                            color = MUTED,
                        ),
                    left =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Language),
                            action = SwitchLanguage,
                            color = MUTED,
                        ),
                    right =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.LinearScale),
                            action = MoveKeyboard.CycleRight,
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
                            action = DeleteKeyAction,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = FOUR_WAY_CROSS,
                    slideType = SlideType.MOVE_CURSOR,
                    left =
                        KeyC(
                            display = KeyDisplay.TextDisplay("←"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_DPAD_LEFT,
                                    ),
                                ),
                            color = MUTED,
                        ),
                    right =
                        KeyC(
                            display = KeyDisplay.TextDisplay("→"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_DPAD_RIGHT,
                                    ),
                                ),
                            color = MUTED,
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            action = ToggleShiftMode(true),
                            color = MUTED,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    widthMultiplier = 3,
                    longPress = DeleteWordBeforeCursor,
                ),
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_DE_TYPESPLITBEN: KeyboardDefinition =
    KeyboardDefinition(
        title = "deutsch type-split-ben",
        modes =
            KeyboardDefinitionModes(
                main = KB_DE_TYPESPLITBEN_MAIN,
                shifted = KB_DE_TYPESPLITBEN_SHIFTED,
                numeric = TYPESPLIT_NUMERIC_KEYBOARD,
            ),
    )
