@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*

fun europeThumbKeyFirstItem(shifted: Boolean): KeyItemC =
    KeyItemC(
        center = KeyC(if (shifted) "S" else "s", size = LARGE),
        topLeft = KeyC("«", color = MUTED),
        top = KeyC("§", color = MUTED),
        topRight = KeyC("»", color = MUTED),
        left = KeyC("¿", color = MUTED),
        right = KeyC("?", color = MUTED),
        bottomLeft =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̄"),
                action = NormalizeLastKey("\u0304"),
            ),
        bottomRight =
            KeyC(
                CommitText(if (shifted) "P" else "p"),
            ),
    )

fun europeThumbKeyDaggerItem(shifted: Boolean): KeyItemC =
    KeyItemC(
        center = KeyC(if (shifted) "T" else "t", size = LARGE),
        topLeft =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̓"),
                action = NormalizeLastKey("\u0313"),
            ),
        top =
            KeyC(
                // PER MILLE SIGN
                display = KeyDisplay.TextDisplay("‰"),
                action = CommitText("\u2030"),
                color = MUTED,
            ),
        topRight =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̦"),
                action = NormalizeLastKey("\u0326"),
            ),
        left =
            KeyC(
                display = KeyDisplay.TextDisplay(if (shifted) "‡" else "†"),
                action = CommitText(if (shifted) "\u2021" else "\u2020"),
                color = MUTED,
            ),
        right =
            KeyC(
                CommitText(if (shifted) "H" else "h"),
            ),
        bottomLeft =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̏"),
                action = NormalizeLastKey("\u030f"),
            ),
        bottom =
            KeyC(
                // PLUS-MINUS SIGN
                display = KeyDisplay.TextDisplay("±"),
                action = CommitText("\u00b1"),
                color = MUTED,
            ),
        bottomRight =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̇"),
                action = NormalizeLastKey("\u0307"),
            ),
    )

fun europeThumbKeySharpSItem(shifted: Boolean): KeyItemC =
    KeyItemC(
        center = KeyC(if (shifted) "E" else "e", size = LARGE),
        topLeft =
            KeyC(
                CommitText(if (shifted) "D" else "d"),
            ),
        topRight =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̀"),
                action = NormalizeLastKey("\u0300"),
            ),
        left =
            KeyC(
                CommitText(if (shifted) "ẞ" else "ß"),
            ),
        right =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̂"),
                action = NormalizeLastKey("\u0302"),
            ),
        bottomLeft =
            KeyC(
                display = KeyDisplay.TextDisplay("◌̋"),
                action = NormalizeLastKey("\u030b"),
            ),
        bottomRight =
            KeyC(
                display = KeyDisplay.TextDisplay("◌́"),
                action = NormalizeLastKey("\u0301"),
            ),
    )
