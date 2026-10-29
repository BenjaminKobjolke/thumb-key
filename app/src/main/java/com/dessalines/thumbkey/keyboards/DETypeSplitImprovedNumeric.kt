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

val KB_DE_TYPESPLIT_IMPROVED_NUMERIC =
    KeyboardC(
        listOf(
            listOf(
                ABC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("=", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    left = KeyC("*"),
                    right = KeyC("/"),
                    top = KeyC("+"),
                    bottom = KeyC("-"),
                ),
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u00B9", displayText = "¹", color = MUTED),
                    bottom = KeyC("\u2081", displayText = "₁", color = MUTED),
                    left = KeyC("\u0300", displayText = "◌̀", color = MUTED),
                    right = KeyC("\u0301", displayText = "◌́", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u00B2", displayText = "²", color = MUTED),
                    bottom = KeyC("\u2082", displayText = "₂", color = MUTED),
                    left = KeyC("\u0302", displayText = "◌̂", color = MUTED),
                    right = KeyC("\u030C", displayText = "◌̌", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u00B3", displayText = "³", color = MUTED),
                    bottom = KeyC("\u2083", displayText = "₃", color = MUTED),
                    left = KeyC("\u0303", displayText = "◌̃", color = MUTED),
                ),
            ),
            listOf(
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    center = KeyC("%", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("~", color = MUTED),
                    bottom = KeyC("°", color = MUTED),
                    left = KeyC("(", color = MUTED),
                    right = KeyC(")", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2074", displayText = "⁴", color = MUTED),
                    bottom = KeyC("\u2084", displayText = "₄", color = MUTED),
                    left = KeyC("[", color = MUTED),
                    right = KeyC("]", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2075", displayText = "⁵", color = MUTED),
                    bottom = KeyC("\u2085", displayText = "₅", color = MUTED),
                    left = KeyC("{", color = MUTED),
                    right = KeyC("}", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2076", displayText = "⁶", color = MUTED),
                    bottom = KeyC("\u2086", displayText = "₆", color = MUTED),
                    left = KeyC("\u0304", displayText = "◌̄", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardReturn),
                            action = IMECompleteAction,
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    swipeType = FOUR_WAY_CROSS,
                    right =
                        KeyC(
                            display = null,
                            action = CommitText("\n"),
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardTab),
                            action = CommitText("\t"),
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = CommitText("\n"),
                ),
                KeyItemC(
                    center = KeyC(".", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC(":"),
                    bottom = KeyC("_", color = MUTED),
                    left = KeyC("<", color = MUTED),
                    right = KeyC(">", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2077", displayText = "⁷", color = MUTED),
                    bottom = KeyC("\u2087", displayText = "₇", color = MUTED),
                    left = KeyC("€", color = MUTED),
                    right = KeyC("$", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2078", displayText = "⁸", color = MUTED),
                    bottom = KeyC("\u2088", displayText = "₈", color = MUTED),
                    left = KeyC("§", color = MUTED),
                    right = KeyC("&", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2079", displayText = "⁹", color = MUTED),
                    bottom = KeyC("\u2089", displayText = "₉", color = MUTED),
                    left = KeyC("\u0336", displayText = "◌̶", color = MUTED),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                cursorMoveSpacebarKeyItem(widthMultiplier = 2),
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC("\u2070", displayText = "⁰", color = MUTED),
                    bottom = KeyC("\u2080", displayText = "₀", color = MUTED),
                    left = KeyC("\u0307", displayText = "◌̇", color = MUTED),
                    right = KeyC("\u0308", displayText = "◌̈", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC(",", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    top = KeyC(";"),
                    left = KeyC("\u0327", displayText = "◌̧", color = MUTED),
                ),
            ),
        ),
    )
