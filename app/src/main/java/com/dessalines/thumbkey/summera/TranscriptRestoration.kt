package com.dessalines.thumbkey.summera

import android.content.Context
import com.dessalines.thumbkey.db.ClipboardItem
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.db.SOURCE_TRANSCRIPT
import de.xida.aichatapi.TranscriptionStatus
import de.xida.aichatapi.XidaAiException
import de.xida.aichatapi.createdAtMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val RESTORE_PAGE_SIZE = 100

/** Pulls transcripts dictated before (another phone, a reinstall) into [repository], once per sign-in. */
internal suspend fun restoreTranscriptHistory(
    context: Context,
    repository: ClipboardRepository,
) {
    val credentials = SummeraAccount.credentials(context) ?: return
    if (SummeraAccount.transcriptsRestored(context)) return
    try {
        val client = SummeraAccount.client(context)
        val items = mutableListOf<ClipboardItem>()
        var start = 0
        do {
            val page = withContext(Dispatchers.IO) { client.services.list(credentials, start, RESTORE_PAGE_SIZE) }
            page.items
                .filter { it.status == TranscriptionStatus.COMPLETE && !it.text.isNullOrBlank() }
                .mapTo(items) {
                    ClipboardItem(
                        text = it.text.orEmpty(),
                        timestamp = it.createdAtMillis(),
                        source = SOURCE_TRANSCRIPT,
                        remoteId = it.id,
                    )
                }
            start += page.items.size
        } while (page.items.isNotEmpty() && start < page.total)
        withContext(Dispatchers.IO) { repository.restoreTranscripts(items) }
        SummeraAccount.setTranscriptsRestored(context)
        SummeraAccount.log(context, "transcripts: restored ${items.size}")
    } catch (e: XidaAiException) {
        SummeraAccount.log(context, "transcripts: restore failed ${e.message}")
    }
}
