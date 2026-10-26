package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.db.ClipboardRepository
import com.dessalines.thumbkey.summera.DictationState
import com.dessalines.thumbkey.summera.SummeraDictation
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.KeyboardMode
import com.dessalines.thumbkey.utils.KeyboardPosition
import com.dessalines.thumbkey.utils.getKeyboardMode
import com.dessalines.thumbkey.utils.resolveKeyboardDefinition
import kotlin.time.TimeMark

fun interface KeyboardPositionChange {
    fun apply(old: KeyboardPosition): KeyboardPosition
}

interface KeyboardScreenActions {
    fun onSwitchLanguage()

    fun onChangePosition(change: KeyboardPositionChange)

    fun onToggleHideLetters()

    fun onGoToClipboardSettings()
}

@Composable
fun KeyboardScreen(data: KeyboardScreenInput) {
    val ime = LocalContext.current as IMEService
    val setup = rememberKeyboardScreenSetup(data, ime)
    val clipboardItems by data.clipboardRepository.allClipboardItems.observeAsState(initial = emptyList())
    val transcripts by data.clipboardRepository.allTranscripts.observeAsState(initial = emptyList())

    KeyboardScreenContent(
        data =
            KeyboardScreenContentData(
                ime = ime,
                clipboardRepository = data.clipboardRepository,
                keyboardDefinition = setup.keyboardDefinition,
                environment = setup.environment,
                modeState = setup.modeState,
                sketch = setup.sketch,
                clipboardItems = clipboardItems,
                transcripts = transcripts,
                onModeStateChange = setup.onModeStateChange,
            ),
        onSwitchLanguage = data.actions::onSwitchLanguage,
        onGoToClipboardSettings = data.actions::onGoToClipboardSettings,
    )
}

data class KeyboardScreenInput(
    val settings: AppSettings?,
    val clipboardRepository: ClipboardRepository,
    val actions: KeyboardScreenActions,
)

private data class KeyboardScreenSetup(
    val keyboardDefinition: KeyboardDefinition,
    val modeState: KeyboardModeState,
    val sketch: SketchState,
    val environment: KeyboardEnvironment,
    val onModeStateChange: (KeyboardModeState) -> Unit,
)

@Composable
private fun rememberKeyboardScreenSetup(
    data: KeyboardScreenInput,
    ime: IMEService,
): KeyboardScreenSetup {
    val config = KeyboardScreenConfig.from(data.settings ?: AppSettings(id = 1), ime)
    val keyboardDefinition = resolveKeyboardDefinition(config.layout, config.keyMods)
    var modeState by remember {
        mutableStateOf(
            KeyboardModeState(
                mode =
                    getKeyboardMode(
                        ime = ime,
                        autoCapitalize = data.settings != null && config.autoCapitalize && keyboardDefinition.settings.autoShift,
                    ),
            ),
        )
    }
    val sketch = ime.sketchState
    LaunchedEffect(modeState.mode) {
        val typingSearch = sketch.searching && modeState.mode != KeyboardMode.EMOJI && modeState.mode != KeyboardMode.CLIPBOARD
        if (modeState.mode != KeyboardMode.SKETCH && !typingSearch) {
            sketch.searching = false
            sketch.list = null
        }
    }

    val lastAction = remember { mutableStateOf<Pair<KeyAction, TimeMark>?>(null) }
    val environment =
        keyboardEnvironment(
            KeyboardEnvironmentInput(
                config = config,
                keyboardDefinition = keyboardDefinition,
                modeState = modeState,
                lastAction = lastAction,
                onModeStateChange = { modeState = it },
                sketchSearching = sketch.searching,
                onSwitchLanguage = data.actions::onSwitchLanguage,
                onChangePosition = { change ->
                    data.actions.onChangePosition(KeyboardPositionChange { old -> change(old) })
                },
                onToggleHideLetters = data.actions::onToggleHideLetters,
            ),
        )
    return KeyboardScreenSetup(
        keyboardDefinition = keyboardDefinition,
        modeState = modeState,
        sketch = sketch,
        environment = environment,
        onModeStateChange = { modeState = it },
    )
}

private data class KeyboardScreenContentData(
    val ime: IMEService,
    val clipboardRepository: ClipboardRepository,
    val keyboardDefinition: KeyboardDefinition,
    val environment: KeyboardEnvironment,
    val modeState: KeyboardModeState,
    val sketch: SketchState,
    val clipboardItems: List<com.dessalines.thumbkey.db.ClipboardItem>,
    val transcripts: List<com.dessalines.thumbkey.db.ClipboardItem>,
    val onModeStateChange: (KeyboardModeState) -> Unit,
)

private data class KeyboardScreenModeContentData(
    val screen: KeyboardScreenContentData,
    val dictationState: DictationState,
    val dictationActive: Boolean,
    val onSwitchLanguage: () -> Unit,
    val onGoToClipboardSettings: () -> Unit,
)

@Composable
private fun KeyboardScreenContent(
    data: KeyboardScreenContentData,
    onSwitchLanguage: () -> Unit,
    onGoToClipboardSettings: () -> Unit,
) {
    val dictationState = SummeraDictation.state
    val dictationActive =
        dictationState != DictationState.Idle &&
            !(dictationState is DictationState.Done && data.ime.currentInputEditorInfo.packageName == data.ime.packageName)
    KeyboardScreenModeContent(
        KeyboardScreenModeContentData(
            screen = data,
            dictationState = dictationState,
            dictationActive = dictationActive,
            onSwitchLanguage = onSwitchLanguage,
            onGoToClipboardSettings = onGoToClipboardSettings,
        ),
    )
}

@Composable
private fun KeyboardScreenModeContent(input: KeyboardScreenModeContentData) {
    val data = input.screen
    val mode = data.modeState.mode

    when {
        mode == KeyboardMode.EMOJI && !input.dictationActive -> {
            EmojiKeyboard(data.ime, data.keyboardDefinition, data.environment, input.onSwitchLanguage)
        }

        input.dictationActive || mode == KeyboardMode.CLIPBOARD || mode == KeyboardMode.TRANSCRIPTS -> {
            ClipboardKeyboardForMode(input)
        }

        mode == KeyboardMode.SKETCH -> {
            SketchKeyboard(
                SketchKeyboardData(
                    ime = data.ime,
                    state = data.sketch,
                    rowCount = data.keyboardDefinition.modes.main.arr.size,
                    modeState = data.modeState,
                    onModeStateChange = data.onModeStateChange,
                ),
                data.environment,
            )
        }

        else -> {
            LetterKeyboard(
                LetterKeyboardData(
                    ime = data.ime,
                    keyboardDefinition = data.keyboardDefinition,
                    modeState = data.modeState,
                    sketch = data.sketch,
                    onModeStateChange = data.onModeStateChange,
                ),
                data.environment,
            )
        }
    }
}

@Composable
private fun ClipboardKeyboardForMode(input: KeyboardScreenModeContentData) {
    val data = input.screen
    ClipboardKeyboard(
        data =
            ClipboardScreenData(
                ime = data.ime,
                repository = data.clipboardRepository,
                mode = data.modeState.mode,
                dictationState = input.dictationState,
                dictationActive = input.dictationActive,
                clipboardItems = data.clipboardItems,
                transcripts = data.transcripts,
                keyboardRowCount = data.keyboardDefinition.modes.main.arr.size,
                onReturnToKeyboard = { data.onModeStateChange(data.modeState.copy(mode = KeyboardMode.MAIN)) },
            ),
        environment = data.environment,
        onGoToClipboardSettings = input.onGoToClipboardSettings,
    )
}
