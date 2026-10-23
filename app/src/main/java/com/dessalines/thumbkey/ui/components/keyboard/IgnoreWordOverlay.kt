package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.AppDB
import com.dessalines.thumbkey.db.WordUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val SCRIM_ALPHA = 0.6f
private val LARGE_SPACING = 24.dp
private val MEDIUM_SPACING = 16.dp

// The IME has no reliable window token for a dialog, so show this alert inside the keyboard.
@Composable
fun IgnoreWordOverlay(
    word: WordUsage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current as IMEService
    val dao = remember { AppDB.getDatabase(ctx).wordUsageDao() }
    val scope = rememberCoroutineScope()

    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = SCRIM_ALPHA))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LARGE_SPACING)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier =
                    Modifier.padding(
                        start = LARGE_SPACING,
                        top = LARGE_SPACING,
                        end = LARGE_SPACING,
                        bottom = MEDIUM_SPACING,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.word_suggestion_ignore_confirm, word.word),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = MEDIUM_SPACING),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) { dao.ignoreById(word.id) }
                                onDismiss()
                            }
                        },
                    ) {
                        Text(stringResource(R.string.word_suggestion_ignore))
                    }
                }
            }
        }
    }
}
