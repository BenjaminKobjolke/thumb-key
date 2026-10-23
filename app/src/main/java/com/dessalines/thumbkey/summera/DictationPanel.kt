package com.dessalines.thumbkey.summera

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/** Solid panel so the app behind the keyboard does not show through. */
@Composable
internal fun DictationPanel(
    padding: Dp,
    content: @Composable () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(padding),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxSize().padding(padding),
        content = content,
    )
}
