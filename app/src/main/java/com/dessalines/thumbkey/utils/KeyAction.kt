package com.dessalines.thumbkey.utils

// Key display and actions available to keyboard layouts.

import android.view.KeyEvent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily

sealed class KeyDisplay {
    class TextDisplay(
        val text: String,
        val fontFamily: FontFamily? = null,
    ) : KeyDisplay()

    class IconDisplay(
        val icon: ImageVector,
    ) : KeyDisplay()
}

sealed class KeyAction {
    class CommitText(
        val text: String,
    ) : KeyAction()

    class SendEvent(
        val event: KeyEvent,
    ) : KeyAction()

    class ReplaceLastText(
        val text: String,
        val trimCount: Int = 2,
    ) : KeyAction()

    class ReplaceTrailingWhitespace(
        val text: String,
        val distanceBack: Int,
    ) : KeyAction()

    class ToggleShiftMode(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleCtrlMode(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleAltMode(
        val enable: Boolean,
    ) : KeyAction()

    class ShiftAndCapsLock(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleCurrentWordCapitalization(
        val toggleUp: Boolean,
    ) : KeyAction()

    class ToggleNumericMode(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleEmojiMode(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleClipboardMode(
        val enable: Boolean,
    ) : KeyAction()

    /** Opens the Summera transcript history; separate from [ToggleClipboardMode] so upstream merges stay easy. */
    class ToggleTranscriptHistoryMode(
        val enable: Boolean,
    ) : KeyAction()

    class ToggleSketchMode(
        val enable: Boolean,
    ) : KeyAction()

    // The keys of sketch mode, handled by its screen
    sealed class Sketch : KeyAction() {
        // Types a recognized character. displayText shows a combining mark on a dotted circle.
        class CommitCandidate(
            val text: String,
            val displayText: String,
        ) : Sketch()

        // Opens the look-alikes of a candidate, the candidate first
        class ShowLookAlikes(
            val codePoints: List<Int>,
        ) : Sketch()

        data object UndoStroke : Sketch()

        data object ClearPad : Sketch()

        data object ShowCandidateList : Sketch()
    }

    class ComposeLastKey(
        val text: String,
    ) : KeyAction()

    // Starts a desktop-style compose sequence. Requires the layout to set a
    // ComposeComboProcessor as its textProcessor. Key modifications that add this
    // action get one attached automatically, see attachComposeComboProcessor.
    data object StartComposeCombo : KeyAction()

    class NormalizeLastKey(
        val text: String,
        val form: java.text.Normalizer.Form? = java.text.Normalizer.Form.NFC,
    ) : KeyAction()

    class SmartQuotes(
        val start: String,
        val end: String,
    ) : KeyAction()

    sealed class MoveKeyboard : KeyAction() {
        class ToPosition(
            val position: KeyboardPosition,
        ) : KeyAction()

        data object Left : KeyAction()

        data object Right : KeyAction()

        data object CycleLeft : KeyAction()

        data object CycleRight : KeyAction()
    }

    data object DeleteKeyAction : KeyAction()

    data object DeleteCharacterAfterCursor : KeyAction()

    data object DeleteViaTextManipulation : KeyAction()

    data object DeleteWordBeforeCursor : KeyAction()

    data object DeleteWordAfterCursor : KeyAction()

    data object PreviousWordBeforeCursor : KeyAction()

    data object NextWordAfterCursor : KeyAction()

    data object SelectLineWithCursor : KeyAction()

    data object GotoSettings : KeyAction()

    data object IMECompleteAction : KeyAction()

    data object ToggleCapsLock : KeyAction()

    data object ToggleHideLetters : KeyAction()

    data object SelectAll : KeyAction()

    data object Cut : KeyAction()

    data object Copy : KeyAction()

    data object Paste : KeyAction()

    data object Undo : KeyAction()

    data object Redo : KeyAction()

    data object CursorToLineStart : KeyAction()

    data object CursorToLineEnd : KeyAction()

    data object CursorToTextStart : KeyAction()

    data object CursorToTextEnd : KeyAction()

    data object SwitchLanguage : KeyAction()

    data object SwitchIME : KeyAction()

    data object SwitchIMEVoice : KeyAction()

    data object SummeraDictate : KeyAction()

    class ExpandAbbreviation(
        val text: String,
    ) : KeyAction()

    data object HideKeyboard : KeyAction()

    data object AcceptBestSuggestion : KeyAction()

    data object Noop : KeyAction()
}
