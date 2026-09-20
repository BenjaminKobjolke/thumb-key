package com.dessalines.thumbkey.summera

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dessalines.thumbkey.BuildConfig
import com.dessalines.thumbkey.utils.TAG
import de.xida.aichatapi.Credentials
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
private const val LAST_CRASH_MAX_CHARS = 800
private const val KEY_DEBUG_LOG = "debug_log"
private const val DEBUG_LOG_MAX_LINES = 40
private const val DEBUG_LOG_REPEAT_WINDOW = 3

object SummeraAccount {
    fun client(): XidaAiClient = XidaAiClient(software = SOFTWARE_ID, appVersion = BuildConfig.VERSION_NAME)

    /**
     * The client of the sign-in, which logs its calls in debug builds. Dictation keeps [client]:
     * its responses carry the dictated text, which has no business in a log.
     */
    fun signInClient(context: Context): XidaAiClient {
        // Not the Activity, the lambda outlives the screen
        val appContext = context.applicationContext
        // Release builds pass no logger at all, so the client skips building the lines
        val logger: ((String) -> Unit)? = if (BuildConfig.DEBUG) ({ line -> logSignIn(appContext, line) }) else null
        return XidaAiClient(software = SOFTWARE_ID, appVersion = BuildConfig.VERSION_NAME, logger = logger)
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

    /** The one way a sign-in line gets recorded: logcat and the debug log, in debug builds only. */
    fun logSignIn(
        context: Context,
        line: String,
    ) {
        if (!BuildConfig.DEBUG) return
        Log.d(TAG, line)
        appendDebugLog(context, line)
    }

    // ponytail: debug-only log of the sign-in calls, shown on the Summera AI screen. Delete it
    // together with the lastCrash functions once the sign-in is confirmed working
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
