@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val FRENCH_FLUID_NUMERIC_KEYBOARD =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    topLeft = KeyC("<"),
                    top = KeyC("["),
                    topRight = KeyC("#"),
                    left = KeyC("^"),
                    right = KeyC("±"),
                    bottomLeft = KeyC("≤"),
                    bottom = KeyC("¬"),
                    bottomRight = KeyC("≥"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    top = KeyC("≈"),
                    topRight = KeyC("`"),
                    left = KeyC("×"),
                    bottomLeft = KeyC("÷"),
                    bottom = KeyC("m"),
                    bottomRight = KeyC("≠"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    topLeft = KeyC("@"),
                    top = KeyC("]"),
                    topRight = KeyC(">"),
                    left = KeyC("+"),
                    right = KeyC("|"),
                    bottomLeft = KeyC("~"),
                    bottom = KeyC("&"),
                ),
                EMOJI_KEY_ITEM_CUSTOM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    topLeft = KeyC("\\"),
                    top = KeyC("²"),
                    topRight = KeyC("³"),
                    left = KeyC("("),
                    right = KeyC("₂"),
                    bottomLeft = KeyC("\u202F", displayText = "⍽", size = SMALLEST),
                    bottom = KeyC("{"),
                    bottomRight = KeyC("₃"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    topLeft = KeyC("k"),
                    topRight = KeyC("¥"),
                    left = KeyC("‰"),
                    right = KeyC("$"),
                    bottomLeft = KeyC("%"),
                    bottom = KeyC("€"),
                    bottomRight = KeyC("£"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    topRight = KeyC("/"),
                    left = KeyC("h"),
                    right = KeyC(")"),
                    bottom = KeyC("}"),
                    bottomRight = KeyC("\u00A0", displayText = "⍽"),
                ),
                ABC_KEY_ITEM_CUSTOM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    topLeft = KeyC("·"),
                    top = KeyC("„"),
                    left = KeyC("“"),
                    right = KeyC("="),
                    bottomLeft = KeyC("« ", displayText = "«"),
                    bottom = KeyC("!"),
                    bottomRight = KeyC("?"),
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    top = KeyC("°"),
                    topRight = KeyC("-"),
                    left = KeyC("*"),
                    right = KeyC(":"),
                    bottomLeft = KeyC(","),
                    bottom = KeyC("."),
                    bottomRight = KeyC(";"),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    topLeft = KeyC("’"),
                    topRight = KeyC("\""),
                    left = KeyC("'"),
                    right = KeyC("”"),
                    bottomLeft = KeyC("…"),
                    bottom = KeyC("_"),
                    bottomRight = KeyC(" »", displayText = "»"),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                ),
                SPACEBAR_FRENCH_KEY_ITEM.copy(widthMultiplier = 2),
                RETURN_KEY_ITEM,
            ),
        ),
    )
