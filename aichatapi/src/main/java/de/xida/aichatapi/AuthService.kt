package de.xida.aichatapi

/** Browser OAuth sign-in: create a code, open [RegisterCode.url], then poll [check]. */
class AuthService internal constructor(
    private val client: XidaAiClient,
) {
    fun createRegisterCode(): RegisterCode {
        val json = client.http.postForm("register/code", client.softwareParams())
        return parseResponse { RegisterCode.fromJson(json, client.baseUrl) }
    }

    /** Throws [XidaAiException] until the user finished the browser leg, so callers keep polling. */
    fun check(code: String): Credentials {
        val json = client.http.postForm("register/check", client.softwareParams() + ("register_code" to code))
        return parseResponse { Credentials.fromJson(json) }
    }
}
