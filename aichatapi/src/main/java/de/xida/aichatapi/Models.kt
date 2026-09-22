package de.xida.aichatapi

import org.json.JSONObject
import java.net.URLEncoder

class XidaAiException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

data class Credentials(
    val userId: Int,
    val apiToken: String,
    val email: String,
) {
    companion object {
        // register/check answers flat, there is no nested user object
        internal fun fromJson(json: JSONObject): Credentials =
            Credentials(
                userId = json.getInt("id"),
                apiToken = json.getString("api_token"),
                email = json.optString("email"),
            )
    }
}

data class RegisterCode(
    val code: String,
    /** The page to open in the browser so the user can confirm this code. */
    val url: String,
) {
    companion object {
        // The `url` field of the response carries no register_code, so it is built here
        internal fun fromJson(
            json: JSONObject,
            baseUrl: String,
        ): RegisterCode {
            val code = json.getString("register_code")
            val query = URLEncoder.encode(code, "UTF-8")
            return RegisterCode(code, "${baseUrl.trimEnd('/')}/register/auth?register_code=$query")
        }
    }
}

data class TranscriptionStatus(
    val status: String,
    /** The raw transcript, only set once [status] is [COMPLETE]. */
    val text: String?,
    val message: String?,
    /** services/info does not send it today, search/info does; shown instead of the app's own text when present. */
    val statusMessage: String?,
    /** Answer of the optional `prompt` sent with the upload, only set on [COMPLETE] when that AI step succeeded. */
    val promptResult: String? = null,
    /** True when a prompt was sent and its AI step failed; [text] is still the transcript. */
    val promptFailed: Boolean = false,
) {
    // The server has several in-progress states, only complete and error end the job: poll while !isFinished
    val isFinished: Boolean get() = status == COMPLETE || status == ERROR

    companion object {
        const val PENDING = "pending_attachments"
        const val ACTIVE = "active_attachments"
        const val PENDING_AI = "pending_ai"
        const val ACTIVE_AI = "active_ai"
        const val COMPLETE = "complete"
        const val ERROR = "error"

        internal fun fromJson(json: JSONObject): TranscriptionStatus {
            val status = json.optString("status")
            val results = if (status == COMPLETE) json.optJSONArray("results") else null
            val prompt = results?.optJSONObject(1)
            // success comes as 1/0, accept a boolean as well
            val promptOk = prompt != null && (prompt.optInt("success") == 1 || prompt.optBoolean("success"))
            return TranscriptionStatus(
                status,
                results?.optJSONObject(0)?.optJSONObject("result")?.optString("text"),
                json.optString("message").ifEmpty { null },
                json.optString("status_message").ifEmpty { null },
                promptResult = if (promptOk) prompt?.optJSONObject("result")?.optString("text") else null,
                promptFailed = prompt != null && !promptOk,
            )
        }
    }
}

internal fun parseTranscribeId(json: JSONObject): Int {
    val status = TranscriptionStatus.fromJson(json)
    if (status.status == TranscriptionStatus.ERROR) {
        throw XidaAiException(status.message ?: TranscriptionStatus.ERROR)
    }
    return json.getInt("id")
}
