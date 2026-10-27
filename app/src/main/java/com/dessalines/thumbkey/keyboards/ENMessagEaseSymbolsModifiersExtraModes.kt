@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

// Defines the Ctrl and Alt layers for the MessageEase symbols layout.

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

val KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_CTRLED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_A, "a", style = KeyModifierStyle(size = LARGE)),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_V, "v"),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_MINUS, "-", style = KeyModifierStyle(color = MUTED)),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_N, "n", style = KeyModifierStyle(size = LARGE)),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_L, "l"),
                    topLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_GRAVE, "`", style = KeyModifierStyle(color = MUTED)),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_BACKSLASH,
                            "\\",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_SLASH,
                            "/",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_I, "i", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_X, "x"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_EQUALS, " = ", style = KeyModifierStyle(color = MUTED)),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_H, "h", style = KeyModifierStyle(size = LARGE)),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_K, "k"),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_LEFT_BRACKET,
                            "[",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_O, "o", style = KeyModifierStyle(size = LARGE)),
                    topLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Q, "q"),
                    top = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_U, "u"),
                    topRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_P, "p"),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_B, "b"),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_J, "j"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_D, "d"),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_G, "g"),
                    left = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_C, "c"),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_R, "r", style = KeyModifierStyle(size = LARGE)),
                    left = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_M, "m"),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_RIGHT_BRACKET,
                            "]",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                NUMERIC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_T, "t", style = KeyModifierStyle(size = LARGE)),
                    topRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Y, "y"),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_TAB,
                            displayText = "⇥",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    bottomLeft =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardDoubleArrowDown),
                            action = ToggleCtrlMode(false),
                            color = MUTED,
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_E, "e", style = KeyModifierStyle(size = LARGE)),
                    top = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_W, "w"),
                    topRight =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_APOSTROPHE,
                            "'",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Z, "z"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_PERIOD, ".", style = KeyModifierStyle(color = MUTED)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_COMMA, ",", style = KeyModifierStyle(color = MUTED)),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_S, "s", style = KeyModifierStyle(size = LARGE)),
                    topLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_F, "f"),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_CTRL_ON,
                            KeyEvent.KEYCODE_SEMICOLON,
                            ";",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_PROGRAMMING_KEY_ITEM,
                KeyItemC(
                    center =
                        keyCModifierWithDisplay(
                            KeyEvent.META_CTRL_ON,
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

val KB_EN_MESSAGEASE_SYMBOLS_MODIFIERS_ALTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_A, "a", style = KeyModifierStyle(size = LARGE)),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_V, "v"),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_MINUS, "-", style = KeyModifierStyle(color = MUTED)),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_N, "n", style = KeyModifierStyle(size = LARGE)),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_L, "l"),
                    topLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_GRAVE, "`", style = KeyModifierStyle(color = MUTED)),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_BACKSLASH,
                            "\\",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_SLASH,
                            "/",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_I, "i", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_X, "x"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_EQUALS, " = ", style = KeyModifierStyle(color = MUTED)),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_H, "h", style = KeyModifierStyle(size = LARGE)),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_K, "k"),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_LEFT_BRACKET,
                            "[",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_O, "o", style = KeyModifierStyle(size = LARGE)),
                    topLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Q, "q"),
                    top = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_U, "u"),
                    topRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_P, "p"),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_B, "b"),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_J, "j"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_D, "d"),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_G, "g"),
                    left = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_C, "c"),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_R, "r", style = KeyModifierStyle(size = LARGE)),
                    left = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_M, "m"),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_RIGHT_BRACKET,
                            "]",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                NUMERIC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_T, "t", style = KeyModifierStyle(size = LARGE)),
                    topRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Y, "y"),
                    bottomRight =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_TAB,
                            displayText = "⇥",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_E, "e", style = KeyModifierStyle(size = LARGE)),
                    top = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_W, "w"),
                    topRight =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_APOSTROPHE,
                            "'",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Z, "z"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_PERIOD, ".", style = KeyModifierStyle(color = MUTED)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_COMMA, ",", style = KeyModifierStyle(color = MUTED)),
                ),
                KeyItemC(
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_S, "s", style = KeyModifierStyle(size = LARGE)),
                    topLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_F, "f"),
                    bottomLeft =
                        keyCModifier(
                            KeyEvent.META_ALT_ON,
                            KeyEvent.KEYCODE_SEMICOLON,
                            ";",
                            style = KeyModifierStyle(color = MUTED),
                        ),
                    bottomRight =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardDoubleArrowDown),
                            action = ToggleAltMode(false),
                            color = MUTED,
                        ),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                SPACEBAR_PROGRAMMING_KEY_ITEM,
                KeyItemC(
                    center =
                        keyCModifierWithDisplay(
                            KeyEvent.META_ALT_ON,
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
