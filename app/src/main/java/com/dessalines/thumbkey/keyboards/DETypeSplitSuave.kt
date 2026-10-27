@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val KB_DE_TYPESPLIT_SUAVE: KeyboardDefinition =
    KeyboardDefinition(
        title = "deutsch type-split suave",
        modes =
            KeyboardDefinitionModes(
                main = KB_DE_TYPESPLIT_SUAVE_MAIN,
                shifted = KB_DE_TYPESPLIT_SUAVE_SHIFTED,
                numeric = KB_DE_TYPESPLIT_SUAVE_NUMERIC,
                ctrled = KB_DE_TYPESPLIT_SUAVE_CTRLED,
                alted = KB_DE_TYPESPLIT_SUAVE_ALTED,
                emoji = KB_DE_TYPESPLIT_SUAVE_EMOJI,
            ),
    )

/**
 * GENERIC PROCESSING LOGIC
 */

private fun suaveModeToken(
    text: String,
    mode: SuaveMode,
): KeyC? =
    when (text.lowercase()) {
        "shift" -> {
            suaveShiftToken(mode)
        }

        "ctrl" -> {
            val isActive = mode == SuaveMode.CTRLED
            KeyC(
                display = KeyDisplay.TextDisplay("ctrl"),
                action = ToggleCtrlMode(!isActive),
                color = if (isActive) PRIMARY else SECONDARY,
            )
        }

        "alt" -> {
            val isActive = mode == SuaveMode.ALTED
            KeyC(
                display = KeyDisplay.TextDisplay("alt"),
                action = ToggleAltMode(!isActive),
                color = if (isActive) PRIMARY else MUTED,
            )
        }

        "emoji" -> {
            val isActive = mode == SuaveMode.EMOJI
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.Mood),
                action = ToggleEmojiMode(!isActive),
                size = LARGE,
                color = if (isActive) PRIMARY else SECONDARY,
            )
        }

        else -> {
            null
        }
    }

private fun suaveShiftToken(mode: SuaveMode): KeyC =
    if (mode == SuaveMode.SHIFTED) {
        KeyC(
            display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardArrowUp),
            capsModeDisplay = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
            action = ToggleShiftMode(false),
            size = LARGE,
            color = SECONDARY,
        )
    } else {
        KeyC(
            display = KeyDisplay.IconDisplay(Icons.Outlined.ArrowDropUp),
            action = ToggleShiftMode(true),
            size = LARGE,
            color = SECONDARY,
        )
    }

private fun shiftedSuaveText(
    text: String,
    mode: SuaveMode,
): String =
    if (mode == SuaveMode.SHIFTED) {
        SHIFT_MAPPINGS[text] ?: if (text.length == 1) text.uppercase() else text
    } else {
        text
    }

private fun suaveShortcutKey(
    mode: SuaveMode,
    text: String,
    processedText: String,
    size: FontSizeVariant,
): KeyC? {
    if ((mode != SuaveMode.CTRLED && mode != SuaveMode.ALTED) || text.length != 1) return null
    val code = suaveKeyCode(text[0])
    if (code == KeyEvent.KEYCODE_UNKNOWN) return null
    val flag =
        if (mode == SuaveMode.CTRLED) {
            KeyEvent.META_CTRL_ON
        } else {
            KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        }
    return keyCModifier(flag, code, processedText, style = KeyModifierStyle(size = size))
}

private fun suaveTextKey(
    mode: SuaveMode,
    text: String,
    isCenter: Boolean,
    size: FontSizeVariant,
): KeyC =
    if ((mode == SuaveMode.CTRLED || mode == SuaveMode.ALTED) && !isCenter) {
        KeyC(text, color = MUTED)
    } else {
        KeyC(text, size = size, color = if (isCenter) PRIMARY else MUTED)
    }

internal fun generateSuaveLayout(
    mode: SuaveMode,
    grid: List<List<Any>>,
): KeyboardC {
    val result =
        grid.map { row ->
            row.map { item ->
                when (item) {
                    is KeyItemC -> {
                        item
                    }

                    is SuaveKeyDef -> {
                        val backgroundColor =
                            item.bg ?: if (mode == SuaveMode.CTRLED || mode == SuaveMode.ALTED) {
                                SURFACE_VARIANT
                            } else {
                                SURFACE
                            }

                        fun process(
                            text: String?,
                            isCenter: Boolean = false,
                        ): KeyC? {
                            if (text == null) return null
                            suaveModeToken(text, mode)?.let { return it }
                            SUAVE_FIXED_TOKENS[text.lowercase()]?.let { return it() }
                            val processedText = shiftedSuaveText(text, mode)
                            val size = if (isCenter) item.size else SMALL
                            return suaveShortcutKey(mode, text, processedText, size)
                                ?: suaveTextKey(mode, processedText, isCenter, size)
                        }

                        KeyItemC(
                            center = process(item.center, true)!!,
                            top = process(item.top),
                            topLeft = process(item.topLeft),
                            topRight = process(item.topRight),
                            left = process(item.left),
                            right = process(item.right),
                            bottom = process(item.bottom),
                            bottomLeft = process(item.bottomLeft),
                            bottomRight = process(item.bottomRight),
                            swipeType = item.swipeType,
                            backgroundColor = backgroundColor,
                            slideType = item.slide,
                            longPress =
                                if (item.center == "shift") {
                                    ToggleCapsLock
                                } else if (item.center == "return") {
                                    CommitText("\n")
                                } else if (item.center == "backspace") {
                                    DeleteWordBeforeCursor
                                } else {
                                    null
                                },
                            nextTapActions =
                                if (item.center == " ") {
                                    listOf(
                                        ReplaceLastText(", ", trimCount = 1),
                                        ReplaceLastText(". "),
                                        ReplaceLastText("? "),
                                        ReplaceLastText("! "),
                                        ReplaceLastText(": "),
                                        ReplaceLastText("; "),
                                    )
                                } else {
                                    null
                                },
                            widthMultiplier = item.w,
                        )
                    }

                    else -> {
                        throw IllegalArgumentException("Unknown item type")
                    }
                }
            }
        }
    return KeyboardC(result)
}
