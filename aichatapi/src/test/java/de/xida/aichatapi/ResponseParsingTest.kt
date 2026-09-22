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
    fun infoPendingAiIsNotFinished() {
        val json = JSONObject("""{"success": true, "status": "pending_ai", "id": 456}""")
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.PENDING_AI, status.status)
        assertFalse(status.isFinished)
        assertNull(status.text)
    }

    @Test
    fun infoActiveAiIsNotFinished() {
        val json = JSONObject("""{"success": true, "status": "active_ai", "id": 456}""")
        val status = TranscriptionStatus.fromJson(json)
        assertEquals(TranscriptionStatus.ACTIVE_AI, status.status)
        assertFalse(status.isFinished)
        assertNull(status.text)
    }

    @Test
    fun infoCompleteWithPrompt() {
        val json =
            JSONObject(
                """
                {"success": true, "status": "complete", "id": 456, "tokens": 0,
                 "results": [{"id": 789, "type": "text", "status": "complete",
                              "result": {"text": "raw transcript"}},
                             {"id": 790, "type": "ai", "success": 1, "prompt_text": "fix grammar",
                              "result": {"text": "Raw transcript."}}]}
                """,
            )
        val status = TranscriptionStatus.fromJson(json)
        assertEquals("raw transcript", status.text)
        assertEquals("Raw transcript.", status.promptResult)
        assertFalse(status.promptFailed)
        assertTrue(status.isFinished)
    }

    @Test
    fun infoCompleteWithFailedPrompt() {
        val json =
            JSONObject(
                """
                {"success": true, "status": "complete", "id": 456,
                 "results": [{"id": 789, "type": "text", "result": {"text": "raw transcript"}},
                             {"id": 790, "type": "ai", "success": 0, "prompt_text": "fix grammar",
                              "result": {"text": ""}}]}
                """,
            )
        val status = TranscriptionStatus.fromJson(json)
        assertEquals("raw transcript", status.text)
        assertNull(status.promptResult)
        assertTrue(status.promptFailed)
    }

    @Test
    fun infoCompleteWithoutPrompt() {
        val json =
            JSONObject(
                """
                {"success": true, "status": "complete", "id": 456,
                 "results": [{"id": 789, "type": "text", "result": {"text": "raw transcript"}}]}
                """,
            )
        val status = TranscriptionStatus.fromJson(json)
        assertNull(status.promptResult)
        assertFalse(status.promptFailed)
    }

    @Test
    fun listPage() {
        val json =
            JSONObject(
                """
                {"success": true,
                 "pagination": {"start": 0, "limit": 20, "amount": 2, "total": 7},
                 "transcriptions": [
                   {"id": 912, "status": "complete", "created_at": "2026-09-22 10:15:03", "language": "de",
                    "text": "raw transcript", "prompt": "fix grammar", "prompt_status": "complete",
                    "prompt_result": "Raw transcript."},
                   {"id": 901, "status": "pending_attachments", "created_at": "2026-09-21 08:00:00",
                    "language": null, "text": null, "prompt": null, "prompt_status": null, "prompt_result": null}
                 ]}
                """,
            )
        val page = parseTranscriptionPage(json)
        assertEquals(0, page.start)
        assertEquals(7, page.total)
        assertEquals(2, page.items.size)
        assertEquals(
            TranscriptionSummary(
                912,
                "complete",
                "2026-09-22 10:15:03",
                "de",
                "raw transcript",
                "fix grammar",
                "complete",
                "Raw transcript.",
            ),
            page.items[0],
        )
        assertEquals(
            TranscriptionSummary(901, "pending_attachments", "2026-09-21 08:00:00", null, null, null, null, null),
            page.items[1],
        )
    }

    @Test
    fun listEmpty() {
        val json =
            JSONObject(
                """{"success": true, "pagination": {"start": 0, "limit": 20, "amount": 0, "total": 0}, "transcriptions": []}""",
            )
        val page = parseTranscriptionPage(json)
        assertEquals(0, page.total)
        assertTrue(page.items.isEmpty())
    }

    @Test
    fun createdAtMillisParsesServerTime() {
        val item = TranscriptionSummary(1, "complete", "2026-09-22 10:15:00", null, "x", null, null, null)
        // 10:15 CEST = 08:15 UTC
        assertEquals(1790064900000L, item.createdAtMillis(fallback = 0L))
        val winter = item.copy(createdAt = "2026-01-10 12:00:00")
        // 12:00 CET = 11:00 UTC
        assertEquals(1768042800000L, winter.createdAtMillis(fallback = 0L))
    }

    @Test
    fun createdAtMillisMalformedUsesFallback() {
        val item = TranscriptionSummary(1, "complete", "not a date", null, "x", null, null, null)
        assertEquals(42L, item.createdAtMillis(fallback = 42L))
        assertEquals(42L, item.copy(createdAt = "").createdAtMillis(fallback = 42L))
    }

    @Test
    fun transcriptionPromptsRoundTrip() {
        val prompts =
            listOf(
                TranscriptionPrompt("a1", "correct spelling and grammar errors", "2026-09-22T10:00:00Z", "2026-09-22T10:00:00Z"),
                TranscriptionPrompt("b2", "summarize", "2026-09-22T11:00:00Z", "2026-09-22T12:00:00Z"),
            )
        val encoded = encodeTranscriptionPrompts(prompts)
        assertEquals(1, JSONObject(encoded).getInt("version"))
        assertEquals(prompts, parseTranscriptionPrompts(encoded))
    }

    @Test
    fun transcriptionPromptsEmptyValue() {
        val json = JSONObject("""{"success": true, "settings": [{"key": "transcriptionPrompts", "value": ""}]}""")
        assertTrue(parseTranscriptionPromptsSetting(json).isEmpty())
    }

    @Test
    fun transcriptionPromptsUnknownVersionIsEmpty() {
        assertTrue(parseTranscriptionPrompts("""{"version": 2, "items": [{"id": "x"}]}""").isEmpty())
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
