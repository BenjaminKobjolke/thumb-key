package com.dessalines.thumbkey.keyboards

import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyItemC
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClipboardButtonLongPressTest {
    @Test(timeout = 30_000)
    fun clipboardModeActionIsAttachedToLongPress() {
        val key = KeyItemC(center = KeyC(action = KeyAction.ToggleClipboardMode(true)))

        val updatedKey = key.copyClipboardModeToLongPress()

        assertEquals(KeyAction.Noop, updatedKey.center.action)
        assertTrue(updatedKey.longPress is KeyAction.ToggleClipboardMode)
        assertTrue((updatedKey.longPress as KeyAction.ToggleClipboardMode).enable)
    }
}
