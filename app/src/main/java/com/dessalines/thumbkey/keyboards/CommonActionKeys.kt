@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.keyboards

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.outlined.*
import com.dessalines.thumbkey.utils.*
import com.dessalines.thumbkey.utils.ColorVariant.*
import com.dessalines.thumbkey.utils.FontSizeVariant.*
import com.dessalines.thumbkey.utils.KeyAction.*
import com.dessalines.thumbkey.utils.SwipeNWay.*

val COPY_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCopy),
        action = Copy,
        color = MUTED,
    )
val SELECT_ALL_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.SelectAll),
        action = SelectAll,
        swipeReturnAction = SelectLineWithCursor,
        color = MUTED,
    )
val CUT_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.ContentCut),
        action = Cut,
        color = MUTED,
    )
val UNDO_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.Undo),
        action = Undo,
        color = MUTED,
    )
val REDO_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.Redo),
        action = Redo,
        color = MUTED,
    )
val PASTE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.ContentPaste),
        action = Paste,
        swipeReturnAction = ToggleClipboardMode(true),
        color = MUTED,
    )
val GOTO_SETTINGS_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Settings),
        action = GotoSettings,
        color = MUTED,
    )
val SWITCH_IME_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Keyboard),
        action = SwitchIME,
        color = MUTED,
    )
val SWITCH_IME_VOICE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Mic),
        action = SwitchIMEVoice,
        color = MUTED,
    )
val SWITCH_LANGUAGE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Language),
        action = SwitchLanguage,
        color = MUTED,
    )
val MOVE_KEYBOARD_CYCLE_RIGHT_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.LinearScale),
        action = MoveKeyboard.CycleRight,
        color = MUTED,
    )
val HIDE_KEYBOARD_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.KeyboardDoubleArrowDown),
        action = HideKeyboard,
        color = MUTED,
    )

fun textEditKeyItem(center: KeyC): KeyItemC =
    KeyItemC(
        backgroundColor = SURFACE_VARIANT,
        swipeType = EIGHT_WAY,
        center = center,
        top = COPY_KEYC,
        topLeft = SELECT_ALL_KEYC,
        topRight = CUT_KEYC,
        bottomLeft = UNDO_KEYC,
        bottomRight = REDO_KEYC,
        bottom = PASTE_KEYC,
    )

fun specialActionKeyItem(center: KeyC): KeyItemC =
    KeyItemC(
        backgroundColor = SURFACE_VARIANT,
        swipeType = EIGHT_WAY,
        center = center,
        top = GOTO_SETTINGS_KEYC,
        topLeft = TOGGLE_HIDE_LETTERS_KEYC,
        bottom = SWITCH_IME_KEYC,
        bottomLeft = SWITCH_IME_VOICE_KEYC,
        left = SWITCH_LANGUAGE_KEYC,
        right = MOVE_KEYBOARD_CYCLE_RIGHT_KEYC,
        bottomRight = TOGGLE_SKETCH_MODE_TRUE_KEYC,
    )

val TOGGLE_NUMERIC_MODE_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Abc),
        action = ToggleNumericMode(false),
        size = LARGE,
    )
val ABC_KEY_ITEM =
    textEditKeyItem(
        center = TOGGLE_NUMERIC_MODE_FALSE_KEYC,
    )

val TOGGLE_NUMERIC_MODE_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Numbers),
        action = ToggleNumericMode(true),
        size = LARGE,
        color = SECONDARY,
    )
val NUMERIC_KEY_ITEM =
    textEditKeyItem(
        center = TOGGLE_NUMERIC_MODE_TRUE_KEYC,
    )

val TOGGLE_EMOJI_MODE_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Mood),
        action = ToggleEmojiMode(true),
        size = LARGE,
        color = SECONDARY,
    )

val TOGGLE_CLIPBOARD_MODE_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Inventory),
        action = ToggleClipboardMode(true),
        size = LARGE,
        color = SECONDARY,
    )

val TOGGLE_SKETCH_MODE_TRUE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Draw),
        action = ToggleSketchMode(true),
        color = MUTED,
    )

val TOGGLE_SKETCH_MODE_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Abc),
        action = ToggleSketchMode(false),
        size = LARGE,
    )
val SKETCH_BACK_KEY_ITEM =
    KeyItemC(
        center = TOGGLE_SKETCH_MODE_FALSE_KEYC,
        backgroundColor = SURFACE_VARIANT,
    )

// Tap to undo a stroke, swipe up for the full list of candidates, swipe down to clear the pad
val SKETCH_UNDO_KEY_ITEM =
    KeyItemC(
        center =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.AutoMirrored.Outlined.Undo),
                action = Sketch.UndoStroke,
                size = LARGE,
            ),
        top =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.FormatListNumbered),
                action = Sketch.ShowCandidateList,
                color = MUTED,
            ),
        bottom =
            KeyC(
                display = KeyDisplay.IconDisplay(Icons.Outlined.DeleteSweep),
                action = Sketch.ClearPad,
                color = MUTED,
            ),
        swipeType = TWO_WAY_VERTICAL,
        backgroundColor = SURFACE_VARIANT,
    )

val TOGGLE_HIDE_LETTERS_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.HideImage),
        action = ToggleHideLetters,
        color = MUTED,
    )

val EMOJI_KEY_ITEM =
    specialActionKeyItem(
        center = TOGGLE_EMOJI_MODE_TRUE_KEYC,
    )

val TOGGLE_EMOJI_MODE_FALSE_KEYC =
    KeyC(
        display = KeyDisplay.IconDisplay(Icons.Outlined.Abc),
        action = ToggleEmojiMode(false),
        size = LARGE,
    )
val EMOJI_BACK_KEY_ITEM =
    KeyItemC(
        center = TOGGLE_EMOJI_MODE_FALSE_KEYC,
        backgroundColor = SURFACE_VARIANT,
    )

val ABC_KEY_ITEM_ALT = specialActionKeyItem(center = ABC_KEY_ITEM.center)
val NUMERIC_KEY_ITEM_ALT = specialActionKeyItem(center = NUMERIC_KEY_ITEM.center)
val EMOJI_KEY_ITEM_ALT = textEditKeyItem(center = EMOJI_KEY_ITEM.center)
