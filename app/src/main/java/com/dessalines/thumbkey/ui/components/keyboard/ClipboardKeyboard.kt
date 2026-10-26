package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.ClipboardItem
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.summera.DictationState
import com.dessalines.thumbkey.summera.SummeraDictation
import com.dessalines.thumbkey.summera.SummeraDictationScreen
import com.dessalines.thumbkey.utils.KeyboardMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal data class ClipboardScreenData(
    val ime: IMEService,
    val repository: ClipboardRepository,
    val mode: KeyboardMode,
    val dictationState: DictationState,
    val dictationActive: Boolean,
    val clipboardItems: List<ClipboardItem>,
    val transcripts: List<ClipboardItem>,
    val keyboardRowCount: Int,
    val onReturnToKeyboard: () -> Unit,
)

private data class ClipboardFrameOptions(
    val scope: CoroutineScope,
    val showTranscripts: Boolean,
    val onGoToClipboardSettings: () -> Unit,
    val environment: KeyboardEnvironment,
)

@Composable
internal fun ClipboardKeyboard(
    data: ClipboardScreenData,
    environment: KeyboardEnvironment,
    onGoToClipboardSettings: () -> Unit,
) {
    val config = environment.config
    val showTranscripts = data.mode == KeyboardMode.TRANSCRIPTS
    val scope = CoroutineScope(Dispatchers.IO)
    val keyboardHeight = environment.keyHeight * data.keyboardRowCount
    val frameOptions = ClipboardFrameOptions(scope, showTranscripts, onGoToClipboardSettings, environment)

    ClipboardEffects(data, showTranscripts, config.clipboardHistoryEnabled)

    KeyboardFrame(KeyboardFrameStyle(config.backdropEnabled, MaterialTheme.colorScheme.background)) {
        ClipboardFrame(data, frameOptions, keyboardHeight)
    }
}

@Composable
private fun ClipboardEffects(
    data: ClipboardScreenData,
    showTranscripts: Boolean,
    clipboardHistoryEnabled: Boolean,
) {
    if (!data.dictationActive && !showTranscripts) {
        LaunchedEffect(Unit) {
            if (clipboardHistoryEnabled) {
                data.repository.clearExpired()
            } else {
                data.repository.clearAll()
            }
        }
    }

    if (!data.dictationActive && showTranscripts) {
        LaunchedEffect(Unit) {
            SummeraDictation.restoreTranscripts(data.ime, data.repository)
        }
    }
}

@Composable
private fun ClipboardFrame(
    data: ClipboardScreenData,
    options: ClipboardFrameOptions,
    keyboardHeight: Float,
) {
    val config = options.environment.config
    Box(
        modifier =
            Modifier
                .keyboardBottomInset(config.ignoreBottomPadding, config.pushupSize.dp)
                .fillMaxWidth()
                .height(keyboardHeight.dp)
                .keyboardBackdropPadding(config.backdropEnabled, 6.dp),
    ) {
        if (data.dictationActive) {
            DictationScreen(data)
        } else {
            ClipboardHistory(data, options)
        }
    }
}

@Composable
private fun DictationScreen(data: ClipboardScreenData) {
    SummeraDictationScreen(
        state = data.dictationState,
        onStop = { SummeraDictation.stopAndSend(data.ime) },
        onCancel = { SummeraDictation.cancel(data.ime) },
        onRetry = { SummeraDictation.retry(data.ime) },
        onInsert = { SummeraDictation.insert(data.ime, it) },
        onReplacements = { SummeraDictation.openReplacements(data.ime, it) },
        onDismiss = { SummeraDictation.dismiss(data.ime) },
    )
}

@Composable
private fun ClipboardHistory(
    data: ClipboardScreenData,
    options: ClipboardFrameOptions,
) {
    val repository = data.repository
    val environment = options.environment
    val showTranscripts = options.showTranscripts
    ClipboardHistoryScreen(
        clipboardItems = if (showTranscripts) data.transcripts else data.clipboardItems,
        title = stringResource(if (showTranscripts) R.string.transcript_history else R.string.clipboard_history),
        emptyText = stringResource(if (showTranscripts) R.string.transcript_empty else R.string.clipboard_empty),
        isEnabled = showTranscripts || environment.config.clipboardHistoryEnabled,
        onItemClick = { item ->
            data.ime.currentInputConnection.commitText(item.text, 1)
            data.onReturnToKeyboard()
        },
        onItemPaste = { item -> data.ime.currentInputConnection.commitText(item.text, 1) },
        onItemDelete = { item -> options.scope.launch { repository.deleteItem(item) } },
        onItemTogglePin = { item -> options.scope.launch { repository.togglePin(item) } },
        onBack = data.onReturnToKeyboard,
        onClearAll = {
            options.scope.launch {
                if (showTranscripts) repository.clearUnpinnedTranscripts() else repository.clearUnpinned()
            }
        },
        onGoToClipboardSettings = options.onGoToClipboardSettings,
        keyHeight = environment.keyHeight,
        keyPadding = environment.config.keyPadding,
        cornerRadius = environment.keyRadius,
        vibrateOnTap = environment.config.vibrateOnTap,
    )
}
