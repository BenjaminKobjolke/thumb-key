package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun SuggestionBarSlots(
    modifier: Modifier = Modifier,
    left: @Composable () -> Unit,
    center: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        SuggestionBarSlot(Modifier.weight(1f), left)
        SuggestionBarSlot(Modifier.weight(1f), center)
        SuggestionBarSlot(Modifier.weight(1f), right)
    }
}

@Composable
private fun SuggestionBarSlot(
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
