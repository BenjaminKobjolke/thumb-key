package com.dessalines.thumbkey.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.dessalines.thumbkey.utils.toBool

class ClipboardRepository(
    private val clipboardItemDao: ClipboardItemDao,
    private val appSettingsDao: AppSettingsDao,
) {
    val allClipboardItems: LiveData<List<ClipboardItem>> = clipboardItemDao.getAllClipboardItems(SOURCE_CLIPBOARD)
    val allTranscripts: LiveData<List<ClipboardItem>> = clipboardItemDao.getAllClipboardItems(SOURCE_TRANSCRIPT)

    @WorkerThread
    suspend fun addItem(text: String) {
        val settings = appSettingsDao.getSettingsSync()
        if (settings?.clipboardHistoryEnabled?.toBool() != true) return

        clearExpired()
        upsert(text, SOURCE_CLIPBOARD)

        if (settings.clipboardSizeLimitEnabled.toBool()) {
            enforceSizeLimit(settings.clipboardMaxSize)
        }
    }

    /** Every Summera transcript is kept, whatever the clipboard settings say; there is no size limit or auto-cleanup. */
    @WorkerThread
    suspend fun addTranscript(
        text: String,
        remoteId: Int?,
    ) {
        upsert(text, SOURCE_TRANSCRIPT, remoteId)
    }

    /** Inserts transcripts fetched from the server, skipping the ones already here (by server id, then by text). */
    @WorkerThread
    suspend fun restoreTranscripts(items: List<ClipboardItem>) {
        for (item in items) {
            val known =
                (item.remoteId != null && clipboardItemDao.existsRemoteId(item.remoteId)) ||
                    clipboardItemDao.findByText(SOURCE_TRANSCRIPT, item.text) != null
            if (!known) clipboardItemDao.insert(item.copy(source = SOURCE_TRANSCRIPT))
        }
    }

    // A repeated text bumps the existing row instead of adding a second one
    private suspend fun upsert(
        text: String,
        source: String,
        remoteId: Int? = null,
    ) {
        val existingItem = clipboardItemDao.findByText(source, text)
        if (existingItem != null) {
            clipboardItemDao.update(
                existingItem.copy(timestamp = System.currentTimeMillis(), remoteId = remoteId ?: existingItem.remoteId),
            )
            return
        }
        clipboardItemDao.insert(ClipboardItem(text = text, source = source, remoteId = remoteId))
    }

    @WorkerThread
    suspend fun togglePin(item: ClipboardItem) {
        clipboardItemDao.update(item.copy(isPinned = !item.isPinned))
    }

    @WorkerThread
    suspend fun deleteItem(item: ClipboardItem) {
        clipboardItemDao.delete(item)
    }

    @WorkerThread
    suspend fun clearAll() {
        clipboardItemDao.clearAll(SOURCE_CLIPBOARD)
    }

    @WorkerThread
    suspend fun clearUnpinned() {
        clipboardItemDao.clearUnpinnedItems(SOURCE_CLIPBOARD)
    }

    @WorkerThread
    suspend fun clearUnpinnedTranscripts() {
        clipboardItemDao.clearUnpinnedItems(SOURCE_TRANSCRIPT)
    }

    @WorkerThread
    suspend fun clearExpired() {
        val settings = appSettingsDao.getSettingsSync() ?: return
        if (!settings.clipboardAutoCleanupEnabled.toBool()) return

        val cutoffTime = System.currentTimeMillis() - (settings.clipboardCleanupAfterMinutes * 60 * 1000L)
        clipboardItemDao.deleteOlderThan(SOURCE_CLIPBOARD, cutoffTime)
    }

    private suspend fun enforceSizeLimit(maxSize: Int) {
        val unpinnedCount = clipboardItemDao.getUnpinnedCount(SOURCE_CLIPBOARD)
        if (unpinnedCount > maxSize) {
            clipboardItemDao.deleteOldestUnpinned(SOURCE_CLIPBOARD, unpinnedCount - maxSize)
        }
    }

    @WorkerThread
    suspend fun enforceSizeLimit() {
        val settings = appSettingsDao.getSettingsSync() ?: return
        if (!settings.clipboardSizeLimitEnabled.toBool()) return
        enforceSizeLimit(settings.clipboardMaxSize)
    }
}
