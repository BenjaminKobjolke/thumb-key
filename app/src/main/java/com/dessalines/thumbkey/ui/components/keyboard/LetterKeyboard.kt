package com.dessalines.thumbkey.ui.components.keyboard

import android.util.Log
import android.view.inputmethod.InputConnection.CURSOR_UPDATE_MONITOR
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.R
import com.dessalines.thumbkey.db.WordUsage
import com.dessalines.thumbkey.keyboards.KB_EN_THUMBKEY_MAIN
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardC
import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.KeyboardMode
import com.dessalines.thumbkey.utils.KeyboardPosition
import com.dessalines.thumbkey.utils.TAG
import com.dessalines.thumbkey.utils.isUriOrEmailOrPasswordField
import com.dessalines.thumbkey.utils.keyboardPositionToAlignment

internal data class LetterKeyboardData(
    val ime: IMEService,
    val keyboardDefinition: KeyboardDefinition,
    val modeState: KeyboardModeState,
    val sketch: SketchState,
    val onModeStateChange: (KeyboardModeState) -> Unit,
)

private data class LetterKeyboardDrawing(
    val keyboard: KeyboardC,
    val definition: KeyboardDefinition,
    val mode: KeyboardMode,
    val environment: KeyboardEnvironment,
)

@Composable
internal fun LetterKeyboard(
    data: LetterKeyboardData,
    environment: KeyboardEnvironment,
) {
    val keyboard =
        modeKeyboard(data.modeState.mode, data.keyboardDefinition, data.ime) {
            data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.MAIN))
        }
    requestLetterCursorUpdates(data.ime)
    val drawing = LetterKeyboardDrawing(keyboard, data.keyboardDefinition, data.modeState.mode, environment)
    when {
        data.sketch.searching -> {
            SketchSearchKeyboard(data, environment, drawing)
        }

        environment.config.wordSuggestionsEnabled && !isUriOrEmailOrPasswordField(data.ime) -> {
            LetterKeyboardWithSuggestions(drawing)
        }

        else -> {
            LetterKeyboardLayouts(drawing)
        }
    }
}

@Composable
internal fun modeKeyboard(
    mode: KeyboardMode,
    keyboardDefinition: KeyboardDefinition,
    ime: IMEService,
    onMissingMode: () -> Unit,
): KeyboardC =
    when (mode) {
        KeyboardMode.MAIN -> {
            keyboardDefinition.modes.main
        }

        KeyboardMode.SHIFTED -> {
            keyboardDefinition.modes.shifted
        }

        KeyboardMode.NUMERIC -> {
            keyboardDefinition.modes.numeric
        }

        KeyboardMode.CTRLED -> {
            keyboardDefinition.modes.ctrled ?: missingModeKeyboard(mode, keyboardDefinition, ime, onMissingMode)
        }

        KeyboardMode.ALTED -> {
            keyboardDefinition.modes.alted ?: missingModeKeyboard(mode, keyboardDefinition, ime, onMissingMode)
        }

        else -> {
            KB_EN_THUMBKEY_MAIN
        }
    }

@Composable
private fun missingModeKeyboard(
    mode: KeyboardMode,
    keyboardDefinition: KeyboardDefinition,
    ime: IMEService,
    onMissingMode: () -> Unit,
): KeyboardC {
    val text = stringResource(R.string.warning_invalid_mode, mode, keyboardDefinition.title)
    Toast.makeText(ime, text, Toast.LENGTH_SHORT).show()
    Log.d(TAG, text)
    onMissingMode()
    return keyboardDefinition.modes.main
}

private fun requestLetterCursorUpdates(ime: IMEService) {
    if (ime.currentInputConnection.requestCursorUpdates(CURSOR_UPDATE_MONITOR)) {
        Log.d(TAG, "request for cursor updates succeeded, cursor updates will be provided")
    } else {
        Log.d(TAG, "request for cursor updates failed, cursor updates will not be provided")
    }
}

@Composable
private fun SketchSearchKeyboard(
    data: LetterKeyboardData,
    environment: KeyboardEnvironment,
    drawing: LetterKeyboardDrawing,
) {
    Column {
        SketchSearch(
            state = data.sketch,
            height = Dp(environment.keyHeight * 2),
            vibrateOnTap = environment.config.vibrateOnTap,
            soundOnTap = environment.config.soundOnTap,
            onPick = { text -> pickSketchSearchResult(data, text) },
            onClose = { data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.SKETCH)) },
        )
        Box { LetterKeyboardLayouts(drawing) }
    }
}

