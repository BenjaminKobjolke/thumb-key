@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_DE_TYPESPLIT_IMPROVED_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("S", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("F"),
                    right = KeyC("G"),
                    bottom = KeyC("ẞ"),
                ),
                KeyItemC(
                    center = KeyC("N", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("J"),
                    right = KeyC("M"),
                    bottom = KeyC("K"),
                    left = KeyC("P"),
                ),
                NUMERIC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("E", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("Ö"),
                    right = KeyC("Y"),
                    bottom = KeyC("$", color = MUTED),
                    left = KeyC("O"),
                ),
                KeyItemC(
                    center = KeyC("A", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("Ä"),
                    bottom = KeyC("£", color = MUTED),
                    left = KeyC("µ", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("T", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("_", color = MUTED),
                    right = KeyC("*", color = MUTED),
                    bottom = KeyC("°", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("X"),
                    right = KeyC("W"),
                    bottom = KeyC("Q"),
                    left = KeyC("V"),
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
                    slideType = SlideType.DELETE,
                    left =
                        KeyC(
                            DeleteWordBeforeCursor,
                            display = null,
                        ),
                    right =
                        KeyC(
                            DeleteWordAfterCursor,
                            display = null,
                        ),
                    top = KeyC(":"),
                    bottom = KeyC(";"),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordBeforeCursor,
                ),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("1", color = MUTED),
                    left = KeyC("2", color = MUTED),
                    right = KeyC("3", color = MUTED),
                    bottom = KeyC("4", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("U", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("Ü"),
                    left = KeyC("5", color = MUTED),
                    bottom = KeyC("6", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("H", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\"", color = MUTED),
                    right = KeyC("~", color = MUTED),
                    bottom = KeyC("§", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("D", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("B"),
                    right = KeyC("Z"),
                    bottom = KeyC("^", color = MUTED),
                    left = KeyC("|", color = MUTED),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleCapsLock,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = FOUR_WAY_CROSS,
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    left = KeyC("[", color = MUTED),
                    right = KeyC("]", color = MUTED),
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("L", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("7", color = MUTED),
                    left = KeyC("8", color = MUTED),
                    right = KeyC("9", color = MUTED),
                    bottom = KeyC("0", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("C", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("}", color = MUTED),
                    bottom = KeyC("{", color = MUTED),
                    left = KeyC("\\", color = MUTED),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                cursorMoveSpacebarKeyItem(widthMultiplier = 3),
                DE_TYPE_SPLIT_IMPROVED_RETURN_KEY_ITEM,
            ),
        ),
    )
