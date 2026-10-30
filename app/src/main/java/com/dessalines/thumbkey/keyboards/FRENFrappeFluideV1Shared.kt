@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*

fun frenchFluideLastLetterKey(shifted: Boolean): KeyItemC =
    KeyItemC(
        center = KeyC(if (shifted) "L" else "l", size = LARGE),
        topLeft = KeyC("’", color = MUTED),
        top = KeyC(if (shifted) "W" else "w"),
        topRight = KeyC("\"", color = MUTED),
        left = KeyC("'", color = MUTED),
        right = KeyC("”", color = MUTED),
        bottomLeft = KeyC("…", color = MUTED),
        bottom = KeyC("_", color = MUTED),
        bottomRight = KeyC(" »", displayText = "»", color = MUTED),
    )
