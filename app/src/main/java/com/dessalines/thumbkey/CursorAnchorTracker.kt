package com.dessalines.thumbkey

import android.util.Log
import android.view.inputmethod.CursorAnchorInfo
import com.dessalines.thumbkey.textprocessors.CursorSelectionUpdate
import com.dessalines.thumbkey.utils.EMPTY_SUGGESTION_CONTEXT
import com.dessalines.thumbkey.utils.SuggestionContext
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.suggestionContext

/** Tracks cursor selection changes reported by the input method framework. */
internal class CursorAnchorTracker {
    private var ignoreCursorMove = false
    var cursorMoved: Boolean = false
        private set
    private var selectionStart: Int = 0
    private var selectionEnd: Int = 0

    fun ignoreNextCursorMove() {
        ignoreCursorMove = true
    }

    fun update(
        service: IMEService,
        cursorAnchorInfo: CursorAnchorInfo,
    ): SuggestionContext {
        cursorMoved =
            if (ignoreCursorMove) {
                ignoreCursorMove = false
                false
            } else {
                Log.d(TAG, "cursor moved")
                cursorAnchorInfo.selectionStart != selectionStart ||
                    cursorAnchorInfo.selectionEnd != selectionEnd
            }
        service.currentKeyboardDefinition?.settings?.textProcessor?.handleCursorUpdate(
            service,
            CursorSelectionUpdate(
                oldSelectionStart = selectionStart,
                oldSelectionEnd = selectionEnd,
                newSelectionStart = cursorAnchorInfo.selectionStart,
                newSelectionEnd = cursorAnchorInfo.selectionEnd,
            ),
        )

        selectionStart = cursorAnchorInfo.selectionStart
        selectionEnd = cursorAnchorInfo.selectionEnd
        return if (selectionStart != selectionEnd) {
            EMPTY_SUGGESTION_CONTEXT
        } else {
            suggestionContext(
                service.appInputConnection
                    .getTextBeforeCursor(128, 0)
                    ?.toString()
                    .orEmpty(),
            )
        }
    }
}
