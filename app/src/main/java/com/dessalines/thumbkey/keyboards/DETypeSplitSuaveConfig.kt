@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

// Holds the shared key grid and generated mode layouts for the Suave keyboard.

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

/**
 * INTERNAL CONFIGURATION
 */

internal enum class SuaveMode { MAIN, SHIFTED, CTRLED, ALTED, EMOJI }

// Special mappings for shifted mode
internal val SHIFT_MAPPINGS =
    mapOf(
        "ö" to "Ö",
        "ä" to "Ä",
        "ü" to "Ü",
        "ß" to "SS",
        "sch" to "Sch",
        "ch" to "Ch",
    )

internal val SUAVE_FIXED_TOKENS: Map<String, () -> KeyC> =
    mapOf(
        "esc" to {
            KeyC(
                display = KeyDisplay.TextDisplay("esc"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ESCAPE)),
                color = MUTED,
            )
        },
        "return" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardReturn),
                action = IMECompleteAction,
                size = LARGE,
                color = SECONDARY,
            )
        },
        "tab" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardTab),
                action = CommitText("\t"),
                color = SECONDARY,
            )
        },
        "enter" to { KeyC(display = null, action = CommitText("\n")) },
        "backspace" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
                action = DeleteKeyAction,
                size = LARGE,
                color = SECONDARY,
            )
        },
        "left" to {
            KeyC(
                display = KeyDisplay.TextDisplay("←"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT)),
                color = MUTED,
            )
        },
        "right" to {
            KeyC(
                display = KeyDisplay.TextDisplay("→"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT)),
                color = MUTED,
            )
        },
        "up" to {
            KeyC(
                display = KeyDisplay.TextDisplay("↑"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP)),
                color = MUTED,
            )
        },
        "down" to {
            KeyC(
                display = KeyDisplay.TextDisplay("↓"),
                action = SendEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN)),
                color = MUTED,
            )
        },
        "numeric" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.Numbers),
                action = ToggleNumericMode(true),
                size = LARGE,
                color = SECONDARY,
            )
        },
        "abc" to {
            KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.Abc), action = ToggleNumericMode(false), size = LARGE, color = SECONDARY)
        },
        "copy" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCopy), action = Copy, color = MUTED) },
        "selectall" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.SelectAll),
                action = SelectAll,
                swipeReturnAction = SelectLineWithCursor,
                color = MUTED,
            )
        },
        "cut" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCut), action = Cut, color = MUTED) },
        "undo" to { KeyC(display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.Undo), action = Undo, color = MUTED) },
        "redo" to { KeyC(display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.Redo), action = Redo, color = MUTED) },
        "paste" to {
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
                action = Paste,
                swipeReturnAction = ToggleClipboardMode(true),
                color = MUTED,
            )
        },
        "settings" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.Settings), action = GotoSettings, color = MUTED) },
        "hide" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.HideImage), action = ToggleHideLetters, color = MUTED) },
        "ime" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.Keyboard), action = SwitchIME, color = MUTED) },
        "voice" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.Mic), action = SwitchIMEVoice, color = MUTED) },
        "lang" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.Language), action = SwitchLanguage, color = MUTED) },
        "move" to { KeyC(display = KeyDisplay.IconDisplay(Icons.Outlined.LinearScale), action = MoveKeyboard.CycleRight, color = MUTED) },
    )

/**
 * Internal helper to define a key's content once and generate layers from it.
 */
internal data class SuaveKeyDef(
    val center: String,
    val top: String? = null,
    val topLeft: String? = null,
    val topRight: String? = null,
    val left: String? = null,
    val right: String? = null,
    val bottom: String? = null,
    val bottomLeft: String? = null,
    val bottomRight: String? = null,
    val swipeType: SwipeNWay = EIGHT_WAY,
    val size: FontSizeVariant = LARGE,
    val w: Int = 1,
    val bg: ColorVariant? = null,
    val slide: SlideType = SlideType.NONE,
)

// Key Definitions
private val GRID_0_0 = SuaveKeyDef("o", top = "1", right = "2", bottom = "ö", left = "")
private val GRID_0_1 = SuaveKeyDef("r", top = "4", right = "5", bottom = "w", bottomLeft = "?", bottomRight = ",", left = "3")
internal val GRID_0_2 =
    SuaveKeyDef(
        center = "backspace",
        top = "'",
        bottom = "\"",
        bg = SURFACE_VARIANT,
        slide = SlideType.DELETE,
        swipeType = FOUR_WAY_CROSS,
    )
private val GRID_0_3 = SuaveKeyDef("t", top = "7", right = "8", bottom = "p", bottomLeft = ".", bottomRight = "!", left = "6")
private val GRID_0_4 = SuaveKeyDef("h", top = "0", bottom = "q", left = "9")

