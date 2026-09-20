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
        throw XidaAiException(json.optString("message").ifEmpty { FALLBACK_ERROR })
    }
    return json
}

internal class Http(
    private val baseUrl: String,
) {
    fun postForm(
        path: String,
        params: Map<String, String>,
    ): JSONObject {
        val body =
            params.entries
                .joinToString("&") { "${encode(it.key)}=${encode(it.value)}" }
                .toByteArray()
        return execute(path, "application/x-www-form-urlencoded") { conn ->
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
        return execute(path, "multipart/form-data; boundary=$boundary") { conn ->
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
            writeBody(conn)

            val stream = if (conn.responseCode >= 400) conn.errorStream else conn.inputStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            return requireSuccess(JSONObject(text))
        } catch (e: IOException) {
            throw XidaAiException(e.message ?: FALLBACK_ERROR, e)
        } catch (e: JSONException) {
            throw XidaAiException(FALLBACK_ERROR, e)
        } finally {
            conn.disconnect()
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
