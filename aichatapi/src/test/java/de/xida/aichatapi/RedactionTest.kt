package de.xida.aichatapi

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RedactionTest {
    private val registerCode = "29f3aac9b1c24d5e8f7a6b5c4d3e2f10"

    @Test
    fun redactParamsMasksApiToken() {
        val result = redactParams(mapOf("software" to "de.xida.thumbkey", "api_token" to "secret123"))
        assertEquals("software=de.xida.thumbkey&api_token=***", result)
        assertFalse(result.contains("secret123"))
    }

    @Test
    fun redactParamsShortensRegisterCode() {
        val result = redactParams(mapOf("register_code" to registerCode))
        assertEquals("register_code=29f3aa…", result)
        assertFalse(result.contains(registerCode))
    }

    @Test
    fun redactSecretsMasksApiTokenInABody() {
        val result = redactSecrets("""{"success":true,"api_token":"abc.def","user":{"id":7}}""")
        assertTrue(result.contains(""""api_token":"***""""))
        assertFalse(result.contains("abc.def"))
        assertTrue(result.contains(""""id":7"""))
    }

    @Test
    fun redactSecretsShortensRegisterCodeInABody() {
        val result = redactSecrets("""{"success": true, "register_code": "$registerCode", "url": "https://ai.xida.de/x"}""")
        assertTrue(result.contains(""""register_code": "29f3aa…""""))
        assertFalse(result.contains(registerCode))
    }

    @Test
    fun redactSecretsMasksPasswordInABody() {
        val result = redactSecrets("""{"email":"user@example.com","password":"hunter2"}""")
        assertTrue(result.contains(""""password":"***""""))
        assertFalse(result.contains("hunter2"))
    }
}
