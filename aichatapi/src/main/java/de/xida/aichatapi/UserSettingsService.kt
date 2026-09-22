package de.xida.aichatapi

import org.json.JSONArray
import org.json.JSONObject

private const val TRANSCRIPTION_PROMPTS_KEY = "transcriptionPrompts"
private const val TRANSCRIPTION_PROMPTS_VERSION = 1

/** One saved prompt of the `transcriptionPrompts` setting. Ids and timestamps are created by the app. */
data class TranscriptionPrompt(
    val id: String,
    val text: String,
    val createdAt: String,
    val updatedAt: String,
)

/** The user settings this client uses; values are JSON strings stored under a contract key. */
class UserSettingsService internal constructor(
    private val client: XidaAiClient,
) {
    fun getTranscriptionPrompts(credentials: Credentials): List<TranscriptionPrompt> {
        val params = client.userParams(credentials) + ("key" to TRANSCRIPTION_PROMPTS_KEY)
        return parseResponse { parseTranscriptionPromptsSetting(client.http.postForm("user/settings/get", params)) }
    }

    /** Replaces the whole list; user/settings/create upserts the key. */
    fun saveTranscriptionPrompts(
        credentials: Credentials,
        prompts: List<TranscriptionPrompt>,
    ) {
        val params =
            client.userParams(credentials) +
                mapOf("key" to TRANSCRIPTION_PROMPTS_KEY, "value" to encodeTranscriptionPrompts(prompts))
        client.http.postForm("user/settings/create", params)
    }
}

/** user/settings/get answers `{settings: [{key, value}]}`, with an empty value when the key was never set. */
internal fun parseTranscriptionPromptsSetting(json: JSONObject): List<TranscriptionPrompt> {
    val value =
        json
            .optJSONArray("settings")
            ?.optJSONObject(0)
            ?.optString("value")
            .orEmpty()
    return if (value.isEmpty()) emptyList() else parseTranscriptionPrompts(value)
}

internal fun parseTranscriptionPrompts(value: String): List<TranscriptionPrompt> {
    val json = JSONObject(value)
    // ponytail: no migration until a version 2 exists
    if (json.optInt("version") != TRANSCRIPTION_PROMPTS_VERSION) return emptyList()
    val items = json.getJSONArray("items")
    return List(items.length()) { i ->
        val item = items.getJSONObject(i)
        TranscriptionPrompt(
            id = item.getString("id"),
            text = item.getString("text"),
            createdAt = item.getString("createdAt"),
            updatedAt = item.getString("updatedAt"),
        )
    }
}

internal fun encodeTranscriptionPrompts(prompts: List<TranscriptionPrompt>): String {
    val items = JSONArray()
    for (prompt in prompts) {
        items.put(
            JSONObject()
                .put("id", prompt.id)
                .put("text", prompt.text)
                .put("createdAt", prompt.createdAt)
                .put("updatedAt", prompt.updatedAt),
        )
    }
    return JSONObject().put("version", TRANSCRIPTION_PROMPTS_VERSION).put("items", items).toString()
}
