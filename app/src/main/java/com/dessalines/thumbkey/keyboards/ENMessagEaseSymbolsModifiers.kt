@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardReturn
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.Copyright
import androidx.compose.material.icons.outlined.KeyboardCapslock
import androidx.compose.material.icons.outlined.KeyboardControlKey
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowDown
import androidx.compose.material.icons.outlined.KeyboardOptionKey
import com.dessalines.thumbkey.utils.ColorVariant
import com.dessalines.thumbkey.utils.ColorVariant.MUTED
import com.dessalines.thumbkey.utils.ColorVariant.SECONDARY
import com.dessalines.thumbkey.utils.ColorVariant.SURFACE_VARIANT
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.FontSizeVariant.LARGE
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyAction.CommitText
import com.dessalines.thumbkey.utils.KeyAction.ToggleAltMode
import com.dessalines.thumbkey.utils.KeyAction.ToggleCapsLock
import com.dessalines.thumbkey.utils.KeyAction.ToggleCtrlMode
import com.dessalines.thumbkey.utils.KeyAction.ToggleCurrentWordCapitalization
import com.dessalines.thumbkey.utils.KeyAction.ToggleShiftMode
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyDisplay
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardC
import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.KeyboardDefinitionModes
import com.dessalines.thumbkey.utils.KeyboardDefinitionSettings
import com.dessalines.thumbkey.utils.autoCapitalizeI
import com.dessalines.thumbkey.utils.autoCapitalizeIApostrophe

val KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_MAIN =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("a", size = LARGE),
                    bottomRight = KeyC("v"),
                    right = KeyC("-", color = MUTED),
                    bottomLeft = KeyC("$", color = MUTED),
                ),
                messageaseNKey(shifted = false),
                KeyItemC(
                    center = KeyC("i", size = LARGE),
                    bottomLeft = KeyC("x"),
                    left = KeyC("?", color = MUTED),
                    bottomRight = KeyC("€", color = MUTED),
                    bottom = KeyC("=", color = MUTED),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                messageaseHKey(shifted = false),
                KeyItemC(
                    center = KeyC("o", size = LARGE),
                    topLeft = KeyC("q"),
                    top = KeyC("u"),
                    topRight = KeyC("p"),
                    right = KeyC("b"),
                    bottomRight = KeyC("j"),
                    bottom = KeyC("d"),
                    bottomLeft = KeyC("g"),
                    left = KeyC("c"),
                ),
                KeyItemC(
                    center = KeyC("r", size = LARGE),
                    left = KeyC("m"),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleShiftMode(true),
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    topLeft = KeyC("|", color = MUTED),
                    topRight = KeyC("}", color = MUTED),
                    right = KeyC(")", color = MUTED),
                    bottom =
                        KeyC(
                            ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                        ),
                    bottomRight = KeyC("]", color = MUTED),
                    bottomLeft = KeyC("@", color = MUTED),
                ),
                NUMERIC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("t", size = LARGE),
                    topRight = KeyC("y"),
                    topLeft = KeyC("~", color = MUTED),
                    right = KeyC("*", color = MUTED),
                    bottomRight = keyCModifier(0, KeyEvent.KEYCODE_TAB, displayText = "⇥", style = KeyModifierStyle(color = MUTED)),
                    left = KeyC("<", color = MUTED),
                    bottomLeft =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardControlKey),
                            action = ToggleCtrlMode(true),
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center = KeyC("e", size = LARGE),
                    topLeft = KeyC("\"", color = MUTED),
                    top = KeyC("w"),
                    topRight = KeyC("'", color = MUTED),
                    right = KeyC("z"),
                    bottomRight = KeyC(":", color = MUTED),
                    bottom = KeyC(".", color = MUTED),
                    bottomLeft = KeyC(",", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("s", size = LARGE),
                    topLeft = KeyC("f"),
                    top = KeyC("&", color = MUTED),
                    topRight = KeyC("°", color = MUTED),
                    right = KeyC(">", color = MUTED),
                    bottomLeft = KeyC(";", color = MUTED),
                    left = KeyC("#", color = MUTED),
                    bottomRight =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardOptionKey),
                            action = ToggleAltMode(true),
                            color = MUTED,
                        ),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_PROGRAMMING_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("A", size = LARGE),
                    bottomRight = KeyC("V"),
                    right = KeyC("-", color = MUTED),
                    bottomLeft = KeyC("$", color = MUTED),
                ),
                messageaseNKey(shifted = true),
                KeyItemC(
                    center = KeyC("I", size = LARGE),
                    bottomLeft = KeyC("X"),
                    left = KeyC("?", color = MUTED),
                    bottomRight = KeyC("€", color = MUTED),
                    bottom = KeyC("=", color = MUTED),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                messageaseHKey(shifted = true),
                KeyItemC(
                    center = KeyC("O", size = LARGE),
                    topLeft = KeyC("Q"),
                    top = KeyC("U"),
                    topRight = KeyC("P"),
                    right = KeyC("B"),
                    bottomRight = KeyC("J"),
                    bottom = KeyC("D"),
                    bottomLeft = KeyC("G"),
                    left = KeyC("C"),
                ),
                KeyItemC(
                    center = KeyC("R", size = LARGE),
                    left = KeyC("M"),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                            action = ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                            color = MUTED,
                        ),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.Copyright),
                            action = ToggleCapsLock,
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    topLeft = KeyC("|", color = MUTED),
                    topRight = KeyC("}", color = MUTED),
                    right = KeyC(")", color = MUTED),
                    bottomRight = KeyC("]", color = MUTED),
                    bottomLeft = KeyC("@", color = MUTED),
                ),
                NUMERIC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("T", size = LARGE),
                    topRight = KeyC("Y"),
                    topLeft = KeyC("~", color = MUTED),
                    right = KeyC("*", color = MUTED),
                    left = KeyC("<", color = MUTED),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_SHIFT_ON,
                            KeyEvent.KEYCODE_TAB,
                            displayText = "⇥",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = KeyC("E", size = LARGE),
                    topLeft = KeyC("\"", color = MUTED),
                    top = KeyC("W"),
                    topRight = KeyC("'", color = MUTED),
                    right = KeyC("Z"),
                    bottomRight = KeyC(":", color = MUTED),
                    bottom = KeyC(".", color = MUTED),
                    bottomLeft = KeyC(",", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("S", size = LARGE),
                    topLeft = KeyC("F"),
                    top = KeyC("&", color = MUTED),
                    topRight = KeyC("°", color = MUTED),
                    right = KeyC(">", color = MUTED),
                    bottomLeft = KeyC(";", color = MUTED),
                    left = KeyC("#", color = MUTED),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_PROGRAMMING_KEY_ITEM,
                KeyItemC(
                    center =
                        keyCModifierWithDisplay(
                            KeyEvent.META_SHIFT_ON,
                            KeyEvent.KEYCODE_ENTER,
                            KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardReturn),
                            style = KeyModifierStyle(size = LARGE, color = SECONDARY),
                        ),
                    backgroundColor = SURFACE_VARIANT,
                    longPress = CommitText("\n"),
                ),
            ),
        ),
    )

