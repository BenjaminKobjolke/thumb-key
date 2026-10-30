@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_EUROPE_THUMBKEY_MAIN =
    KeyboardC(
        listOf(
            listOf(
                europeThumbKeyFirstItem(shifted = false),
                KeyItemC(
                    center = KeyC("l", size = LARGE),
                    left = KeyC("¡", color = MUTED),
                    right = KeyC("!", color = MUTED),
                    top =
                        KeyC(
                            CommitText("ł"),
                        ),
                    bottom =
                        KeyC(
                            CommitText("v"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("i", size = LARGE),
                    topLeft =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̃"),
                            action = NormalizeLastKey("\u0303"),
                        ),
                    topRight =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̆"),
                            action = NormalizeLastKey("\u0306"),
                        ),
                    bottomLeft =
                        KeyC(
                            CommitText("m"),
                        ),
                    bottomRight =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̌"),
                            action = NormalizeLastKey("\u030c"),
                        ),
                    top =
                        KeyC(
                            CommitText("ı"),
                        ),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                europeThumbKeyDaggerItem(shifted = false),
                KeyItemC(
                    center = KeyC("r", size = LARGE),
                    topLeft =
                        KeyC(
                            CommitText("y"),
                        ),
                    top =
                        KeyC(
                            CommitText("q"),
                        ),
                    topRight =
                        KeyC(
                            CommitText("w"),
                        ),
                    right =
                        KeyC(
                            CommitText("k"),
                        ),
                    bottomRight =
                        KeyC(
                            CommitText("b"),
                        ),
                    bottom =
                        KeyC(
                            CommitText("j"),
                        ),
                    bottomLeft =
                        KeyC(
                            CommitText("z"),
                        ),
                    left =
                        KeyC(
                            CommitText("f"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("a", size = LARGE),
                    topLeft =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̊"),
                            action = NormalizeLastKey("\u030a"),
                        ),
                    topRight =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̨"),
                            action = NormalizeLastKey("\u0328"),
                        ),
                    left =
                        KeyC(
                            CommitText("u"),
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleShiftMode(true),
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                        ),
                    bottomLeft =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̒"),
                            action = NormalizeLastKey("\u0312"),
                        ),
                    right =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̈"),
                            action = NormalizeLastKey("\u0308"),
                        ),
                    bottomRight =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̧"),
                            action = NormalizeLastKey("\u0327"),
                        ),
                ),
                NUMERIC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("n", size = LARGE),
                    topLeft =
                        KeyC(
                            // U+00DE
                            action = CommitText("þ"),
                        ),
                    top =
                        KeyC(
                            // DEGREE SIGN
                            display = KeyDisplay.TextDisplay("°"),
                            action = CommitText("\u00b0"),
                            color = MUTED,
                        ),
                    topRight =
                        KeyC(
                            CommitText("c"),
                        ),
                    bottomLeft =
                        KeyC(
                            // U+0152
                            action = CommitText("œ"),
                        ),
                    bottomRight =
                        KeyC(
                            // U+00D0
                            action = CommitText("ð"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("o", size = LARGE),
                    topLeft =
                        KeyC(
                            CommitText("đ"),
                        ),
                    top =
                        KeyC(
                            CommitText("g"),
                        ),
                    topRight =
                        KeyC(
                            CommitText("ø"),
                        ),
                    right =
                        KeyC(
                            CommitText("x"),
                        ),
                    left =
                        KeyC(
                            CommitText("æ"),
                        ),
                    bottomLeft = KeyC("'", color = MUTED),
                    bottom = KeyC(".", color = MUTED),
                    bottomRight = KeyC("-", color = MUTED),
                ),
                europeThumbKeySharpSItem(shifted = false),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )
