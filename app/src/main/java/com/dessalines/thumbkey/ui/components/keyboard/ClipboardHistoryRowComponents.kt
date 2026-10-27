package com.dessalines.thumbkey.ui.components.keyboard

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.ClipboardItem

private fun formatTimestamp(timestamp: Long): String =
    android.text.format.DateUtils
        .getRelativeTimeSpanString(
            timestamp,
            System.currentTimeMillis(),
            android.text.format.DateUtils.SECOND_IN_MILLIS,
            android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE,
        ).toString()

@Composable
internal fun ClipboardItemRow(
    item: ClipboardItem,
    actions: ClipboardHistoryActions,
    style: ClipboardHistoryStyle,
) {
    var showContextMenu by remember { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current
    val view = LocalView.current

    Box {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(style.cornerRadius.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { actions.onItemClick(item) },
                            onLongPress = { offset ->
                                pressOffset = offset
                                showContextMenu = true
                                if (style.vibrateOnTap) {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                }
                            },
                        )
                    },
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            ClipboardItemSummary(item)
        }

        val menuOffset =
            with(density) {
                DpOffset(pressOffset.x.toDp(), pressOffset.y.toDp())
            }
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
            offset = menuOffset,
            properties = PopupProperties(focusable = false),
        ) {
            ClipboardItemMenuItems(
                item = item,
                actions = actions,
                onDismiss = { showContextMenu = false },
            )
        }
    }
}

@Composable
private fun ClipboardItemSummary(item: ClipboardItem) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (item.isPinned) {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = null,
                modifier = Modifier.size(spacing),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = item.text.replace("\n", " ").replace("\r", ""),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis,
            modifier = Modifier.weight(1f),
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatTimestamp(item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = stringResource(R.string.clipboard_characters, item.text.length),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ClipboardItemMenuItems(
    item: ClipboardItem,
    actions: ClipboardHistoryActions,
    onDismiss: () -> Unit,
) {
    ClipboardPasteMenuItem(item, actions, onDismiss)
    ClipboardDeleteMenuItem(item, actions, onDismiss)
    ClipboardPinMenuItem(item, actions, onDismiss)
}

@Composable
private fun ClipboardPasteMenuItem(
    item: ClipboardItem,
    actions: ClipboardHistoryActions,
    onDismiss: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.clipboard_paste)) },
        onClick = {
            onDismiss()
            actions.onItemPaste(item)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.ContentPaste,
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun ClipboardDeleteMenuItem(
    item: ClipboardItem,
    actions: ClipboardHistoryActions,
    onDismiss: () -> Unit,
) {
    DropdownMenuItem(
        text = { Text(stringResource(R.string.clipboard_delete)) },
        onClick = {
            onDismiss()
            actions.onItemDelete(item)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun ClipboardPinMenuItem(
    item: ClipboardItem,
    actions: ClipboardHistoryActions,
    onDismiss: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                if (item.isPinned) {
                    stringResource(R.string.clipboard_unpin)
                } else {
                    stringResource(R.string.clipboard_pin)
                },
            )
        },
        onClick = {
            onDismiss()
            actions.onItemTogglePin(item)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = null,
            )
        },
    )
}
