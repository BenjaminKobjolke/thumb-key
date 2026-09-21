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
    /** Only set once [status] is [COMPLETE]. */
    val text: String?,
    val message: String?,
) {
    companion object {
        const val PENDING = "pending_attachments"
        const val COMPLETE = "complete"
        const val ERROR = "error"

        internal fun fromJson(json: JSONObject): TranscriptionStatus {
            val status = json.optString("status")
            val text =
                if (status == COMPLETE) {
                    json
                        .optJSONArray("results")
                        ?.optJSONObject(0)
                        ?.optJSONObject("result")
                        ?.optString("text")
                } else {
                    null
                }
            return TranscriptionStatus(status, text, json.optString("message").ifEmpty { null })
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