private val GRID_1_0 = SuaveKeyDef("a", right = "ä", bottom = "+")
private val GRID_1_1 = SuaveKeyDef("e", top = "v", topRight = "€", right = "c", bottom = "f", bottomRight = "ch", left = "z")
internal val GRID_1_2 =
    SuaveKeyDef(
        center = " ",
        top = "up",
        bottom = "down",
        left = "left",
        right = "right",
        bg = SURFACE_VARIANT,
        swipeType = FOUR_WAY_CROSS,
        slide = SlideType.MOVE_CURSOR,
    )
private val GRID_1_3 = SuaveKeyDef("n", top = "b", right = "k", bottom = "m", left = "g")
private val GRID_1_4 = SuaveKeyDef("s", top = "~", topLeft = "$", bottom = "|", bottomLeft = "sch", left = "ß")

private val GRID_2_0 = SuaveKeyDef("u", top = "ü", topRight = "(", right = "[", bottomRight = "{")
private val GRID_2_1 = SuaveKeyDef("i", topLeft = "<", right = "x", bottom = "#", bottomLeft = "@", bottomRight = "$")
private val GRID_2_2 =
    SuaveKeyDef(
        center = "shift",
        top = "-",
        left = "—",
        topLeft = ";",
        topRight = ":",
        right = "_",
        bottom = "^",
        bottomLeft = "%",
        bottomRight = "&",
        bg = SURFACE_VARIANT,
    )
private val GRID_2_3 = SuaveKeyDef("d", top = "j", topRight = ">", bottom = "=", bottomLeft = "*", bottomRight = "/", left = "y")
private val GRID_2_4 = SuaveKeyDef("l", topLeft = ")", bottom = "\\", bottomLeft = "}", left = "]")

internal val GRID_3_0 = SuaveKeyDef("ctrl", right = "alt", top = "esc", bg = SURFACE_VARIANT, swipeType = FOUR_WAY_CROSS)
private val GRID_3_0_EMOJI = SuaveKeyDef("backspace", bg = SURFACE_VARIANT, slide = SlideType.DELETE)
internal val GRID_3_1 =
    SuaveKeyDef(
        center = "emoji",
        top = "settings",
        topLeft = "hide",
        bottom = "ime",
        bottomLeft = "voice",
        left = "lang",
        right = "move",
        bg = SURFACE_VARIANT,
    )
private val GRID_3_2 =
    SuaveKeyDef(
        center = "numeric",
        top = "copy",
        topLeft = "selectAll",
        topRight = "cut",
        bottomLeft = "undo",
        bottomRight = "redo",
        bottom = "paste",
        bg = SURFACE_VARIANT,
    )
internal val GRID_3_2_ABC = GRID_3_2.copy(center = "abc")
internal val GRID_3_3 = SuaveKeyDef("return", top = "tab", left = "enter", bg = SURFACE_VARIANT, swipeType = FOUR_WAY_CROSS, w = 2)

private val SUAVE_GRID_TEMPLATE =
    listOf(
        listOf(GRID_0_0, GRID_0_1, GRID_0_2, GRID_0_3, GRID_0_4),
        listOf(GRID_1_0, GRID_1_1, GRID_1_2, GRID_1_3, GRID_1_4),
        listOf(GRID_2_0, GRID_2_1, GRID_2_2, GRID_2_3, GRID_2_4),
    )

val KB_DE_TYPESPLIT_SUAVE_MAIN: KeyboardC by lazy {
    generateSuaveLayout(
        SuaveMode.MAIN,
        SUAVE_GRID_TEMPLATE + listOf(listOf(GRID_3_0, GRID_3_1, GRID_3_2, GRID_3_3)),
    )
}
val KB_DE_TYPESPLIT_SUAVE_SHIFTED: KeyboardC by lazy {
    generateSuaveLayout(
        SuaveMode.SHIFTED,
        SUAVE_GRID_TEMPLATE + listOf(listOf(GRID_3_0, GRID_3_1, GRID_3_2, GRID_3_3)),
    )
}
val KB_DE_TYPESPLIT_SUAVE_CTRLED: KeyboardC by lazy {
    generateSuaveLayout(
        SuaveMode.CTRLED,
        SUAVE_GRID_TEMPLATE + listOf(listOf(GRID_3_0, GRID_3_1, GRID_3_2, GRID_3_3)),
    )
}
val KB_DE_TYPESPLIT_SUAVE_ALTED: KeyboardC by lazy {
    generateSuaveLayout(
        SuaveMode.ALTED,
        SUAVE_GRID_TEMPLATE + listOf(listOf(GRID_3_0, GRID_3_1, GRID_3_2, GRID_3_3)),
    )
}
val KB_DE_TYPESPLIT_SUAVE_EMOJI: KeyboardC by lazy {
    generateSuaveLayout(
        SuaveMode.EMOJI,
        SUAVE_GRID_TEMPLATE + listOf(listOf(GRID_3_0_EMOJI, GRID_3_1, GRID_3_2, GRID_3_3)),
    )
}
