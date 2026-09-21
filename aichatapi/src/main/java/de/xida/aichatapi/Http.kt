package de.xida.aichatapi

import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID

private const val TIMEOUT_MS = 30_000
private const val FALLBACK_ERROR = "request_failed"

internal fun requireSuccess(json: JSONObject): JSONObject {
    if (!json.optBoolean("success")) {
        // The server sends the reason top-level or nested in "data", depending on the endpoint
        val message =
            json.optString("message").ifEmpty { json.optJSONObject("data")?.optString("message").orEmpty() }
        throw XidaAiException(message.ifEmpty { FALLBACK_ERROR })
    }
    return json
}

/** The one place a malformed response turns into the module's [XidaAiException]. */
internal fun <T> parseResponse(parse: () -> T): T =
    try {
        parse()
    } catch (e: JSONException) {
        throw XidaAiException(FALLBACK_ERROR, e)
    }

private val SECRET_KEYS = setOf("api_token", "password")
private const val REGISTER_CODE_KEY = "register_code"
private const val REGISTER_CODE_VISIBLE = 6
private const val MASK = "***"
private const val LOG_BODY_MAX = 400

private val SECRET_JSON = Regex("(\"(?:${SECRET_KEYS.joinToString("|")})\"\\s*:\\s*\")[^\"]*")
private val REGISTER_CODE_JSON = Regex("(\"$REGISTER_CODE_KEY\"\\s*:\\s*\")([^\"]*)")

// Enough to correlate two calls, not enough to reuse the code
private fun shortenCode(code: String): String = code.take(REGISTER_CODE_VISIBLE) + "…"

/** Request params as one log line, without anything that could be replayed. */
internal fun redactParams(params: Map<String, String>): String =
    params.entries.joinToString("&") { (key, value) ->
        val shown =
            when (key) {
                in SECRET_KEYS -> MASK
                REGISTER_CODE_KEY -> shortenCode(value)
                else -> value
            }
        "$key=$shown"
    }

/** A JSON response body with the same values blanked as in [redactParams]. */
internal fun redactSecrets(text: String): String =
    text
        .replace(SECRET_JSON) { it.groupValues[1] + MASK }
        .replace(REGISTER_CODE_JSON) { it.groupValues[1] + shortenCode(it.groupValues[2]) }

internal class Http(
    private val baseUrl: String,
    private val logger: ((String) -> Unit)? = null,
) {
    fun postForm(
        path: String,
        params: Map<String, String>,
    ): JSONObject {
        val body =
            params.entries
                .joinToString("&") { "${encode(it.key)}=${encode(it.value)}" }
                .toByteArray()
        return execute(path, "application/x-www-form-urlencoded", params) { conn ->
            conn.setFixedLengthStreamingMode(body.size)
            conn.outputStream.use { it.write(body) }
        }
    }

    fun postMultipart(
        path: String,
        params: Map<String, String>,
        fileField: String,
        file: File,
        mimeType: String,
    ): JSONObject {
        val boundary = "----aichatapi${UUID.randomUUID()}"
        return execute(path, "multipart/form-data; boundary=$boundary", params + ("file" to file.name)) { conn ->
            // Chunked, so the file is streamed instead of buffered in memory
            conn.setChunkedStreamingMode(0)
            conn.outputStream.use { out ->
                for ((name, value) in params) {
                    out.write("--$boundary\r\nContent-Disposition: form-data; name=\"$name\"\r\n\r\n$value\r\n".toByteArray())
                }
                out.write(
                    (
                        "--$boundary\r\nContent-Disposition: form-data; name=\"$fileField\"; " +
                            "filename=\"${file.name}\"\r\nContent-Type: $mimeType\r\n\r\n"
                    ).toByteArray(),
                )
                file.inputStream().use { it.copyTo(out) }
                out.write("\r\n--$boundary--\r\n".toByteArray())
            }
        }
    }

    private fun execute(
        path: String,
        contentType: String,
        logParams: Map<String, String>,
        writeBody: (HttpURLConnection) -> Unit,
    ): JSONObject {
        val conn = URL("${baseUrl.trimEnd('/')}/$path").openConnection() as HttpURLConnection
        try {
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.connectTimeout = TIMEOUT_MS
            conn.readTimeout = TIMEOUT_MS
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Content-Type", contentType)
            log("POST $path ${redactParams(logParams)}")
            writeBody(conn)

            val stream = if (conn.responseCode >= 400) conn.errorStream else conn.inputStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            log("<- ${conn.responseCode} ${redactSecrets(text).take(LOG_BODY_MAX)}")
            return parseResponse { requireSuccess(JSONObject(text)) }
        } catch (e: IOException) {
            log("<- failed: ${e.message}")
            throw XidaAiException(e.message ?: FALLBACK_ERROR, e)
        } finally {
            conn.disconnect()
        }
    }

    // A broken log must not break a request
    private fun log(line: String) {
        try {
            logger?.invoke(line)
        } catch (_: Exception) {
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
