package com.dessalines.thumbkey.ui.components.settings.behavior

import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.BehaviorUpdate
import com.dessalines.thumbkey.db.DEFAULT_AUTO_CAPITALIZE
import com.dessalines.thumbkey.db.DEFAULT_CIRCULAR_DRAG_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLOCKWISE_DRAG_ACTION
import com.dessalines.thumbkey.db.DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION
import com.dessalines.thumbkey.db.DEFAULT_DRAG_RETURN_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_GHOST_KEYS_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_MIN_SWIPE_LENGTH
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_HOLD_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_SENSITIVITY
import com.dessalines.thumbkey.db.DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_SPACEBAR_MULTITAPS
import com.dessalines.thumbkey.db.DEFAULT_SWITCH_IME_MODE
import com.dessalines.thumbkey.db.DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE
import org.junit.Assert.assertEquals
import org.junit.Test

class BehaviorFormTest {
    @Test(timeout = 30_000)
    fun nullAndStoredDefaultsUseBehaviorDefaults() {
        val expected =
            BehaviorUpdate(
                id = 1,
                minSwipeLength = DEFAULT_MIN_SWIPE_LENGTH,
                slideSensitivity = DEFAULT_SLIDE_SENSITIVITY,
                slideEnabled = DEFAULT_SLIDE_ENABLED,
                slideCursorMovementMode = DEFAULT_SLIDE_CURSOR_MOVEMENT_MODE,
                slideSpacebarDeadzoneEnabled = DEFAULT_SLIDE_SPACEBAR_DEADZONE_ENABLED,
                slideBackspaceDeadzoneEnabled = DEFAULT_SLIDE_BACKSPACE_DEADZONE_ENABLED,
                autoCapitalize = DEFAULT_AUTO_CAPITALIZE,
                spacebarMultiTaps = DEFAULT_SPACEBAR_MULTITAPS,
                switchToLettersAfterSpace = DEFAULT_SWITCH_TO_LETTERS_AFTER_SPACE,
                dragReturnEnabled = DEFAULT_DRAG_RETURN_ENABLED,
                circularDragEnabled = DEFAULT_CIRCULAR_DRAG_ENABLED,
                clockwiseDragAction = DEFAULT_CLOCKWISE_DRAG_ACTION,
                counterclockwiseDragAction = DEFAULT_COUNTERCLOCKWISE_DRAG_ACTION,
                ghostKeysEnabled = DEFAULT_GHOST_KEYS_ENABLED,
                slideHoldEnabled = DEFAULT_SLIDE_HOLD_ENABLED,
                switchImeMode = DEFAULT_SWITCH_IME_MODE,
            )

        assertEquals(expected, BehaviorForm(null).toUpdate())
        assertEquals(expected, BehaviorForm(AppSettings(id = 1)).toUpdate())
    }

    @Test(timeout = 30_000)
    fun settingsRoundTripAllBehaviorFields() {
        val settings =
            AppSettings(
                id = 1,
                minSwipeLength = 77,
                slideSensitivity = 23,
                slideEnabled = 1,
                slideCursorMovementMode = 1,
                slideSpacebarDeadzoneEnabled = 0,
                slideBackspaceDeadzoneEnabled = 0,
                autoCapitalize = 0,
                spacebarMultiTaps = 0,
                switchToLettersAfterSpace = 1,
                dragReturnEnabled = 0,
                circularDragEnabled = 0,
                clockwiseDragAction = 1,
                counterclockwiseDragAction = 0,
                ghostKeysEnabled = 1,
                slideHoldEnabled = 1,
                switchImeMode = 1,
            )

        assertEquals(
            BehaviorUpdate(
                id = 1,
                minSwipeLength = 77,
                slideSensitivity = 23,
                slideEnabled = 1,
                slideCursorMovementMode = 1,
                slideSpacebarDeadzoneEnabled = 0,
                slideBackspaceDeadzoneEnabled = 0,
                autoCapitalize = 0,
                spacebarMultiTaps = 0,
                switchToLettersAfterSpace = 1,
                dragReturnEnabled = 0,
                circularDragEnabled = 0,
                clockwiseDragAction = 1,
                counterclockwiseDragAction = 0,
                ghostKeysEnabled = 1,
                slideHoldEnabled = 1,
                switchImeMode = 1,
            ),
            BehaviorForm(settings).toUpdate(),
        )
    }
}
