package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.db.ClipboardItem

val spacing = 16.dp

internal data class ClipboardHistoryActions(
    val onItemClick: (ClipboardItem) -> Unit,
    val onItemPaste: (ClipboardItem) -> Unit,
    val onItemDelete: (ClipboardItem) -> Unit,
    val onItemTogglePin: (ClipboardItem) -> Unit,
    val onBack: () -> Unit,
    val onClearAll: () -> Unit,
    val onGoToClipboardSettings: () -> Unit,
)

internal data class ClipboardHistoryStyle(
    val keyHeight: Float,
    val keyPadding: Int,
    val cornerRadius: Float,
    val vibrateOnTap: Boolean,
)

@Composable
internal fun ClipboardHistoryScreen(
    clipboardItems: List<ClipboardItem>,
    title: String,
    emptyText: String,
    isEnabled: Boolean,
    actions: ClipboardHistoryActions,
    style: ClipboardHistoryStyle,
    modifier: Modifier = Modifier,
) {
    val headerHeight = (style.keyHeight * 0.6f).dp
    val backdropColor = MaterialTheme.colorScheme.surfaceContainerLow

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(backdropColor),
    ) {
        ClipboardHeader(
            state =
                ClipboardHeaderState(
                    title = title,
                    showClearAll = isEnabled,
                    height = headerHeight,
                    keyPadding = style.keyPadding,
                    cornerRadius = style.cornerRadius,
                ),
            actions = ClipboardHeaderActions(actions.onBack, actions.onClearAll),
        )

        if (!isEnabled) {
            ClipboardDisabledView(
                onGoToClipboardSettings = actions.onGoToClipboardSettings,
                cornerRadius = style.cornerRadius,
            )
        } else if (clipboardItems.isEmpty()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(spacing),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            val pinnedItems = clipboardItems.filter { it.isPinned }
            val unpinnedItems = clipboardItems.filter { !it.isPinned }

            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = style.keyPadding.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (pinnedItems.isNotEmpty()) {
                    items(pinnedItems, key = { it.id }) { item ->
                        ClipboardItemRow(item = item, actions = actions, style = style)
                    }
                }

                if (unpinnedItems.isNotEmpty()) {
                    items(unpinnedItems, key = { it.id }) { item ->
                        ClipboardItemRow(item = item, actions = actions, style = style)
                    }
                }
            }
        }
    }
}
