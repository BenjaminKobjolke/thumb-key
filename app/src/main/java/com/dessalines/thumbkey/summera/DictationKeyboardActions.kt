package com.dessalines.thumbkey.summera

import android.content.Intent
import android.view.inputmethod.EditorInfo
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.MainActivity

internal fun openDictationReplacements(
    ime: IMEService,
    transcript: String,
) {
    ime.startActivity(
        Intent(ime, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("startRoute", "summeraReplacements")
            .putExtra(EXTRA_TRANSCRIPT, transcript),
    )
}

internal fun dismissDictation(ime: IMEService) {
    SummeraDictation.cancel(ime)
    val editor = ime.currentInputEditorInfo
    if (ime.currentInputConnection == null || editor == null || editor.inputType == EditorInfo.TYPE_NULL) {
        ime.requestHideSelf(0)
    }
}
