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

internal val TRANSCRIPT_HISTORY_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.History),
        action = ToggleTranscriptHistoryMode(true),
        color = MUTED,
    )

/** Space key whose center shows the mic (system voice input and Summera dictation). */
internal val SPACEBAR_MIC_CENTER_KEYC =
    SPACEBAR_CENTER_KEYC.copy(
        display = SWITCH_IME_VOICE_KEYC.display,
        color = SWITCH_IME_VOICE_KEYC.color,
    )

internal val SPACEBAR_MIC_KEY_ITEM =
    KeyItemC(
        // Mic marks the long-press Summera dictation; a tap still types a space
        center = SPACEBAR_MIC_CENTER_KEYC,
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.DELETE,
        left =
            KeyC(
                DeleteWordBeforeCursor,
                display = KeyDisplay.TextDisplay("←×"),
            ),
        right =
            KeyC(
                DeleteWordAfterCursor,
                display = KeyDisplay.TextDisplay("×→"),
            ),
        top = KeyC("\"", color = MUTED),
        bottom = KeyC(",", color = MUTED),
        nextTapActions =
            listOf(
                ReplaceLastText(", ", trimCount = 1),
                ReplaceLastText(". "),
                ReplaceLastText("? "),
                ReplaceLastText("! "),
                ReplaceLastText(": "),
                ReplaceLastText("; "),
            ),
        backgroundColor = SURFACE_VARIANT,
        longPress = SummeraDictate,
    )

internal fun cursorControlKeyItem(shiftMode: Boolean) =
    KeyItemC(
        center =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.KeyboardBackspace),
                action = DeleteKeyAction,
                size = LARGE,
                color = SECONDARY,
            ),
        swipeType = FOUR_WAY_CROSS,
        slideType = SlideType.MOVE_CURSOR,
        left =
            KeyC(
                display = KeyDisplay.TextDisplay("←"),
                action =
                    SendEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_LEFT,
                        ),
                    ),
                color = MUTED,
            ),
        right =
            KeyC(
                display = KeyDisplay.TextDisplay("→"),
                action =
                    SendEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_DPAD_RIGHT,
                        ),
                    ),
                color = MUTED,
            ),
        top =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardCapslock),
                action = ToggleShiftMode(shiftMode),
                color = MUTED,
            ),
        backgroundColor = SURFACE_VARIANT,
        widthMultiplier = 2,
        longPress = DeleteWordBeforeCursor,
    )
