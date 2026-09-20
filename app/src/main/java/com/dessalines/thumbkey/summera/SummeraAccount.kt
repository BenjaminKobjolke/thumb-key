package com.dessalines.thumbkey.summera

import android.content.Context
import android.content.SharedPreferences
import com.dessalines.thumbkey.BuildConfig
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.XidaAiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

// Has to exist in the API's apps table, swap for "de.xida.ai" if it does not
const val SOFTWARE_ID = "de.xida.thumbkey"

// Shared by the sign-in and the transcription polling
const val POLL_INTERVAL_MS = 2_000L
const val POLL_TIMEOUT_MS = 180_000L

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

object SummeraAccount {
    fun client(): XidaAiClient = XidaAiClient(software = SOFTWARE_ID, appVersion = BuildConfig.VERSION_NAME)

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

    private fun prefs(context: Context): SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
