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

// uses programming layout but adds numbers to the left side. the grid is 5x4.
// the layout also adds the few other keys from the numeric layout that the programming layout didn't already have.
// also adds a custom numeric layout with all the letter keys on it, just for good measure

fun programmingExpandedOKey(shifted: Boolean) =
    KeyItemC(
        center = KeyC(if (shifted) "O" else "o", size = LARGE),
        swipeType = FOUR_WAY_DIAGONAL,
        bottomLeft = KeyC(if (shifted) "U" else "u"),
        bottomRight = KeyC(")", color = MUTED),
        topLeft = KeyC("=", color = MUTED),
        topRight = KeyC("+", color = MUTED),
    )

fun programmingExpandedShiftKey(shifted: Boolean) =
    KeyItemC(
        center = KeyC(if (shifted) "A" else "a", size = LARGE),
        swipeType = FOUR_WAY_CROSS,
        left = KeyC(if (shifted) "L" else "l"),
        top =
            if (shifted) {
                KeyC(
                    display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                    capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.Copyright),
                    action = ToggleCapsLock,
                    swipeReturnAction = ToggleCurrentWordCapitalization(true),
                    color = MUTED,
                )
            } else {
                KeyC(
                    display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                    action = ToggleShiftMode(true),
                    swipeReturnAction = ToggleCurrentWordCapitalization(true),
                    color = MUTED,
                )
            },
        bottom =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                action = ToggleShiftMode(false),
                swipeReturnAction = ToggleCurrentWordCapitalization(false),
                color = MUTED,
            ),
        right =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardTab),
                action = CommitText("\t"),
                color = MUTED,
            ),
    )

val KB_EN_THUMBKEY_PROGRAMMING_EXPANDED: KeyboardDefinition =
    KeyboardDefinition(
        title = "english thumb-key programming expanded",
        modes =
            KeyboardDefinitionModes(
                main = KB_EN_THUMBKEY_PROGRAMMING_EXPANDED_MAIN,
                shifted = KB_EN_THUMBKEY_PROGRAMMING_EXPANDED_SHIFTED,
                numeric = NUMERIC_KEYBOARD_EXPANDED,
                // there is no numeric shifted layout mode, so...
                alted = NUMERIC_KEYBOARD_EXPANDED_SHIFTED,
            ),
        settings =
            KeyboardDefinitionSettings(
                autoCapitalizers = arrayOf(::autoCapitalizeI, ::autoCapitalizeIApostrophe),
            ),
    )
