package com.dessalines.thumbkey.utils

import com.dessalines.thumbkey.keyboards.BACKSPACE_KEYC
import com.dessalines.thumbkey.keyboards.BACKSPACE_TEXT_MANIPULATION_KEYC
import com.dessalines.thumbkey.keyboards.COMPOSE_COMBO_KEYC
import com.dessalines.thumbkey.keyboards.COPY_KEYC
import com.dessalines.thumbkey.keyboards.CUT_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_CHARACTER_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_WORD_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_WORD_BEFORE_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.GOTO_SETTINGS_KEYC
import com.dessalines.thumbkey.keyboards.HIDE_KEYBOARD_KEYC
import com.dessalines.thumbkey.keyboards.NEXT_WORD_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.NOOP_KEYC
import com.dessalines.thumbkey.keyboards.PASTE_KEYC
import com.dessalines.thumbkey.keyboards.PREVIOUS_WORD_BEFORE_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.REDO_KEYC
import com.dessalines.thumbkey.keyboards.RETURN_KEYC
import com.dessalines.thumbkey.keyboards.SELECT_ALL_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_LEFT_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_PROGRAMMING_BOTTOM_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_PROGRAMMING_TOP_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_RIGHT_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_IME_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_IME_VOICE_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_LANGUAGE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_ALT_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_ALT_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CAPS_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CLIPBOARD_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CTRL_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CTRL_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_EMOJI_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_NUMERIC_MODE_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_NUMERIC_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SHIFT_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SHIFT_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SKETCH_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.UNDO_KEYC
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class CommonKeyCFromKeyActionTest {
    @Test
    fun everySerializableActionResolvesToItsCommonKey() {
        val expectedKeys =
            mapOf(
                KeyActionSerializable.ToggleNumericMode to TOGGLE_NUMERIC_MODE_TRUE_KEYC,
                KeyActionSerializable.ToggleABCMode to TOGGLE_NUMERIC_MODE_FALSE_KEYC,
                KeyActionSerializable.ToggleEmojiMode to TOGGLE_EMOJI_MODE_TRUE_KEYC,
                KeyActionSerializable.ToggleClipboardMode to TOGGLE_CLIPBOARD_MODE_TRUE_KEYC,
                KeyActionSerializable.ToggleSketchMode to TOGGLE_SKETCH_MODE_TRUE_KEYC,
                KeyActionSerializable.ToggleCapsLock to TOGGLE_CAPS_KEYC,
                KeyActionSerializable.ToggleShiftModeTrue to TOGGLE_SHIFT_TRUE_KEYC,
                KeyActionSerializable.ToggleShiftModeFalse to TOGGLE_SHIFT_FALSE_KEYC,
                KeyActionSerializable.ToggleCtrlModeTrue to TOGGLE_CTRL_TRUE_KEYC,
                KeyActionSerializable.ToggleCtrlModeFalse to TOGGLE_CTRL_FALSE_KEYC,
                KeyActionSerializable.ToggleAltModeTrue to TOGGLE_ALT_TRUE_KEYC,
                KeyActionSerializable.ToggleAltModeFalse to TOGGLE_ALT_FALSE_KEYC,
                KeyActionSerializable.Left to SPACEBAR_LEFT_KEYC,
                KeyActionSerializable.Right to SPACEBAR_RIGHT_KEYC,
                KeyActionSerializable.Top to SPACEBAR_PROGRAMMING_TOP_KEYC,
                KeyActionSerializable.Bottom to SPACEBAR_PROGRAMMING_BOTTOM_KEYC,
                KeyActionSerializable.IMEComplete to RETURN_KEYC,
                KeyActionSerializable.PreviousWordBeforeCursor to PREVIOUS_WORD_BEFORE_CURSOR_KEYC,
                KeyActionSerializable.NextWordAfterCursor to NEXT_WORD_AFTER_CURSOR_KEYC,
                KeyActionSerializable.GotoSettings to GOTO_SETTINGS_KEYC,
                KeyActionSerializable.SelectAll to SELECT_ALL_KEYC,
                KeyActionSerializable.Cut to CUT_KEYC,
                KeyActionSerializable.Copy to COPY_KEYC,
                KeyActionSerializable.Paste to PASTE_KEYC,
                KeyActionSerializable.Undo to UNDO_KEYC,
                KeyActionSerializable.Redo to REDO_KEYC,
                KeyActionSerializable.Delete to BACKSPACE_KEYC,
                KeyActionSerializable.DeleteCharacterAfterCursor to DELETE_CHARACTER_AFTER_CURSOR_KEYC,
                KeyActionSerializable.DeleteViaTextManipulation to BACKSPACE_TEXT_MANIPULATION_KEYC,
                KeyActionSerializable.DeleteWordBeforeCursor to DELETE_WORD_BEFORE_CURSOR_KEYC,
                KeyActionSerializable.DeleteWordAfterCursor to DELETE_WORD_AFTER_CURSOR_KEYC,
                KeyActionSerializable.SwitchLanguage to SWITCH_LANGUAGE_KEYC,
                KeyActionSerializable.SwitchIME to SWITCH_IME_KEYC,
                KeyActionSerializable.SwitchIMEVoice to SWITCH_IME_VOICE_KEYC,
                KeyActionSerializable.HideKeyboard to HIDE_KEYBOARD_KEYC,
                KeyActionSerializable.StartComposeCombo to COMPOSE_COMBO_KEYC,
                KeyActionSerializable.Noop to NOOP_KEYC,
            )

        assertEquals(KeyActionSerializable.entries.toSet(), expectedKeys.keys)
        expectedKeys.forEach { (action, expectedKey) ->
            assertSame("$action", expectedKey, getCommonKeyCFromKeyAction(action))
        }
    }

    @Test
    fun nullActionResolvesToNull() {
        assertNull(getCommonKeyCFromKeyAction(null))
    }
}
