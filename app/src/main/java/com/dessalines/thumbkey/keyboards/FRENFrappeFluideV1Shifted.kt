@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_FR_EN_FRAPPE_FLUIDE_V1_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("U", size = LARGE),
                    topLeft = KeyC("<", color = MUTED),
                    top = KeyC("[", color = MUTED),
                    topRight = KeyC("#", color = MUTED),
                    left = KeyC("^", color = MUTED),
                    right = KeyC("Û", color = MUTED),
                    bottomLeft = KeyC("LL", size = SMALLEST, color = MUTED),
                    bottom = KeyC("B"),
                    bottomRight = KeyC("Qu"),
                ),
                KeyItemC(
                    center = KeyC("N", size = LARGE),
                    topLeft = KeyC("FF", color = MUTED),
                    top = KeyC("\u0308", displayText = "¨", color = MUTED),
                    topRight = KeyC("`", color = MUTED),
                    left = KeyC("MM", size = SMALLEST, color = MUTED),
                    right = KeyC("Ù", color = MUTED),
                    bottomLeft = KeyC("D"),
                    bottom = KeyC("M"),
                    bottomRight = KeyC("F"),
                ),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
                    topLeft = KeyC("@", color = MUTED),
                    top = KeyC("]", color = MUTED),
                    topRight = KeyC(">", color = MUTED),
                    left = KeyC("+", color = MUTED),
                    right = KeyC("GH", color = MUTED),
                    bottomLeft = KeyC("C"),
                    bottom = KeyC("Î", color = MUTED),
                    bottomRight = KeyC("Ô", color = MUTED),
                ),
                EMOJI_KEY_ITEM_CUSTOM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("E", size = LARGE),
                    topLeft = KeyC("Œ", color = MUTED),
                    top = KeyC("Q", color = MUTED),
                    topRight = KeyC("Ê", color = MUTED),
                    left = KeyC("(", color = MUTED),
                    right = KeyC("É", color = MUTED),
                    bottomLeft = KeyC("\u202F", displayText = "⍽", size = SMALLEST, color = MUTED),
                    bottom = KeyC("Z"),
                    bottomRight = KeyC("È", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("A", size = LARGE),
                    topLeft = KeyC("K"),
                    top = KeyC("O"),
                    topRight = KeyC("Â", color = MUTED),
                    left = KeyC("V"),
                    right = KeyC("Y"),
                    bottomLeft = KeyC("G"),
                    bottom = KeyC("X"),
                    bottomRight = KeyC("À", color = MUTED),
                ),
                KeyItemC(
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            action = ToggleCapsLock,
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = PRIMARY,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                            action = ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                            color = PRIMARY,
                        ),
                    center = KeyC("T", size = LARGE),
                    topLeft = KeyC("Ç", color = MUTED),
                    topRight = KeyC("/", color = MUTED),
                    left = KeyC("H"),
                    right = KeyC(")", color = MUTED),
                    bottomLeft = KeyC("P"),
                    bottomRight = KeyC("\u00A0", displayText = "⍽", color = MUTED),
                ),
                NUMERIC_KEY_ITEM_CUSTOM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                    topLeft = KeyC("·", color = MUTED),
                    top = KeyC("J"),
                    topRight = KeyC("Ch", color = MUTED),
                    left = KeyC("“", color = MUTED),
                    right = KeyC("=", color = MUTED),
                    bottomLeft = KeyC("« ", displayText = "«", color = MUTED),
                    bottom = KeyC("!", color = MUTED),
                    bottomRight = KeyC("?", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("S", size = LARGE),
                    topLeft = KeyC("PP", size = SMALLEST, color = MUTED),
                    top = KeyC("OO", size = SMALLEST, color = MUTED),
                    topRight = KeyC("-", color = MUTED),
                    left = KeyC("*", color = MUTED),
                    right = KeyC(":", color = MUTED),
                    bottomLeft = KeyC(",", color = MUTED),
                    bottom = KeyC(".", color = MUTED),
                    bottomRight = KeyC(";", color = MUTED),
                ),
                frenchFluideLastLetterKey(shifted = true),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_FRENCH_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )
