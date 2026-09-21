package de.xida.aichatapi

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseParsingTest {
    private val baseUrl = "https://ai.xida.de/api/v11"

    @Test
    fun registerCode() {
        val json =
            JSONObject(
                """{"success": true, "register_code": "29f3aac9", "url": "https://ai.xida.de/api/v10/register/auth"}""",
            )
        val result = RegisterCode.fromJson(json, baseUrl)
        assertEquals("29f3aac9", result.code)
        assertEquals("$baseUrl/register/auth?register_code=29f3aac9", result.url)
    }

    @Test
    fun registerCheck() {
        val json =
            JSONObject(
                """
                {"success": true, "api_token": "a1b2c3", "id": 42, "name": "John Doe",
                 "email": "user@example.com", "software": "de.xida.thumbkey", "admin": false}
                """,
            )
        assertEquals(Credentials(42, "a1b2c3", "user@example.com"), Credentials.fromJson(json))
    }

    @Test
    fun registerCheckMalformedThrowsXidaAiException() {
        val e =
            assertThrows(XidaAiException::class.java) {
                parseResponse { Credentials.fromJson(JSONObject("""{"success": true, "api_token": "a1b2c3"}""")) }
            }
        assertEquals("request_failed", e.message)
    }

    @Test
    fun registerCodeMalformedThrowsXidaAiException() {
        val e =
            assertThrows(XidaAiException::class.java) {
                parseResponse {
                    RegisterCode.fromJson(JSONObject("""{"success": true, "url": "https://ai.xida.de/x"}"""), baseUrl)
                }
            }
        assertEquals("request_failed", e.message)
    }

    @Test
    fun transcribeSuccess() {
        val json = JSONObject("""{"success": true, "status": "pending_attachments", "id": 456}""")
        assertEquals(456, parseTranscribeId(json))
    }

    @Test
    fun transcribeRejectedFile() {
        val json =
            JSONObject("""{"success": true, "status": "error", "id": 456, "message": "file_not_supported"}""")
        val e = assertThrows(XidaAiException::class.java) { parseTranscribeId(json) }
        assertEquals("file_not_supported", e.message)
    }

    @Test
    fun infoPending() {
        val json = JSONObject("""{"success": true, "status": "pending_attachments", "id": 456}""")
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.PENDING, status.status)
        assertNull(status.text)
        assertFalse(status.isFinished)
        assertNull(status.statusMessage)
    }

    @Test
    fun infoActiveIsNotFinished() {
        val json = JSONObject("""{"success": true, "status": "active_attachments", "id": 456}""")
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.ACTIVE, status.status)
        assertFalse(status.isFinished)
        assertNull(status.text)
    }

    @Test
    fun infoStatusMessage() {
        val json =
            JSONObject(
                """
                {"success": true, "status": "pending_attachments", "id": 456,
                 "status_message": "Transcribing audio – 42%"}
                """,
            )
        assertEquals("Transcribing audio – 42%", TranscriptionStatus.fromJson(json).statusMessage)
    }

    @Test
    fun infoComplete() {
        val json =
            JSONObject(
                """
                {"success": true, "status": "complete", "id": 456, "tokens": 0,
                 "results": [{"id": 789, "type": "audio", "status": "complete",
                              "result": {"text": "Transcribed speech ..."}}]}
                """,
            )
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.COMPLETE, status.status)
        assertEquals("Transcribed speech ...", status.text)
        assertTrue(status.isFinished)
    }

    @Test
    fun infoError() {
        val json =
            JSONObject("""{"success": true, "status": "error", "id": 456, "message": "text_not_found"}""")
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.ERROR, status.status)
        assertNull(status.text)
        assertEquals("text_not_found", status.message)
        assertTrue(status.isFinished)
    }

    @Test
    fun successFalseThrows() {
        val e =
            assertThrows(XidaAiException::class.java) {
                requireSuccess(JSONObject("""{"success": false, "message": "error_login"}"""))
            }
        assertEquals("error_login", e.message)
    }

    @Test
    fun successFalseWithNestedMessageThrows() {
        val e =
            assertThrows(XidaAiException::class.java) {
                requireSuccess(
                    JSONObject("""{"success":false,"error":1,"data":{"message":"registration_pending"}}"""),
                )
            }
        assertEquals("registration_pending", e.message)
    }

    @Test
    fun successFalseWithoutMessageThrows() {
        assertThrows(XidaAiException::class.java) {
            requireSuccess(JSONObject("""{"success": false, "status": ""}"""))
        }
    }
}
