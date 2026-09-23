package com.dessalines.thumbkey.summera

import android.content.Context
import android.content.SharedPreferences
import com.dessalines.thumbkey.BuildConfig
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.TranscriptionPrompt
import de.xida.aichatapi.XidaAiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Has to exist in the API's apps table, swap for "de.xida.ai" if it does not
const val SOFTWARE_ID = "de.xida.thumbkey"

// Shared by the sign-in and the transcription polling
const val POLL_INTERVAL_MS = 2_000L
const val POLL_TIMEOUT_MS = 180_000L

// The link the browser returns to after the sign-in. Has to match the intent filter of
// MainActivity in the manifest
const val SUMMERA_LOGIN_SCHEME = "thumbkey"
const val SUMMERA_LOGIN_HOST = "summera-login"
const val SUMMERA_LOGIN_URL = "$SUMMERA_LOGIN_SCHEME://$SUMMERA_LOGIN_HOST"

/**
 * Polls [attempt] every [intervalMs] until it returns a non-null value, giving up after
 * [timeoutMs]. Shared by the sign-in and the dictation paths so the timing contract lives here.
 */
suspend fun <T> poll(
    timeoutMs: Long = POLL_TIMEOUT_MS,
    intervalMs: Long = POLL_INTERVAL_MS,
    attempt: suspend () -> T?,
): T? =
    withTimeoutOrNull(timeoutMs) {
        var result: T?
        do {
            delay(intervalMs)
            result = attempt()
        } while (result == null)
        result
    }

private const val PREFS_NAME = "summera"
private const val KEY_USER_ID = "user_id"
private const val KEY_API_TOKEN = "api_token"
private const val KEY_EMAIL = "email"
private const val KEY_PENDING_CODE = "pending_code"
private const val KEY_PENDING_STARTED_AT = "pending_started_at"
private const val KEY_LAST_CRASH = "last_crash"
private const val KEY_TRANSCRIPTS_RESTORED = "transcripts_restored"
private const val KEY_ACTIVE_PROMPT_ID = "active_prompt_id"
private const val KEY_ACTIVE_PROMPT_TEXT = "active_prompt_text"
private const val LAST_CRASH_MAX_CHARS = 800
private const val KEY_DEBUG_LOG = "debug_log"
private const val KEY_DEBUG_LOGGING = "debug_logging"
private const val DEBUG_LOG_MAX_LINES = 40
private const val DEBUG_LOG_REPEAT_WINDOW = 3

object SummeraAccount {
    /**
     * The one client every Summera call goes through. While the debug switch is on, every request
     * and answer lands in the in-app log through [log]; the library redacts tokens, passwords and
     * register codes, the dictated text is logged as is.
     */
    fun client(context: Context): XidaAiClient {
        // Not the Activity, the lambda outlives the screen
        val appContext = context.applicationContext
        // With the switch off no logger is passed at all, so the client skips building the lines
        val logger: ((String) -> Unit)? = if (debugLoggingEnabled(context)) ({ line -> log(appContext, line) }) else null
        return XidaAiClient(software = SOFTWARE_ID, appVersion = BuildConfig.VERSION_NAME, logger = logger)
    }

    /** The user-facing debug switch on the Debug log screen. Off by default, in every build type. */
    fun debugLoggingEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_DEBUG_LOGGING, false)

    fun setDebugLogging(
        context: Context,
        enabled: Boolean,
    ) {
        prefs(context).edit().putBoolean(KEY_DEBUG_LOGGING, enabled).apply()
    }

    fun save(
        context: Context,
        credentials: Credentials,
    ) {
        prefs(context)
            .edit()
            .putInt(KEY_USER_ID, credentials.userId)
            .putString(KEY_API_TOKEN, credentials.apiToken)
            .putString(KEY_EMAIL, credentials.email)
            .apply()
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun credentials(context: Context): Credentials? {
        val prefs = prefs(context)
        val apiToken = prefs.getString(KEY_API_TOKEN, null) ?: return null
        return Credentials(
            userId = prefs.getInt(KEY_USER_ID, 0),
            apiToken = apiToken,
            email = prefs.getString(KEY_EMAIL, null).orEmpty(),
        )
    }

    /** True once the transcript history has pulled the earlier transcripts of this sign-in; [clear] resets it. */
    fun transcriptsRestored(context: Context): Boolean = prefs(context).getBoolean(KEY_TRANSCRIPTS_RESTORED, false)

    fun setTranscriptsRestored(context: Context) {
        prefs(context).edit().putBoolean(KEY_TRANSCRIPTS_RESTORED, true).apply()
    }

    /**
     * The saved prompt every dictation sends, or null for none. The text is kept locally so the
     * upload needs no extra call; [clear] on sign-out wipes it.
     */
    fun saveActivePrompt(
        context: Context,
        prompt: TranscriptionPrompt?,
    ) {
        val editor = prefs(context).edit()
        if (prompt == null) {
            editor.remove(KEY_ACTIVE_PROMPT_ID).remove(KEY_ACTIVE_PROMPT_TEXT)
        } else {
            editor.putString(KEY_ACTIVE_PROMPT_ID, prompt.id).putString(KEY_ACTIVE_PROMPT_TEXT, prompt.text)
        }
        editor.apply()
    }

    fun activePromptId(context: Context): String? = prefs(context).getString(KEY_ACTIVE_PROMPT_ID, null)

    fun activePromptText(context: Context): String? = prefs(context).getString(KEY_ACTIVE_PROMPT_TEXT, null)

    /** Remembers the register code of a running sign-in, so it survives the screen or the activity dying. */
    fun savePending(
        context: Context,
        code: String,
    ) {
        prefs(context)
            .edit()
            .putString(KEY_PENDING_CODE, code)
            .putLong(KEY_PENDING_STARTED_AT, System.currentTimeMillis())
            .apply()
    }

    /** The pending register code, or null once it is older than the poll window (it is dead by then). */
    fun pendingCode(context: Context): String? {
        val prefs = prefs(context)
        val code = prefs.getString(KEY_PENDING_CODE, null) ?: return null
        if (System.currentTimeMillis() - prefs.getLong(KEY_PENDING_STARTED_AT, 0L) < POLL_TIMEOUT_MS) return code
        clearPending(context)
        return null
    }

    fun clearPending(context: Context) {
        prefs(context)
            .edit()
            .remove(KEY_PENDING_CODE)
            .remove(KEY_PENDING_STARTED_AT)
            .apply()
    }

    /** Debug breadcrumb of an uncaught exception. commit(), not apply(): the process is dying. */
    fun saveLastCrash(
        context: Context,
        text: String,
    ) {
        prefs(context).edit().putString(KEY_LAST_CRASH, text.take(LAST_CRASH_MAX_CHARS)).commit()
    }

    fun lastCrash(context: Context): String? = prefs(context).getString(KEY_LAST_CRASH, null)

    fun clearLastCrash(context: Context) {
        prefs(context).edit().remove(KEY_LAST_CRASH).apply()
    }

    /** The one way a line gets recorded: the in-app debug log, only while the switch is on. Nothing goes to logcat. */
    fun log(
        context: Context,
        line: String,
    ) {
        if (!debugLoggingEnabled(context)) return
        appendDebugLog(context, line)
    }

    // ponytail: the user-facing debug log of every Summera call, shown on the Debug log screen.
    // Capped at DEBUG_LOG_MAX_LINES in SharedPreferences; a file would be the upgrade if that is too little
    @Synchronized
    private fun appendDebugLog(
        context: Context,
        line: String,
    ) {
        val lines = debugLog(context)?.lines().orEmpty()
        // One poll is three lines (request, response, reason) and 90 polls would push the
        // register/code lines out of the cap, so a poll repeating the last one is not written again
        // A pretty-printed response body must stay one entry
        val flat = line.lines().joinToString(" ")
        if (lines.takeLast(DEBUG_LOG_REPEAT_WINDOW).any { it.substringAfter(' ') == flat }) return
        val stamped = "${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())} $flat"
        prefs(context)
            .edit()
            .putString(KEY_DEBUG_LOG, (lines + stamped).takeLast(DEBUG_LOG_MAX_LINES).joinToString("\n"))
            .apply()
    }

    fun debugLog(context: Context): String? = prefs(context).getString(KEY_DEBUG_LOG, null)

    fun clearDebugLog(context: Context) {
        prefs(context).edit().remove(KEY_DEBUG_LOG).apply()
    }

    private fun prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
