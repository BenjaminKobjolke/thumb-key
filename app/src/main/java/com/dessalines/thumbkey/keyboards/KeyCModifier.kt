package com.dessalines.thumbkey.keyboards

// Creates styled key definitions for modifier layouts.

import android.view.KeyEvent
import com.dessalines.thumbkey.utils.ColorVariant
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyDisplay

/** Shared size and color choices for modifier keys. */
data class KeyModifierStyle(
    val size: FontSizeVariant = FontSizeVariant.SMALL,
    val color: ColorVariant? = null,
)

fun keyCModifier(
    flagCode: Int,
    keyCode: Int,
    displayText: String,
    style: KeyModifierStyle = KeyModifierStyle(),
): KeyC =
    createKeyCModifier(
        flagCode,
        keyCode,
        KeyDisplay.TextDisplay(displayText),
        style,
    )

fun keyCModifierWithDisplay(
    flagCode: Int,
    keyCode: Int,
    display: KeyDisplay,
    style: KeyModifierStyle = KeyModifierStyle(),
): KeyC = createKeyCModifier(flagCode, keyCode, display, style)

private fun createKeyCModifier(
    flagCode: Int,
    keyCode: Int,
    display: KeyDisplay,
    style: KeyModifierStyle,
): KeyC =
    KeyC(
        KeyAction.SendEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, flagCode)),
        null,
        display,
        null,
        style.size,
        style.color ?: defaultModifierColor(style.size),
    )

private fun defaultModifierColor(size: FontSizeVariant): ColorVariant =
    when (size) {
        FontSizeVariant.LARGE -> ColorVariant.PRIMARY
        else -> ColorVariant.SECONDARY
    }
