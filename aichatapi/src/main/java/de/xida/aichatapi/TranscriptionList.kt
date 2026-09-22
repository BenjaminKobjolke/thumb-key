package de.xida.aichatapi

import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** One item of services/list. [createdAt] is the server string `YYYY-MM-DD HH:MM:SS`, the UI formats it. */
data class TranscriptionSummary(
    val id: Int,
    val status: String,
    val createdAt: String,
    val language: String?,
    /** The full transcript, null while the job is still running. */
    val text: String?,
    /** All three are null when no prompt was sent with the upload. */
    val prompt: String?,
    /** `pending` | `active` | `complete` | `error`. */
    val promptStatus: String?,
    val promptResult: String?,
)

// The server formats created_at in its own zone (ai-chat-api config: Europe/Berlin), not UTC
private const val SERVER_TIME_ZONE = "Europe/Berlin"

/** [TranscriptionSummary.createdAt] as epoch millis, or [fallback] when the server string is malformed. */
fun TranscriptionSummary.createdAtMillis(fallback: Long = System.currentTimeMillis()): Long {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    format.timeZone = TimeZone.getTimeZone(SERVER_TIME_ZONE)
    format.isLenient = false
    return try {
        format.parse(createdAt)?.time ?: fallback
    } catch (_: ParseException) {
        fallback
    }
}

data class TranscriptionPage(
    val start: Int,
    val total: Int,
    val items: List<TranscriptionSummary>,
)

// optString() would turn a JSON null into the string "null"
private fun JSONObject.nullableString(key: String): String? = if (isNull(key)) null else getString(key)

internal fun parseTranscriptionPage(json: JSONObject): TranscriptionPage {
    val pagination = json.getJSONObject("pagination")
    val items = json.getJSONArray("transcriptions")
    return TranscriptionPage(
        start = pagination.getInt("start"),
        total = pagination.getInt("total"),
        items =
            List(items.length()) { i ->
                val item = items.getJSONObject(i)
                TranscriptionSummary(
                    id = item.getInt("id"),
                    status = item.getString("status"),
                    createdAt = item.getString("created_at"),
                    language = item.nullableString("language"),
                    text = item.nullableString("text"),
                    prompt = item.nullableString("prompt"),
                    promptStatus = item.nullableString("prompt_status"),
                    promptResult = item.nullableString("prompt_result"),
                )
            },
    )
}
