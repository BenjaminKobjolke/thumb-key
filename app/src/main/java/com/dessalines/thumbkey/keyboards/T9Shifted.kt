@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_T9_SHIFTED =
    KeyboardC(
        listOf(
            t9FirstRow(shifted = true),
            listOf(
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    bottomRight = KeyC("I"),
                    bottom = KeyC("H"),
                    bottomLeft = KeyC("G"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    bottomRight = KeyC("L"),
                    bottom = KeyC("K"),
                    bottomLeft = KeyC("J"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    bottomRight = KeyC("O"),
                    bottom = KeyC("N"),
                    bottomLeft = KeyC("M"),
                ),
                KeyItemC(
                    center =
                        KeyC(
                            display = KeyDisplay.TextDisplay("⇩"),
                            action = ToggleShiftMode(false),
                            size = LARGE,
                            color = SECONDARY,
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.TextDisplay("⇪"),
                            capsModeDisplay = KeyDisplay.TextDisplay("⇧"),
                            action = ToggleCapsLock,
                            color = MUTED,
                        ),
                    backgroundColor = SURFACE_VARIANT,
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    bottomRight = KeyC("S"),
                    bottom = KeyC("R"),
                    bottomLeft = KeyC("Q"),
                    left = KeyC("P"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    bottomRight = KeyC("V"),
                    bottom = KeyC("U"),
                    bottomLeft = KeyC("T"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    right = KeyC("Z"),
                    bottomRight = KeyC("Y"),
                    bottom = KeyC("X"),
                    bottomLeft = KeyC("W"),
                ),
                T9_EDIT_KEY_ITEM,
            ),
            T9_BOTTOM_ROW,
        ),
    )
