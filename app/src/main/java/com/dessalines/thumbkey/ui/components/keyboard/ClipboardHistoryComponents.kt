package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R

@Composable
internal fun ClipboardDisabledView(
    onGoToClipboardSettings: () -> Unit,
    cornerRadius: Float,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            Text(
                text = stringResource(R.string.clipboard_disabled),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onGoToClipboardSettings,
                shape = RoundedCornerShape(cornerRadius.dp),
            ) {
                Text(stringResource(R.string.clipboard_go_to_settings))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ClipboardHeader(
    state: ClipboardHeaderState,
    actions: ClipboardHeaderActions,
) {
    ClipboardHeaderSurface(state, actions)
}

/** Layout and visibility settings for the clipboard header. */
internal data class ClipboardHeaderState(
    val title: String,
    val showClearAll: Boolean,
    val height: Dp,
    val keyPadding: Int,
    val cornerRadius: Float,
)

/** Actions handled by the clipboard header controls. */
internal data class ClipboardHeaderActions(
    val onBack: () -> Unit,
    val onClearAll: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClipboardHeaderSurface(
    state: ClipboardHeaderState,
    actions: ClipboardHeaderActions,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(state.height)
                .padding(state.keyPadding.dp)
                .clip(RoundedCornerShape(state.cornerRadius.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ClipboardBackButton(actions.onBack)

            Text(
                text = state.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Row {
                if (state.showClearAll) ClipboardClearAllButton(actions.onClearAll)
                ClipboardBackButton(actions.onBack)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ClipboardBackButton(onBack: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(stringResource(R.string.clipboard_back)) } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.clipboard_back),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ClipboardClearAllButton(onClearAll: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = { PlainTooltip { Text(stringResource(R.string.clipboard_clear_all)) } },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onClearAll) {
            Icon(
                imageVector = Icons.Outlined.DeleteSweep,
                contentDescription = stringResource(R.string.clipboard_clear_all),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
