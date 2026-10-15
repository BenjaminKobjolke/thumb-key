package com.dessalines.thumbkey.summera

import android.content.Context
import android.media.MediaPlayer
import com.dessalines.thumbkey.R

/** Sounds played while Summera processes a dictation. */
object SummeraSounds {
    private var waitingPlayer: MediaPlayer? = null

    fun startWaiting(context: Context) {
        if (!SummeraAccount.waitSoundsEnabled(context)) return

        stopWaiting()
        waitingPlayer =
            MediaPlayer
                .create(context.applicationContext, R.raw.summera_waiting)
                ?.apply {
                    isLooping = true
                    start()
                }
    }

    fun stopWaiting() {
        waitingPlayer?.let { player ->
            player.stop()
            player.release()
        }
        waitingPlayer = null
    }

    fun playDone(context: Context) {
        if (!SummeraAccount.waitSoundsEnabled(context)) return

        MediaPlayer
            .create(context.applicationContext, R.raw.summera_done)
            ?.apply {
                setOnCompletionListener { it.release() }
                start()
            }
    }
}
