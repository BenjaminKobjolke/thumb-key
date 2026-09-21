package de.xida.aichatapi

import java.io.File

class ServicesService internal constructor(
    private val client: XidaAiClient,
) {
    /** Uploads [file] and returns the job id to poll with [info]. */
    fun transcribe(
        credentials: Credentials,
        file: File,
        language: String? = null,
        mimeType: String = "audio/mp4",
    ): Int {
        val params = client.userParams(credentials) + listOfNotNull(language?.let { "language" to it })
        return parseTranscribeId(
            client.http.postMultipart("services/transcribe", params, "attachment", file, mimeType),
        )
    }

    /** The endpoint is read-only, poll it until it is finished. */
    fun info(
        credentials: Credentials,
        id: Int,
    ): TranscriptionStatus =
        TranscriptionStatus.fromJson(
            client.http.postForm("services/info/$id", client.userParams(credentials)),
        )
}
