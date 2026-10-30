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

val T9_EDIT_KEY_ITEM =
    KeyItemC(
        center =
            KeyC(
                display = KeyDisplay.TextDisplay("⌫"),
                action =
                    SendEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent
                                .KEYCODE_DEL,
                        ),
                    ),
                size = LARGE,
                color = SECONDARY,
            ),
        topLeft =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.SelectAll),
                action = SelectAll,
                color = MUTED,
            ),
        top =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCopy),
                action = Copy,
                color = MUTED,
            ),
        topRight =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCut),
                action = Cut,
                color = MUTED,
            ),
        right =
            KeyC(
                display = KeyDisplay.TextDisplay("⇥"),
                action = DeleteWordAfterCursor,
                color = MUTED,
            ),
        bottom =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                action = Paste,
                color = MUTED,
            ),
        left =
            KeyC(
                display = KeyDisplay.TextDisplay("⇤"),
                action = DeleteWordBeforeCursor,
                color = MUTED,
            ),
        backgroundColor = SURFACE_VARIANT,
    )

val T9_BOTTOM_ROW =
    listOf(
        KeyItemC(
            center = KeyC(" ", displayText = "␠", color = SECONDARY),
            top =
                KeyC(
                    display = KeyDisplay.TextDisplay("↑"),
                    action =
                        SendEvent(
                            KeyEvent(
                                KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_DPAD_UP,
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
            bottom =
                KeyC(
                    display = KeyDisplay.TextDisplay("↓"),
                    action =
                        SendEvent(
                            KeyEvent(
                                KeyEvent.ACTION_DOWN,
                                KeyEvent.KEYCODE_DPAD_DOWN,
                            ),
                        ),
                    color = MUTED,
                ),
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
            backgroundColor = SURFACE_VARIANT,
            nextTapActions =
                listOf(
                    ReplaceLastText(", ", trimCount = 1),
                    ReplaceLastText(". "),
                    ReplaceLastText("? "),
                    ReplaceLastText("! "),
                    ReplaceLastText(": "),
                    ReplaceLastText("; "),
                ),
        ),
        KeyItemC(
            center = KeyC("0", size = LARGE),
            topLeft = KeyC("{", color = MUTED),
            topRight = KeyC("}", color = MUTED),
            right = KeyC(")", color = MUTED),
            bottomRight = KeyC("]", color = MUTED),
            bottom = KeyC("|", color = MUTED),
            bottomLeft = KeyC("[", color = MUTED),
            left = KeyC("(", color = MUTED),
        ),
        KeyItemC(
            center =
                KeyC(
                    CommitText("."),
                    size = LARGE,
                    color = SECONDARY,
                ),
            topLeft = KeyC("'", color = MUTED),
            topRight = KeyC("\"", color = MUTED),
            bottomRight = KeyC("?", color = MUTED),
            bottom = KeyC(",", color = MUTED),
            bottomLeft = KeyC("!", color = MUTED),
            backgroundColor = SURFACE_VARIANT,
        ),
        KeyItemC(
            center =
                KeyC(
                    display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardReturn),
                    action = IMECompleteAction,
                    size = LARGE,
                    color = SECONDARY,
                ),
            topRight =
                KeyC(
                    display = KeyDisplay.IconDisplay(Icons.Outlined.Settings),
                    action = GotoSettings,
                    color = MUTED,
                ),
            right =
                KeyC(
                    display = KeyDisplay.IconDisplay(Icons.Outlined.Language),
                    action = SwitchLanguage,
                    color = MUTED,
                ),
            bottomRight =
                KeyC(
                    display = KeyDisplay.TextDisplay("↔"),
                    action = MoveKeyboard.CycleRight,
                    color = MUTED,
                ),
            backgroundColor = SURFACE_VARIANT,
        ),
    )

fun t9FirstRow(shifted: Boolean): List<KeyItemC> =
    listOf(
        KeyItemC(
            center = KeyC("1", size = LARGE),
            topLeft = KeyC("$", color = MUTED),
            top = KeyC("%", color = MUTED),
            topRight = KeyC("&", color = MUTED),
            bottomLeft = KeyC("£", color = MUTED),
            left = KeyC("€", color = MUTED),
        ),
        KeyItemC(
            center = KeyC("2", size = LARGE),
            topLeft = KeyC("~", color = MUTED),
            top = KeyC("`", color = MUTED),
            topRight = KeyC("^", color = MUTED),
            bottomRight = KeyC(t9Letter("c", shifted)),
            bottom = KeyC(t9Letter("b", shifted)),
            bottomLeft = KeyC(t9Letter("a", shifted)),
        ),
        KeyItemC(
            center = KeyC("3", size = LARGE),
            topLeft = KeyC("@", color = MUTED),
            top = KeyC(":", color = MUTED),
            topRight = KeyC(";", color = MUTED),
            bottomRight = KeyC(t9Letter("f", shifted)),
            bottom = KeyC(t9Letter("e", shifted)),
            bottomLeft = KeyC(t9Letter("d", shifted)),
            left = KeyC("#", color = MUTED),
        ),
        KeyItemC(
            center =
                KeyC(
                    CommitText("-"),
                    size = LARGE,
                    color = SECONDARY,
                ),
            topLeft = KeyC("/", color = MUTED),
            top = KeyC("*", color = MUTED),
            topRight = KeyC("\\", color = MUTED),
            right = KeyC("=", color = MUTED),
            bottomRight = KeyC(">", color = MUTED),
            bottom = KeyC("_", color = MUTED),
            bottomLeft = KeyC("<", color = MUTED),
            left = KeyC("+", color = MUTED),
            backgroundColor = SURFACE_VARIANT,
        ),
    )

private fun t9Letter(
    letter: String,
    shifted: Boolean,
): String = if (shifted) letter.uppercase() else letter
