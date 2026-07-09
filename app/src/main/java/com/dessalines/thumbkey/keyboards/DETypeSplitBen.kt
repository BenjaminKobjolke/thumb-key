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
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                            action = ToggleClipboardMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = EIGHT_WAY,
                    top = COPY_KEYC,
                    topLeft = SELECT_ALL_KEYC,
                    topRight = CUT_KEYC,
                    bottom = PASTE_KEYC,
                    bottomLeft = UNDO_KEYC,
                    bottomRight = REDO_KEYC,
                    backgroundColor = SURFACE_VARIANT,
                ),
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
                    top = KeyC("\"", color = MUTED),
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
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("m", size = LARGE),
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
                    widthMultiplier = 2,
                    longPress = DeleteWordBeforeCursor,
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("Entf"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_FORWARD_DEL,
                                    ),
                                ),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordAfterCursor,
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
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                            action = ToggleClipboardMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = EIGHT_WAY,
                    top = COPY_KEYC,
                    topLeft = SELECT_ALL_KEYC,
                    topRight = CUT_KEYC,
                    bottom = PASTE_KEYC,
                    bottomLeft = UNDO_KEYC,
                    bottomRight = REDO_KEYC,
                    backgroundColor = SURFACE_VARIANT,
                ),
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
                    top = KeyC("\"", color = MUTED),
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
                    swipeType = FOUR_WAY_CROSS,
                    right = KeyC("?", color = MUTED),
                    left = KeyC("!", color = MUTED),
                    bottom = KeyC(":", color = MUTED),
                    top = KeyC(";", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("M", size = LARGE),
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
                    widthMultiplier = 2,
                    longPress = DeleteWordBeforeCursor,
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("Entf"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_FORWARD_DEL,
                                    ),
                                ),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordAfterCursor,
                ),
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_DE_TYPESPLITBEN_NUMERIC =
    KeyboardC(
        listOf(
            listOf(
                textEditKeyItem(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                            action = ToggleClipboardMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                ),
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    bottomRight = KeyC("!"),
                    top = KeyC("¯\\_(ツ)_/¯", size = SMALLEST),
                    bottom = KeyC("~"),
                    left = KeyC("{"),
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    bottom = KeyC("@"),
                    topLeft = KeyC("`"),
                    topRight = KeyC("´"),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    right = KeyC("}"),
                    topRight = KeyC("°"),
                    bottomLeft = KeyC("#"),
                ),
                KeyItemC(
                    center = KeyC(".", size = LARGE),
                ),
            ),
            listOf(
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Mic),
                            action = SwitchIMEVoice,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    top = KeyC("+"),
                    bottom = KeyC("="),
                    left = KeyC("-"),
                    right = KeyC("_"),
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    top = KeyC("\""),
                    bottom = KeyC(":"),
                    left = KeyC("("),
                    right = KeyC("$"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    left = KeyC("€"),
                    right = KeyC("£"),
                    bottom = KeyC("%"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    top = KeyC("'"),
                    bottom = KeyC(";"),
                    left = KeyC("^"),
                    right = KeyC(")"),
                ),
                KeyItemC(
                    center = KeyC(",", size = LARGE),
                    swipeType = TWO_WAY_VERTICAL,
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.Mood),
                            action = ToggleEmojiMode(true),
                            color = MUTED,
                        ),
                ),
            ),
            listOf(
                SPACEBAR_SKINNY_KEY_ITEM,
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    topLeft = KeyC("["),
                    topRight = KeyC("&"),
                    bottomLeft = KeyC("<"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    top = KeyC("*"),
                    bottom = KeyC("?"),
                    left = KeyC("/"),
                    right = KeyC("\\"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    left = KeyC("|"),
                    topRight = KeyC("]"),
                    bottomRight = KeyC(">"),
                ),
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                ),
            ),
            listOf(
                ABC_KEY_ITEM_ALT,
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
                    widthMultiplier = 2,
                    longPress = DeleteWordBeforeCursor,
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("Entf"),
                            action =
                                SendEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_FORWARD_DEL,
                                    ),
                                ),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordAfterCursor,
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
                numeric = KB_DE_TYPESPLITBEN_NUMERIC,
            ),
    )
