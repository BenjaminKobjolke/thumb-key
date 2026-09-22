package de.xida.aichatapi

import java.io.File

class ServicesService internal constructor(
    private val client: XidaAiClient,
) {
    /**
     * Uploads [file] and returns the job id to poll with [info].
     *
     * [prompt] is an optional instruction the server runs over the finished transcript (e.g.
     * "correct spelling and grammar errors"); its answer is [TranscriptionStatus.promptResult].
     */
    fun transcribe(
        credentials: Credentials,
        file: File,
        language: String? = null,
        mimeType: String = "audio/mp4",
        prompt: String? = null,
    ): Int {
        val params =
            client.userParams(credentials) +
                listOfNotNull(
                    language?.let { "language" to it },
                    prompt?.takeIf { it.isNotBlank() }?.let { "prompt" to it },
                )
        return parseResponse {
            parseTranscribeId(
                client.http.postMultipart("services/transcribe", params, "attachment", file, mimeType),
            )
        }
    }

    /** The endpoint is read-only, poll it while [TranscriptionStatus.isFinished] is false. */
    fun info(
        credentials: Credentials,
        id: Int,
    ): TranscriptionStatus =
        parseResponse {
            TranscriptionStatus.fromJson(
                client.http.postForm("services/info/$id", client.userParams(credentials)),
            )
        }

    /** The caller's past transcriptions, newest first; [limit] is capped at 100 by the server. */
    fun list(
        credentials: Credentials,
        start: Int = 0,
        limit: Int = 20,
    ): TranscriptionPage {
        val params = client.userParams(credentials) + mapOf("start" to start.toString(), "limit" to limit.toString())
        return parseResponse { parseTranscriptionPage(client.http.postForm("services/list", params)) }
    }
}
