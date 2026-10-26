package com.dessalines.thumbkey.ui.components.keyboard

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import com.dessalines.thumbkey.IMEService
import com.dessalines.thumbkey.keyboards.BACKSPACE_KEY_ITEM
import com.dessalines.thumbkey.keyboards.EMOJI_BACK_KEY_ITEM
import com.dessalines.thumbkey.keyboards.NUMERIC_KEY_ITEM
import com.dessalines.thumbkey.keyboards.RETURN_KEY_ITEM
import com.dessalines.thumbkey.utils.KeyAction
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.KeyboardC
import com.dessalines.thumbkey.utils.KeyboardDefinition
import com.dessalines.thumbkey.utils.performKeyAction

private data class EmojiPickContext(
    val ime: IMEService,
    val environment: KeyboardEnvironment,
    val pickerView: View,
    val audioManager: AudioManager,
    val onSwitchLanguage: () -> Unit,
)

private data class StandardEmojiLayout(
    val controllerKeys: List<KeyItemC>,
    val pickerHeight: Dp,
)

@Composable
internal fun EmojiKeyboard(
    ime: IMEService,
    keyboardDefinition: KeyboardDefinition,
    environment: KeyboardEnvironment,
    onSwitchLanguage: () -> Unit,
) {
    val pickerModifier = emojiPickerModifier(environment)
    val pickContext =
        EmojiPickContext(
            ime = ime,
            environment = environment,
            pickerView = LocalView.current,
            audioManager = ime.getSystemService(Context.AUDIO_SERVICE) as AudioManager,
            onSwitchLanguage = onSwitchLanguage,
        )
    val onEmojiPicked = { emoji: String -> handleEmojiPicked(emoji, pickContext) }
    ime.currentInputConnection.requestCursorUpdates(0)

    KeyboardFrame(KeyboardFrameStyle(environment.config.backdropEnabled, MaterialTheme.colorScheme.background)) {
        val emojiLayout = keyboardDefinition.modes.emoji
        if (emojiLayout != null) {
            CustomEmojiKeyboard(emojiLayout, environment, pickerModifier, onEmojiPicked)
        } else {
            StandardEmojiKeyboard(standardEmojiLayout(keyboardDefinition, environment), environment, pickerModifier, onEmojiPicked)
        }
    }
}

private fun handleEmojiPicked(
    emoji: String,
    context: EmojiPickContext,
) {
    val config = context.environment.config
    if (config.vibrateOnTap) context.pickerView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    if (config.soundOnTap) context.audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, .1f)
    performKeyAction(
        action = KeyAction.CommitText(emoji),
        ime = context.ime,
        autoCapitalize = config.autoCapitalize,
        keyboardSettings = context.environment.keyboardSettings,
        onToggleShiftMode = context.environment.callbacks.onToggleShiftMode,
        onToggleCtrlMode = context.environment.callbacks.onToggleCtrlMode,
        onToggleAltMode = context.environment.callbacks.onToggleAltMode,
        onToggleNumericMode = context.environment.callbacks.onToggleNumericMode,
        onToggleEmojiMode = context.environment.callbacks.onToggleEmojiMode,
        onToggleCapsLock = context.environment.callbacks.onToggleCapsLockFromEmojiPicker,
        onAutoCapitalize = context.environment.callbacks.onAutoCapitalize,
        onToggleClipboardMode = context.environment.callbacks.onToggleClipboardMode,
        onToggleTranscriptHistoryMode = context.environment.callbacks.onToggleTranscriptHistoryMode,
        onToggleSketchMode = context.environment.callbacks.onToggleSketchMode,
        onToggleHideLetters = context.environment.callbacks.onToggleHideLetters,
        onSwitchLanguage = context.onSwitchLanguage,
        onChangePosition = context.environment.callbacks.onChangePosition,
        onKeyEvent = {},
        abbreviationBufferEnabled = config.abbreviationBufferEnabled,
    )
}

@Composable
private fun emojiPickerModifier(environment: KeyboardEnvironment) =
    emojiPickerBoxModifier(
        EmojiPickerBoxOptions(
            keyPadding = environment.config.keyPadding,
            cornerRadius = environment.keyRadius,
            borderWidth = environment.keyBorderWidth,
            borderColor = MaterialTheme.colorScheme.outline,
        ),
    )

private fun standardEmojiLayout(
    keyboardDefinition: KeyboardDefinition,
    environment: KeyboardEnvironment,
): StandardEmojiLayout {
    val rowCount = keyboardDefinition.modes.main.arr.size
    val controllerKeys =
        if (rowCount <= 3) {
            listOf(EMOJI_BACK_KEY_ITEM, BACKSPACE_KEY_ITEM, RETURN_KEY_ITEM)
        } else {
            listOf(EMOJI_BACK_KEY_ITEM, NUMERIC_KEY_ITEM, BACKSPACE_KEY_ITEM, RETURN_KEY_ITEM)
        }
    val pickerHeight = Dp((environment.keyHeight * controllerKeys.size) - (environment.config.keyPadding * 2))
    return StandardEmojiLayout(controllerKeys, pickerHeight)
}

@Composable
private fun CustomEmojiKeyboard(
    emojiLayout: KeyboardC,
    environment: KeyboardEnvironment,
    pickerModifier: Modifier,
    onEmojiPicked: (String) -> Unit,
) {
    val config = environment.config
    Column(
        modifier =
            Modifier
                .keyboardBottomInset(config.ignoreBottomPadding, config.pushupSize.dp)
                .fillMaxWidth()
                .keyboardBackdropPadding(config.backdropEnabled, 6.dp),
    ) {
        val pickerHeight = Dp((environment.keyHeight * (emojiLayout.arr.size - 1)) - (config.keyPadding * 2))
        EmojiPickerBox(pickerModifier, pickerHeight, onEmojiPicked)
        Row {
            emojiLayout.arr.last().forEach { key ->
                Column { ScreenKey(ScreenKeyData(key, environment)) }
            }
        }
    }
}

@Composable
private fun StandardEmojiKeyboard(
    layout: StandardEmojiLayout,
    environment: KeyboardEnvironment,
    pickerModifier: Modifier,
    onEmojiPicked: (String) -> Unit,
) {
    val config = environment.config
    Row(
        modifier =
            Modifier
                .keyboardBottomInset(config.ignoreBottomPadding, config.pushupSize.dp)
                .fillMaxWidth()
                .keyboardBackdropPadding(config.backdropEnabled, 6.dp),
    ) {
        Box(modifier = Modifier.weight(1f).then(pickerModifier)) {
            AndroidView(
                factory = { context -> createEmojiPicker(context, onEmojiPicked) },
                modifier = Modifier.fillMaxWidth().height(layout.pickerHeight),
            )
        }
        Column {
            layout.controllerKeys.forEach { key ->
                Column { ScreenKey(ScreenKeyData(key, environment)) }
            }
        }
    }
}

@Composable
private fun EmojiPickerBox(
    pickerModifier: Modifier,
    pickerHeight: Dp,
    onEmojiPicked: (String) -> Unit,
) {
    Box(modifier = pickerModifier) {
        AndroidView(
            factory = { context -> createEmojiPicker(context, onEmojiPicked) },
            modifier = Modifier.fillMaxWidth().height(pickerHeight),
        )
    }
}

private fun createEmojiPicker(
    context: Context,
    onEmojiPicked: (String) -> Unit,
): EmojiPickerView =
    EmojiPickerView(context).apply {
        setOnEmojiPickedListener { onEmojiPicked(it.emoji) }
    }