private fun messageaseHKey(shifted: Boolean): KeyItemC {
    fun letter(value: String) = if (shifted) value.uppercase() else value
    return KeyItemC(
        center = KeyC(letter("h"), size = LARGE),
        right = KeyC(letter("k")),
        topLeft = KeyC("{", color = MUTED),
        topRight = KeyC("%", color = MUTED),
        bottomRight = KeyC("_", color = MUTED),
        bottomLeft = KeyC("[", color = MUTED),
        left = KeyC("(", color = MUTED),
    )
}

private fun messageaseNKey(shifted: Boolean): KeyItemC {
    fun letter(value: String) = if (shifted) value.uppercase() else value
    return KeyItemC(
        center = KeyC(letter("n"), size = LARGE),
        bottom = KeyC(letter("l")),
        topLeft = KeyC("`", color = MUTED),
        top = KeyC("^", color = MUTED),
        topRight = KeyC("´", color = MUTED),
        right = KeyC("!", color = MUTED),
        bottomRight = KeyC("\\", color = MUTED),
        bottomLeft = KeyC("/", color = MUTED),
        left = KeyC("+", color = MUTED),
    )
}

val KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS: KeyboardDefinition =
    KeyboardDefinition(
        title = "english messagease symbols modifiers",
        modes =
            KeyboardDefinitionModes(
                main = KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_MAIN,
                shifted = KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_SHIFTED,
                numeric = KB_EN_MESSAGEASE_NUMERIC,
                ctrled = KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_CTRLED,
                alted = KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_ALTED,
            ),
        settings =
            KeyboardDefinitionSettings(
                autoCapitalizers = arrayOf(::autoCapitalizeI, ::autoCapitalizeIApostrophe),
            ),
    )
