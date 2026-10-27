@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

// Defines the Alt and numeric layouts for the Czech TypeSplit keyboard.

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

val KB_CZ_TYPESPLIT_FULL_ALTED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_N, "n", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Z, "z"),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_C, "c"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_V, "v"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_T, "t", style = KeyModifierStyle(size = LARGE)),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_W, "w"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_X, "x"),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Q, "q"),
                    topRight = KeyC(action = ToggleAltMode(false), color = MUTED),
                ),
                NUMERIC_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_E, "e", style = KeyModifierStyle(size = LARGE)),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_A, "a", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_L, "l", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_H, "h"),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_F, "f"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_R, "r", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_P, "p"),
                    bottomRight = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_B, "b"),
                    bottom = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_G, "g"),
                ),
                SPACEBAR_ALL_DIRECTIONS,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_O, "o", style = KeyModifierStyle(size = LARGE)),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_I, "i", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_D, "d", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_K, "k"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_M, "m", style = KeyModifierStyle(size = LARGE)),
                    bottomLeft = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_J, "j"),
                ),
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_S, "s", style = KeyModifierStyle(size = LARGE)),
                    right = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_Y, "y"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = keyCModifier(KeyEvent.META_ALT_ON, KeyEvent.KEYCODE_U, "u", style = KeyModifierStyle(size = LARGE)),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )

val KB_CZ_TYPESPLIT_FULL_NUMERIC =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    swipeType = EIGHT_WAY,
                    top = KeyC("/"),
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    swipeType = EIGHT_WAY,
                    top = KeyC("*"),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    swipeType = EIGHT_WAY,
                    top = KeyC("-"),
                ),
                ABC_KEY_ITEM,
                KeyItemC(
                    center = KeyC("\u0301", displayText = "◌́", size = LARGE),
                    swipeType = EIGHT_WAY,
                    topLeft = KeyC("/"),
                    top = KeyC("*"),
                    topRight = KeyC("-"),
                    right = KeyC("+"),
                    bottomRight = KeyC("="),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    swipeType = EIGHT_WAY,
                    topLeft = KeyC("{"),
                    left = KeyC("("),
                    bottomLeft = KeyC("["),
                    bottomRight = KeyC("@"),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                    swipeType = EIGHT_WAY,
                    bottomLeft = KeyC("$"),
                    bottom = KeyC("€"),
                    bottomRight = KeyC("#"),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    swipeType = EIGHT_WAY,
                    top = KeyC("+"),
                    bottomRight = KeyC("'"),
                    bottomLeft = KeyC("|"),
                    bottom = KeyC("~"),
                ),
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                ),
                KeyItemC(
                    center = KeyC("\u0308", displayText = "◌̈", size = LARGE),
                    swipeType = EIGHT_WAY,
                    topRight = KeyC("}"),
                    right = KeyC(")"),
                    bottomRight = KeyC("]"),
                    bottomLeft = KeyC("\""),
                ),
            ),
            listOf(
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    swipeType = EIGHT_WAY,
                    topLeft = KeyC("<"),
                    bottomLeft = KeyC("^"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    swipeType = EIGHT_WAY,
                    topLeft = KeyC("`"),
                    bottomRight = KeyC("&"),
                    bottomLeft = KeyC("_"),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    swipeType = FOUR_WAY_CROSS,
                    bottom = KeyC("."),
                    left = KeyC(","),
                    right = KeyC("!"),
                    top = KeyC("%"),
                ),
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    center = KeyC("\u0300", displayText = "◌̀", size = LARGE),
                    swipeType = EIGHT_WAY,
                    left = KeyC("?"),
                    topRight = KeyC(">"),
                    bottom = KeyC(":"),
                    top = KeyC(";"),
                    bottomRight = KeyC("\\"),
                ),
            ),
            listOf(
                EMOJI_KEY_ITEM,
                SPACEBAR_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )
