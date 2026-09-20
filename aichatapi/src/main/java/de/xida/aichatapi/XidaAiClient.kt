package de.xida.aichatapi

const val DEFAULT_BASE_URL = "https://ai.xida.de/api/v11"

/**
 * Entry point of the XIDA AI chat API. All calls block, run them off the main thread.
 *
 * [software] has to exist in the API's `apps` table.
 */
class XidaAiClient(
    val software: String,
    val appVersion: String,
    val baseUrl: String = DEFAULT_BASE_URL,
) {
    internal val http = Http(baseUrl)

    val auth = AuthService(this)
    val services = ServicesService(this)

    internal fun softwareParams(): Map<String, String> = mapOf("software" to software, "app_version" to appVersion)

    internal fun userParams(credentials: Credentials): Map<String, String> =
        softwareParams() + mapOf("user_id" to credentials.userId.toString(), "api_token" to credentials.apiToken)
}
