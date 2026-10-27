@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

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

val KB_CZ_TYPESPLIT_FULL_MAIN =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("1"),
                    center = KeyC("n", size = LARGE),
                    topLeft = KeyC("ň"),
                    top = KeyC("č"),
                    right = KeyC("z"),
                    bottomLeft = KeyC("ž"),
                    bottomRight = KeyC("c"),
                    bottom = KeyC("v"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("2"),
                    center = KeyC("t", size = LARGE),
                    bottomLeft = KeyC("ť"),
                    bottomRight = KeyC("w"),
                    bottom = KeyC("x"),
                    right = KeyC("q"),
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
                czEKey(shifted = false),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("4"),
                    center = KeyC("a", size = LARGE),
                    topLeft = KeyC("á"),
                    left = KeyC("-", color = MUTED),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleShiftMode(true),
                            swipeReturnAction = ToggleCurrentWordCapitalization(true),
                            color = MUTED,
                        ),
                    bottom = KeyC(ToggleShiftMode(false), swipeReturnAction = ToggleCurrentWordCapitalization(false)),
                ),
            ),
            listOf(
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("5"),
                    center = KeyC("l", size = LARGE),
                    right = KeyC("h"),
                    bottomRight = KeyC("f"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("6"),
                    center = KeyC("r", size = LARGE),
                    topLeft = KeyC("ř"),
                    bottomLeft = KeyC("p"),
                    bottomRight = KeyC("b"),
                    bottom = KeyC("g"),
                    top = KeyC("*", color = MUTED),
                    topRight = KeyC("=", color = MUTED),
                    left = KeyC("<", color = MUTED),
                    right = KeyC(">", color = MUTED),
                ),
                SPACEBAR_ALL_DIRECTIONS,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("o", size = LARGE),
                    topLeft = KeyC("ó"),
                    top = KeyC("{", color = MUTED),
                    topRight = KeyC("}", color = MUTED),
                    right = KeyC("\"", color = MUTED),
                    bottomRight = KeyC("'", color = MUTED),
                    bottom = KeyC("`", color = MUTED),
                    bottomLeft = KeyC("„", color = MUTED),
                    left = KeyC("“", color = MUTED),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("i", size = LARGE),
                    topLeft = KeyC("í"),
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
                    center = KeyC("d", size = LARGE),
                    topLeft = KeyC("ď"),
                    right = KeyC("k"),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    longPress = CommitText("0"),
                    center = KeyC("m", size = LARGE),
                    bottomLeft = KeyC("j"),
                    topLeft = KeyC("(", color = MUTED),
                    topRight = KeyC(")", color = MUTED),
                    left = KeyC("[", color = MUTED),
                    right = KeyC("]", color = MUTED),
                ),
                BACKSPACE_KEY_ITEM,
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("s", size = LARGE),
                    topLeft = KeyC("š"),
                    right = KeyC("y"),
                    bottomRight = KeyC("ý"),
                    top = KeyC("@", color = MUTED),
                    topRight = KeyC("#", color = MUTED),
                    bottom = KeyC("$", color = MUTED),
                    bottomLeft = KeyC("&", color = MUTED),
                    left = KeyC("_", color = MUTED),
                ),
                KeyItemC(
                    swipeType = EIGHT_WAY,
                    center = KeyC("u", size = LARGE),
                    topLeft = KeyC("ú"),
                    top = KeyC("ů"),
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

val KB_CZ_TYPESPLIT_FULL: KeyboardDefinition =
    KeyboardDefinition(
        title = "czech type-split full (writing + code)",
        modes =
            KeyboardDefinitionModes(
                main = KB_CZ_TYPESPLIT_FULL_MAIN,
                shifted = KB_CZ_TYPESPLIT_FULL_SHIFTED,
                numeric = KB_CZ_TYPESPLIT_FULL_NUMERIC,
                ctrled = KB_CZ_TYPESPLIT_FULL_CTRLED,
                alted = KB_CZ_TYPESPLIT_FULL_ALTED,
            ),
        // NOTE: reference layouts pass English autoCapitalizers here
        // (::autoCapitalizeI, ::autoCapitalizeIApostrophe) - those are
        // English-specific ("i" -> "I") and don't apply to Czech, so
        // omitted. Verify KeyboardDefinitionSettings() has a valid
        // no-arg / default form at compile time.
        settings = KeyboardDefinitionSettings(),
    )

internal fun czEKey(shifted: Boolean): KeyItemC =
    KeyItemC(
        swipeType = EIGHT_WAY,
        longPress = CommitText("3"),
        center = KeyC(if (shifted) "E" else "e", size = LARGE),
        topLeft = KeyC(if (shifted) "É" else "é"),
        top = KeyC(if (shifted) "Ě" else "ě"),
        topRight = KeyC(",", color = MUTED),
        right = KeyC(".", color = MUTED),
        bottomRight = KeyC("!", color = MUTED),
        bottom = KeyC("?", color = MUTED),
        bottomLeft = KeyC(":", color = MUTED),
        left = KeyC(";", color = MUTED),
    )
