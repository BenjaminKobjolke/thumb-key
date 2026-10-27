package com.dessalines.thumbkey.ui.components.settings.summera

import android.content.Context
import android.content.Intent
import android.util.Log
import com.dessalines.thumbkey.MainActivity
import com.dessalines.thumbkey.summera.poll
import com.dessalines.thumbkey.utils.TAG
import de.xida.aichatapi.Credentials
import de.xida.aichatapi.XidaAiClient
import de.xida.aichatapi.XidaAiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// check() fails until the browser leg is done
internal suspend fun awaitSignIn(
    client: XidaAiClient,
    code: String,
    log: (String) -> Unit,
): Credentials? =
    poll {
        try {
            withContext(Dispatchers.IO) { client.auth.check(code) }
        } catch (e: XidaAiException) {
            // A wrong response shape shows up as a repeating reason instead of silence
            log("check: pending (${e.message})")
            null
        }
    }

internal fun bringToFront(ctx: Context) {
    // ponytail: the return is a bonus. Android 10+ and several OEM skins block or reject an
    // activity start from the background; the sign-in is already saved and the screen shows it on
    // the next resume. NEW_TASK also covers a Custom Tab that ended up in the browser's own task
    try {
        ctx.startActivity(
            Intent(ctx, MainActivity::class.java)
                .addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP,
                ),
        )
    } catch (e: Exception) {
        // The credentials are saved, the screen shows them on the next resume
        Log.w(TAG, "Could not return from the sign-in page", e)
    }
}
