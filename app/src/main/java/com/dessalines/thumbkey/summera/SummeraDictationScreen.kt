package com.dessalines.thumbkey.summera

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.R
import de.xida.aichatapi.TranscriptionStatus
import kotlinx.coroutines.delay

private val PADDING = 12.dp
private val ICON_SIZE = 40.dp

@Composable
fun SummeraDictationScreen(
    state: DictationState,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
) {
    when (state) {
        is DictationState.Recording -> {
            RecordingView(startedAt = state.startedAt, onStop = onStop)
        }

        is DictationState.Failed -> {
            StatusView(text = stringResource(R.string.summera_failed, state.message)) {
                Button(onClick = onRetry) { Text(stringResource(R.string.summera_retry)) }
                OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.summera_close)) }
            }
        }

        // Uploading and polling: tell the user what is going on, with the texts of the Summera AI app
        else -> {
            val result = (state as? DictationState.Polling)?.result
            StatusView(
                text =
                    result?.statusMessage ?: stringResource(
                        when {
                            state is DictationState.Uploading -> R.string.summera_uploading
                            result?.status == TranscriptionStatus.PENDING -> R.string.summera_pending
                            result?.status == TranscriptionStatus.ACTIVE -> R.string.summera_active
                            // Unknown in-progress status, or the first poll is not back yet
                            else -> R.string.summera_transcribing
                        },
                    ),
                showProgress = true,
            ) {
                OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.summera_cancel)) }
            }
        }
    }
}

@Composable
private fun RecordingView(
    startedAt: Long,
    onStop: () -> Unit,
) {
    var elapsedSeconds by remember(startedAt) { mutableLongStateOf(0L) }
    LaunchedEffect(startedAt) {
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - startedAt) / DateUtils.SECOND_IN_MILLIS
            delay(DateUtils.SECOND_IN_MILLIS)
        }
    }

    Button(
        onClick = onStop,
        shape = RoundedCornerShape(PADDING),
        modifier =
            Modifier
                .fillMaxSize()
                .padding(PADDING),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PADDING),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PADDING),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = stringResource(R.string.summera_recording),
                )
                Text(
                    text = DateUtils.formatElapsedTime(elapsedSeconds),
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            Icon(
                imageVector = Icons.Outlined.Stop,
                contentDescription = null,
                modifier = Modifier.size(ICON_SIZE),
            )
            Text(stringResource(R.string.summera_stop))
        }
    }
}

@Composable
private fun StatusView(
    text: String,
    showProgress: Boolean = false,
    buttons: @Composable () -> Unit,
) {
    // Solid panel like RecordingView: without it the app behind the keyboard shows through
    Surface(
        shape = RoundedCornerShape(PADDING),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier =
            Modifier
                .fillMaxSize()
                .padding(PADDING),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PADDING, Alignment.CenterVertically),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(PADDING),
        ) {
            if (showProgress) {
                CircularProgressIndicator()
            }
            Text(
                text = text,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(PADDING)) {
                buttons()
            }
        }
    }
}
