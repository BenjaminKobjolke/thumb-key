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

val KB_DE_TYPESPLIT_IMPROVED_MAIN =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("s", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("f"),
                    right = KeyC("g"),
                    bottom = KeyC("ß"),
                ),
                KeyItemC(
                    center = KeyC("n", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("j"),
                    right = KeyC("m"),
                    bottom = KeyC("k"),
                    left = KeyC("p"),
                ),
                NUMERIC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("e", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("ö"),
                    right = KeyC("y"),
                    bottom = KeyC("€", color = MUTED),
                    left = KeyC("o"),
                ),
                KeyItemC(
                    center = KeyC("a", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("ä"),
                    bottom = KeyC("&", color = MUTED),
                    left = KeyC("@", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("t", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("-", color = MUTED),
                    right = KeyC("+", color = MUTED),
                    bottom = KeyC("%", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("r", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("x"),
                    right = KeyC("w"),
                    bottom = KeyC("q"),
                    left = KeyC("v"),
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
                    top = KeyC("."),
                    bottom = KeyC(","),
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
                    backgroundColor = SURFACE_VARIANT,
                    longPress = DeleteWordBeforeCursor,
                ),
                KeyItemC(
                    center = KeyC("i", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("1", color = MUTED),
                    left = KeyC("2", color = MUTED),
                    right = KeyC("3", color = MUTED),
                    bottom = KeyC("4", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("u", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("ü"),
                    left = KeyC("5", color = MUTED),
                    bottom = KeyC("6", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("h", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top =
                        KeyC(
                            display = KeyDisplay.TextDisplay("„“"), // mi
                            action = SmartQuotes("„", "“"),
                            color = MUTED,
                        ),
                    right = KeyC("'", color = MUTED),
                    bottom = KeyC("#", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("d", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("b"),
                    right = KeyC("z"),
                    left = KeyC("<", color = MUTED),
                    bottom = KeyC(">", color = MUTED),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleShiftMode(true),
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
                    left = KeyC("!"),
                    right = KeyC("?"),
                    backgroundColor = SURFACE_VARIANT,
                ),
                KeyItemC(
                    center = KeyC("l", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("7", color = MUTED),
                    left = KeyC("8", color = MUTED),
                    right = KeyC("9", color = MUTED),
                    bottom = KeyC("0", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("c", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("(", color = MUTED),
                    bottom = KeyC(")", color = MUTED),
                    left = KeyC("/", color = MUTED),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                cursorMoveSpacebarKeyItem(widthMultiplier = 3),
                DE_TYPE_SPLIT_IMPROVED_RETURN_KEY_ITEM,
            ),
        ),
    )
