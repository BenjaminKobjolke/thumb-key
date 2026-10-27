@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

// Defines the shifted and Ctrl layouts for the Czech TypeSplit keyboard.

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.KeyboardCapslock
import androidx.compose.material.icons.outlined.KeyboardControlKey
import androidx.compose.material.icons.outlined.KeyboardOptionKey
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

// NOTE: relies on keyCModifier() being visible package-wide from
// ENMessagEaseSymbolsModifiers.kt (same package, no import needed).
//
// Design summary:
//  - LEFT main keys (n t l r d m): consonant-heavy, chosen + arranged by
//    running a constrained max-cut optimization over this user's own
//    Czech tweet-archive bigram frequencies (see extract_czech_freq.py /
//    optimize_split.py). Achieves ~81% of all bigram weight alternating
//    thumbs, vs ~34% for a naive vowel/consonant split.
//  - RIGHT main keys (e a o i s u): vowel-heavy, lighter letter-swipe load,
//    so carries diacritics, Ctrl/Alt toggles, and the programming symbol set.
//  - Digits 1-9,0 are longPress (no swipe slots used) in reading order
//    across the first 10 main keys.
//  - Ctrl (t.bottomLeft) / Alt (s.bottomRight) toggle into full CTRLED/
//    ALTED grids mirroring all 26 letters, matching the confirmed-working
//    pattern from ENMessagEaseSymbolsModifiers.kt. Same physical gesture
//    toggles back off.

val KB_CZ_TYPESPLIT_FULL_SHIFTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("1"),
                    center = KeyC("N", size = LARGE),
                    topLeft = KeyC("Ň"),
                    top = KeyC("Č"),
                    right = KeyC("Z"),
                    bottomLeft = KeyC("Ž"),
                    bottomRight = KeyC("C"),
                    bottom = KeyC("V"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("2"),
                    center = KeyC("T", size = LARGE),
                    bottomLeft = KeyC("Ť"),
                    bottomRight = KeyC("W"),
                    bottom = KeyC("X"),
                    right = KeyC("Q"),
                    topLeft =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardControlKey),
                            action = ToggleCtrlMode(true),
                            color = MUTED,
                        ),
                    topRight =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardOptionKey),
                            action = ToggleAltMode(true),
                            color = MUTED,
                        ),
                ),
                NUMERIC_KEY_ITEM,
                czEKey(shifted = true),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("4"),
                    center = KeyC("A", size = LARGE),
                    topLeft = KeyC("Á"),
                    left = KeyC("-", color = MUTED),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                            capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleCapsLock,
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                            action = ToggleShiftMode(false),
                            swipeReturnAction = ToggleCurrentWordCapitalization(false),
                            color = MUTED,
                        ),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("5"),
                    center = KeyC("L", size = LARGE),
                    right = KeyC("H"),
                    bottomRight = KeyC("F"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("6"),
                    center = KeyC("R", size = LARGE),
                    topLeft = KeyC("Ř"),
                    bottomLeft = KeyC("P"),
                    bottomRight = KeyC("B"),
                    bottom = KeyC("G"),
                    top = KeyC("*", color = MUTED),
                    topRight = KeyC("=", color = MUTED),
                    left = KeyC("<", color = MUTED),
                    right = KeyC(">", color = MUTED),
                ),
                SPACEBAR_ALL_DIRECTIONS,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("O", size = LARGE),
                    topLeft = KeyC("Ó"),
                    top = KeyC("{", color = MUTED),
                    topRight = KeyC("}", color = MUTED),
                    right = KeyC("a\"", color = MUTED),
                    bottomRight = KeyC("'", color = MUTED),
                    bottom = KeyC("`", color = MUTED),
                    bottomLeft = KeyC("„", color = MUTED),
                    left = KeyC("“", color = MUTED),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("I", size = LARGE),
                    topLeft = KeyC("Í"),
                    top = KeyC("+", color = MUTED),
                    bottomRight = KeyC("/", color = MUTED),
                    bottom = KeyC("%", color = MUTED),
                    bottomLeft = KeyC("^", color = MUTED),
                    left = KeyC("~", color = MUTED),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("9"),
                    center = KeyC("D", size = LARGE),
                    topLeft = KeyC("Ď"),
                    right = KeyC("K"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("0"),
                    center = KeyC("M", size = LARGE),
                    bottomLeft = KeyC("J"),
                    topLeft = KeyC("(", color = MUTED),
                    topRight = KeyC(")", color = MUTED),
                    left = KeyC("[", color = MUTED),
                    right = KeyC("]", color = MUTED),
                ),
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("S", size = LARGE),
                    topLeft = KeyC("Š"),
                    right = KeyC("Y"),
                    bottomRight = KeyC("Ý"),
                    top = KeyC("@", color = MUTED),
                    topRight = KeyC("#", color = MUTED),
                    bottom = KeyC("$", color = MUTED),
                    bottomLeft = KeyC("&", color = MUTED),
                    left = KeyC("_", color = MUTED),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("U", size = LARGE),
                    topLeft = KeyC("Ú"),
                    top = KeyC("Ů"),
                    topRight = KeyC("|", color = MUTED),
                    right = KeyC("\\", color = MUTED),
                    bottomLeft = KeyC("–", color = MUTED),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_CZ_TYPESPLIT_FULL_CTRLED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_N, "n", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Z, "z"),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_C, "c"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_V, "v"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_T, "t", style = KeyModifierStyle(size = LARGE)),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_W, "w"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_X, "x"),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Q, "q"),
                    topLeft = KeyC(action = ToggleCtrlMode(false), color = MUTED),
                ),
                NUMERIC_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_E, "e", style = KeyModifierStyle(size = LARGE)),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_A, "a", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_L, "l", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_H, "h"),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_F, "f"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_R, "r", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_P, "p"),
                    bottomRight = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_B, "b"),
                    bottom = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_G, "g"),
                ),
                SPACEBAR_ALL_DIRECTIONS,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_O, "o", style = KeyModifierStyle(size = LARGE)),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_I, "i", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_D, "d", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_K, "k"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_M, "m", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_J, "j"),
                ),
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_S, "s", style = KeyModifierStyle(size = LARGE)),
                    right = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_Y, "y"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_U, "u", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )
