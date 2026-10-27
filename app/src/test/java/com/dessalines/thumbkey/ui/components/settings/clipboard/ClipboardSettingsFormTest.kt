package com.dessalines.thumbkey.ui.components.settings.clipboard

import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.ClipboardSettingsUpdate
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_HISTORY_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_MAX_SIZE
import com.dessalines.thumbkey.db.DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED
import com.dessalines.thumbkey.db.DEFAULT_USE_PRIVATE_CLIPBOARD
import org.junit.Assert.assertEquals
import org.junit.Test

class ClipboardSettingsFormTest {
    @Test(timeout = 30_000)
    fun nullAndStoredDefaultsUseClipboardDefaults() {
        val expected =
            ClipboardSettingsUpdate(
                id = 1,
                clipboardHistoryEnabled = DEFAULT_CLIPBOARD_HISTORY_ENABLED,
                clipboardAutoCleanupEnabled = DEFAULT_CLIPBOARD_AUTO_CLEANUP_ENABLED,
                clipboardCleanupAfterMinutes = DEFAULT_CLIPBOARD_CLEANUP_AFTER_MINUTES,
                clipboardSizeLimitEnabled = DEFAULT_CLIPBOARD_SIZE_LIMIT_ENABLED,
                clipboardMaxSize = DEFAULT_CLIPBOARD_MAX_SIZE,
                usePrivateClipboard = DEFAULT_USE_PRIVATE_CLIPBOARD,
            )

        assertEquals(expected, ClipboardSettingsForm(null).toUpdate())
        assertEquals(expected, ClipboardSettingsForm(AppSettings(id = 1)).toUpdate())
    }

    @Test(timeout = 30_000)
    fun settingsRoundTripEveryClipboardField() {
        val settings =
            AppSettings(
                id = 1,
                clipboardHistoryEnabled = 0,
                clipboardAutoCleanupEnabled = 1,
                clipboardCleanupAfterMinutes = 240,
                clipboardSizeLimitEnabled = 0,
                clipboardMaxSize = 37,
                usePrivateClipboard = 1,
            )

        assertEquals(
            ClipboardSettingsUpdate(
                id = 1,
                clipboardHistoryEnabled = 0,
                clipboardAutoCleanupEnabled = 1,
                clipboardCleanupAfterMinutes = 240,
                clipboardSizeLimitEnabled = 0,
                clipboardMaxSize = 37,
                usePrivateClipboard = 1,
            ),
            ClipboardSettingsForm(settings).toUpdate(),
        )
    }
}
