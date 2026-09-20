package de.xida.aichatapi

/** Browser OAuth sign-in: create a code, open [RegisterCode.url], then poll [check]. */
class AuthService internal constructor(
    private val client: XidaAiClient,
) {
    /**
     * [redirectUri] is where the login page should send the browser once the login is done. The
     * server may ignore it today (SUMMERA AI API #9748), the polling with [check] works either way.
     */
    fun createRegisterCode(redirectUri: String? = null): RegisterCode {
        val params = client.softwareParams() + listOfNotNull(redirectUri?.let { "redirect_uri" to it })
        val json = client.http.postForm("register/code", params)
        return parseResponse { RegisterCode.fromJson(json, client.baseUrl) }
    }

    /** Throws [XidaAiException] until the user finished the browser leg, so callers keep polling. */
    fun check(code: String): Credentials {
        val json = client.http.postForm("register/check", client.softwareParams() + ("register_code" to code))
        return parseResponse { Credentials.fromJson(json) }
    }
}
