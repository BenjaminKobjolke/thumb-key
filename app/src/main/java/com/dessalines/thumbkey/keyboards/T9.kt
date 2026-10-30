@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_T9_MAIN =
    KeyboardC(
        listOf(
            t9FirstRow(shifted = false),
            listOf(
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    bottomRight = KeyC("i"),
                    bottom = KeyC("h"),
                    bottomLeft = KeyC("g"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    bottomRight = KeyC("l"),
                    bottom = KeyC("k"),
                    bottomLeft = KeyC("j"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    bottomRight = KeyC("o"),
                    bottom = KeyC("n"),
                    bottomLeft = KeyC("m"),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("⇧"),
                            action = ToggleShiftMode(true),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    bottomRight = KeyC("s"),
                    bottom = KeyC("r"),
                    bottomLeft = KeyC("q"),
                    left = KeyC("p"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    bottomRight = KeyC("v"),
                    bottom = KeyC("u"),
                    bottomLeft = KeyC("t"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    right = KeyC("z"),
                    bottomRight = KeyC("y"),
                    bottom = KeyC("x"),
                    bottomLeft = KeyC("w"),
                ),
                T9_EDIT_KEY_ITEM,
            ),
            T9_BOTTOM_ROW,
        ),
    )

val KB_T9: KeyboardDefinition =
    KeyboardDefinition(
        title = "T9",
        modes =
            KeyboardDefinitionModes(
                main = KB_T9_MAIN,
                shifted = KB_T9_SHIFTED,
                numeric = NUMERIC_KEYBOARD,
            ),
    )
