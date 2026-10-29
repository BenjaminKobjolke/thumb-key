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

val NUMERIC_KEYBOARD_EXPANDED =
    KeyboardC(
        listOf(
            listOf(
                KeyItemC(
                    center = KeyC("a", size = LARGE, color = MUTED),
                    top = KeyC("b", color = MUTED),
                    topRight = KeyC("c", color = MUTED),
                    right = KeyC("d", color = MUTED),
                    bottomRight = KeyC("e", color = MUTED),
                    bottom = KeyC("f", color = MUTED),
                    left = KeyC("w", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("1", size = LARGE),
                    bottomLeft = KeyC("$"),
                    bottomRight =
                        getLocalCurrency()?.let {
                            if (it !in setOf("$", "£", "€")) {
                                KeyC(it)
                            } else {
                                null
                            }
                        },
                ),
                KeyItemC(
                    center = KeyC("2", size = LARGE),
                    topLeft = KeyC("`"),
                    top = KeyC("^"),
                    topRight = KeyC("´"),
                    right = KeyC("!"),
                    bottomRight = KeyC("\\"),
                    bottomLeft = KeyC("/"),
                    left = KeyC("+"),
                ),
                KeyItemC(
                    center = KeyC("3", size = LARGE),
                    left = KeyC("?"),
                    bottomRight = KeyC("€"),
                    bottomLeft = KeyC("£"),
                    bottom = KeyC("="),
                ),
                EMOJI_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("g", size = LARGE, color = MUTED),
                    top = KeyC("h", color = MUTED),
                    topRight = KeyC("i", color = MUTED),
                    right = KeyC("j", color = MUTED),
                    bottomRight = KeyC("k", color = MUTED),
                    bottom = KeyC("l", color = MUTED),
                    left = KeyC("x", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("4", size = LARGE),
                    topLeft = KeyC("{"),
                    topRight = KeyC("%"),
                    bottomRight = KeyC("_"),
                    bottomLeft = KeyC("["),
                    left = KeyC("("),
                ),
                KeyItemC(
                    center = KeyC("5", size = LARGE),
                ),
                KeyItemC(
                    center = KeyC("6", size = LARGE),
                    topLeft = KeyC("|"),
                    topRight = KeyC("}"),
                    right = KeyC(")"),
                    bottomRight = KeyC("]"),
                    bottomLeft = KeyC("@"),
                    top =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
                            action = ToggleAltMode(true),
                            color = MUTED,
                        ),
                    bottom =
                        KeyC(
                            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropDown),
                            action = ToggleNumericMode(true),
                            color = MUTED,
                        ),
                ),
                ABC_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("m", size = LARGE, color = MUTED),
                    top = KeyC("n", color = MUTED),
                    topRight = KeyC("o", color = MUTED),
                    right = KeyC("p", color = MUTED),
                    bottomRight = KeyC("q", color = MUTED),
                    bottom = KeyC("r", color = MUTED),
                    left = KeyC("y", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("7", size = LARGE),
                    topLeft = KeyC("~"),
                    bottomRight = KeyC(":"),
                    bottomLeft = KeyC("<"),
                ),
                KeyItemC(
                    center = KeyC("8", size = LARGE),
                    topLeft = KeyC("\""),
                    topRight = KeyC("'"),
                    bottomRight = KeyC("-"),
                    bottom = KeyC("."),
                    bottomLeft = KeyC("*"),
                    left = KeyC(","),
                ),
                KeyItemC(
                    center = KeyC("9", size = LARGE),
                    top = KeyC("&"),
                    topRight = KeyC("°"),
                    bottomRight = KeyC(">"),
                    bottomLeft = KeyC(";"),
                    left = KeyC("#"),
                ),
                BACKSPACE_KEY_ITEM,
            ),
            listOf(
                KeyItemC(
                    center = KeyC("s", size = LARGE, color = MUTED),
                    top = KeyC("t", color = MUTED),
                    topRight = KeyC("u", color = MUTED),
                    right = KeyC("v", color = MUTED),
                    left = KeyC("z", color = MUTED),
                ),
                KeyItemC(
                    center = KeyC("0", size = LARGE),
                    widthMultiplier = 2,
                ),
                SPACEBAR_SKINNY_KEY_ITEM,
                RETURN_KEY_ITEM,
            ),
        ),
    )

val NUMERIC_KEYBOARD_EXPANDED_SHIFTED =
    NUMERIC_KEYBOARD_EXPANDED.uppercaseLetterKeys().alterKey(1, 3) { key ->
        key.copy(top = key.bottom, bottom = key.top)
    }

private fun KeyboardC.uppercaseLetterKeys(): KeyboardC =
    copy(
        arr =
            arr.map { row ->
                row.map { key ->
                    key.copy(
                        center = key.center.uppercaseLetter(),
                        left = key.left?.uppercaseLetter(),
                        topLeft = key.topLeft?.uppercaseLetter(),
                        top = key.top?.uppercaseLetter(),
                        topRight = key.topRight?.uppercaseLetter(),
                        right = key.right?.uppercaseLetter(),
                        bottomRight = key.bottomRight?.uppercaseLetter(),
                        bottom = key.bottom?.uppercaseLetter(),
                        bottomLeft = key.bottomLeft?.uppercaseLetter(),
                    )
                }
            },
    )

private fun KeyC.uppercaseLetter(): KeyC {
    val text = (action as? CommitText)?.text ?: return this
    if (text.length != 1 || !text[0].isLetter()) return this

    val uppercaseText = text.uppercase()
    return copy(
        action = CommitText(uppercaseText),
        display = KeyDisplay.TextDisplay(uppercaseText),
    )
}