private fun pickSketchSearchResult(
    data: LetterKeyboardData,
    text: String,
) {
    data.sketch.searching = false
    data.sketch.list = null
    data.ime.setInputRedirect(null)
    data.ime.ignoreNextCursorMove()
    data.ime.appInputConnection.commitText(text, 1)
    data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.SKETCH))
}

@Composable
private fun LetterKeyboardWithSuggestions(drawing: LetterKeyboardDrawing) {
    val environment = drawing.environment
    var ignoringWord by remember { mutableStateOf<WordUsage?>(null) }
    Box {
        Column(
            modifier =
                Modifier.background(
                    if (environment.config.backdropEnabled) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
        ) {
            SuggestionBar(
                SuggestionBarOptions(
                    height = Dp(environment.keyHeight / 2),
                    vibrateOnTap = environment.config.vibrateOnTap,
                    soundOnTap = environment.config.soundOnTap,
                    wordCombinationsEnabled = environment.config.wordCombinationsEnabled,
                    onIgnoreRequest = { ignoringWord = it },
                ),
            )
            Box { LetterKeyboardLayouts(drawing) }
        }
        ignoringWord?.let { word ->
            IgnoreWordOverlay(word, onDismiss = { ignoringWord = null }, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun LetterKeyboardLayouts(drawing: LetterKeyboardDrawing) {
    val environment = drawing.environment
    val config = environment.config
    val positionPadding = config.positionPadding
    val alignment = keyboardPositionToAlignment(config.position)
    LetterKeyboardLayout(drawing, alignment, config.backdropEnabled, positionPadding)
    if (config.position == KeyboardPosition.Dual) {
        LetterKeyboardLayout(drawing, keyboardPositionToAlignment(KeyboardPosition.Right), false, positionPadding)
    }
}

@Composable
private fun LetterKeyboardLayout(
    drawing: LetterKeyboardDrawing,
    alignment: Alignment,
    drawBackdrop: Boolean,
    positionPadding: Int,
) {
    val environment = drawing.environment
    val config = environment.config
    val modifierPositionPadding =
        if (positionPadding > 0) Modifier.padding(start = positionPadding.dp) else Modifier.padding(end = -positionPadding.dp)
    KeyboardFrame(
        style =
            KeyboardFrameStyle(
                backdropEnabled = drawBackdrop,
                backdropColor = MaterialTheme.colorScheme.background,
                contentAlignment = alignment,
                modifier =
                    Modifier
                        .keyboardBottomInset(config.ignoreBottomPadding, config.pushupSize.dp)
                        .keyboardBackdropPadding(config.backdropEnabled, 6.dp),
            ),
    ) {
        Column(modifier = modifierPositionPadding) { LetterKeyboardRows(drawing) }
    }
}

@Composable
private fun LetterKeyboardRows(drawing: LetterKeyboardDrawing) {
    drawing.keyboard.arr.forEachIndexed { rowIndex, row ->
        Row {
            row.forEachIndexed { columnIndex, key ->
                Column {
                    ScreenKey(
                        ScreenKeyData(
                            key = key,
                            environment = drawing.environment,
                            ghostKey = neighboringKey(drawing, drawing.environment.config.ghostKeysEnabled, rowIndex, columnIndex),
                            oppositeCaseKey = oppositeCaseKey(drawing, rowIndex, columnIndex),
                            numericKey = numericNeighbor(drawing, rowIndex, columnIndex),
                        ),
                    )
                }
            }
        }
    }
}

private fun neighboringKey(
    drawing: LetterKeyboardDrawing,
    enabled: Boolean,
    row: Int,
    column: Int,
): KeyItemC? = if (enabled) keyboardKeyAt(numericKeyboard(drawing), row, column) else null

private fun oppositeCaseKey(
    drawing: LetterKeyboardDrawing,
    row: Int,
    column: Int,
): KeyItemC? {
    val keyboard =
        when (drawing.mode) {
            KeyboardMode.MAIN -> drawing.definition.modes.shifted
            KeyboardMode.SHIFTED -> drawing.definition.modes.main
            else -> null
        }
    return keyboardKeyAt(keyboard, row, column)
}

private fun numericNeighbor(
    drawing: LetterKeyboardDrawing,
    row: Int,
    column: Int,
): KeyItemC? = keyboardKeyAt(numericKeyboard(drawing), row, column)

private fun numericKeyboard(drawing: LetterKeyboardDrawing): KeyboardC? =
    when (drawing.mode) {
        KeyboardMode.MAIN, KeyboardMode.SHIFTED, KeyboardMode.CTRLED, KeyboardMode.ALTED -> drawing.definition.modes.numeric
        else -> null
    }
