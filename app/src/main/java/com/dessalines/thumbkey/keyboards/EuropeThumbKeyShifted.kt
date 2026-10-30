@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_EUROPE_THUMBKEY_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                europeThumbKeyFirstItem(shifted = true),
                KeyItemC(
                    center = KeyC("L", size = LARGE),
                    left = KeyC("¡", color = MUTED),
                    right = KeyC("!", color = MUTED),
                    top =
                        KeyC(
                            CommitText("Ł"),
                        ),
                    bottom =
                        KeyC(
                            CommitText("V"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
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
                            CommitText("M"),
                        ),
                    bottomRight =
                        KeyC(
                            display = KeyDisplay.TextDisplay("◌̌"),
                            action = NormalizeLastKey("\u030c"),
                        ),
                    top =
                        KeyC(
                            CommitText("İ"),
                        ),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                europeThumbKeyDaggerItem(shifted = true),
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                    topLeft =
                        KeyC(
                            CommitText("Y"),
                        ),
                    top =
                        KeyC(
                            CommitText("Q"),
                        ),
                    topRight =
                        KeyC(
                            CommitText("W"),
                        ),
                    right =
                        KeyC(
                            CommitText("K"),
                        ),
                    bottomRight =
                        KeyC(
                            CommitText("B"),
                        ),
                    bottom =
                        KeyC(
                            CommitText("J"),
                        ),
                    bottomLeft =
                        KeyC(
                            CommitText("Z"),
                        ),
                    left =
                        KeyC(
                            CommitText("F"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("A", size = LARGE),
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
                            CommitText("U"),
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.Copyright),
                            action = ToggleCapsLock,
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                            action = ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                            color = MUTED,
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
                    center = KeyC("N", size = LARGE),
                    topLeft =
                        KeyC(
                            CommitText("Þ"),
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
                            CommitText("C"),
                        ),
                    bottomLeft =
                        KeyC(
                            CommitText("Œ"),
                        ),
                    bottomRight =
                        KeyC(
                            CommitText("Ð"),
                        ),
                ),
                KeyItemC(
                    center = KeyC("O", size = LARGE),
                    topLeft =
                        KeyC(
                            CommitText("Đ"),
                        ),
                    top =
                        KeyC(
                            CommitText("G"),
                        ),
                    topRight =
                        KeyC(
                            CommitText("Ø"),
                        ),
                    right =
                        KeyC(
                            CommitText("X"),
                        ),
                    left =
                        KeyC(
                            CommitText("Æ"),
                        ),
                    bottomLeft = KeyC("'", color = MUTED),
                    bottom = KeyC(".", color = MUTED),
                    bottomRight = KeyC("-", color = MUTED),
                ),
                europeThumbKeySharpSItem(shifted = true),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )
