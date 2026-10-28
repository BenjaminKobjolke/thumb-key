package com.dessalines.thumbkey.utils

import androidx.annotation.Keep
import com.charleskorn.kaml.AnchorsAndAliases
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.dessalines.thumbkey.keyboards.BACKSPACE_KEYC
import com.dessalines.thumbkey.keyboards.BACKSPACE_TEXT_MANIPULATION_KEYC
import com.dessalines.thumbkey.keyboards.COMPOSE_COMBO_KEYC
import com.dessalines.thumbkey.keyboards.COPY_KEYC
import com.dessalines.thumbkey.keyboards.CUT_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_CHARACTER_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_WORD_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.DELETE_WORD_BEFORE_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.GOTO_SETTINGS_KEYC
import com.dessalines.thumbkey.keyboards.HIDE_KEYBOARD_KEYC
import com.dessalines.thumbkey.keyboards.NEXT_WORD_AFTER_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.NOOP_KEYC
import com.dessalines.thumbkey.keyboards.PASTE_KEYC
import com.dessalines.thumbkey.keyboards.PREVIOUS_WORD_BEFORE_CURSOR_KEYC
import com.dessalines.thumbkey.keyboards.REDO_KEYC
import com.dessalines.thumbkey.keyboards.RETURN_KEYC
import com.dessalines.thumbkey.keyboards.SELECT_ALL_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_LEFT_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_PROGRAMMING_BOTTOM_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_PROGRAMMING_TOP_KEYC
import com.dessalines.thumbkey.keyboards.SPACEBAR_RIGHT_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_IME_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_IME_VOICE_KEYC
import com.dessalines.thumbkey.keyboards.SWITCH_LANGUAGE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_ALT_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_ALT_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CAPS_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CLIPBOARD_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CTRL_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_CTRL_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_EMOJI_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_NUMERIC_MODE_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_NUMERIC_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SHIFT_FALSE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SHIFT_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.TOGGLE_SKETCH_MODE_TRUE_KEYC
import com.dessalines.thumbkey.keyboards.UNDO_KEYC
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

fun getYaml(): Yaml =
    Yaml(
        configuration =
            YamlConfiguration(
                encodeDefaults = false,
                anchorsAndAliases = AnchorsAndAliases.Permitted(),
                extensionDefinitionPrefix = "x-",
            ),
    )

fun checkTextAndKeyActionValidity(
    keyActionKeyC: KeyC?,
    swipeReturnActionKeyC: KeyC?,
    keyCSerializable: KeyCSerializable,
) {
    if (keyActionKeyC != null && keyCSerializable.text != null) {
        throw IllegalArgumentException(
            "Properties `text` and `keyAction` cannot both be used:\n${
                getYaml().encodeToString(KeyCSerializable.serializer(), keyCSerializable)
            }",
        )
    }
    if (swipeReturnActionKeyC != null && keyCSerializable.swipeReturnText != null) {
        throw IllegalArgumentException(
            "Properties `swipeReturnText` and `swipeReturnAction` cannot both be used:\n${
                getYaml().encodeToString(KeyCSerializable.serializer(), keyCSerializable)
            }",
        )
    }
}

fun getCommonKeyCFromKeyAction(keyActionSerializable: KeyActionSerializable?): KeyC? = keyActionSerializable?.commonKeyC

typealias KeyModifications = Map<String, KeyboardDefinitionModesSerializable>

@Serializable
@Keep
data class KeyboardDefinitionModesSerializable(
    val main: KeyboardCSerializable? = null,
    val shifted: KeyboardCSerializable? = null,
    val numeric: KeyboardCSerializable? = null,
    val ctrled: KeyboardCSerializable? = null,
    val alted: KeyboardCSerializable? = null,
    val emoji: KeyboardCSerializable? = null,
)

@Serializable
@Suppress("PropertyName")
@Keep
data class KeyboardCSerializable(
    val key0_0: KeyItemCSerializable? = null,
    val key0_1: KeyItemCSerializable? = null,
    val key0_2: KeyItemCSerializable? = null,
    val key0_3: KeyItemCSerializable? = null,
    val key0_4: KeyItemCSerializable? = null,
    val key0_5: KeyItemCSerializable? = null,
    val key0_6: KeyItemCSerializable? = null,
    val key0_7: KeyItemCSerializable? = null,
    val key0_8: KeyItemCSerializable? = null,
    val key0_9: KeyItemCSerializable? = null,
    val key1_0: KeyItemCSerializable? = null,
    val key1_1: KeyItemCSerializable? = null,
    val key1_2: KeyItemCSerializable? = null,
    val key1_3: KeyItemCSerializable? = null,
    val key1_4: KeyItemCSerializable? = null,
    val key1_5: KeyItemCSerializable? = null,
    val key1_6: KeyItemCSerializable? = null,
    val key1_7: KeyItemCSerializable? = null,
    val key1_8: KeyItemCSerializable? = null,
    val key1_9: KeyItemCSerializable? = null,
    val key2_0: KeyItemCSerializable? = null,
    val key2_1: KeyItemCSerializable? = null,
    val key2_2: KeyItemCSerializable? = null,
    val key2_3: KeyItemCSerializable? = null,
    val key2_4: KeyItemCSerializable? = null,
    val key2_5: KeyItemCSerializable? = null,
    val key2_6: KeyItemCSerializable? = null,
    val key2_7: KeyItemCSerializable? = null,
    val key2_8: KeyItemCSerializable? = null,
    val key2_9: KeyItemCSerializable? = null,
    val key3_0: KeyItemCSerializable? = null,
    val key3_1: KeyItemCSerializable? = null,
    val key3_2: KeyItemCSerializable? = null,
    val key3_3: KeyItemCSerializable? = null,
    val key3_4: KeyItemCSerializable? = null,
    val key3_5: KeyItemCSerializable? = null,
    val key3_6: KeyItemCSerializable? = null,
    val key3_7: KeyItemCSerializable? = null,
    val key3_8: KeyItemCSerializable? = null,
    val key3_9: KeyItemCSerializable? = null,
    val key4_0: KeyItemCSerializable? = null,
    val key4_1: KeyItemCSerializable? = null,
    val key4_2: KeyItemCSerializable? = null,
    val key4_3: KeyItemCSerializable? = null,
    val key4_4: KeyItemCSerializable? = null,
    val key4_5: KeyItemCSerializable? = null,
    val key4_6: KeyItemCSerializable? = null,
    val key4_7: KeyItemCSerializable? = null,
    val key4_8: KeyItemCSerializable? = null,
    val key4_9: KeyItemCSerializable? = null,
    val key5_0: KeyItemCSerializable? = null,
    val key5_1: KeyItemCSerializable? = null,
    val key5_2: KeyItemCSerializable? = null,
    val key5_3: KeyItemCSerializable? = null,
    val key5_4: KeyItemCSerializable? = null,
    val key5_5: KeyItemCSerializable? = null,
    val key5_6: KeyItemCSerializable? = null,
    val key5_7: KeyItemCSerializable? = null,
    val key5_8: KeyItemCSerializable? = null,
    val key5_9: KeyItemCSerializable? = null,
    val key6_0: KeyItemCSerializable? = null,
    val key6_1: KeyItemCSerializable? = null,
    val key6_2: KeyItemCSerializable? = null,
    val key6_3: KeyItemCSerializable? = null,
    val key6_4: KeyItemCSerializable? = null,
    val key6_5: KeyItemCSerializable? = null,
    val key6_6: KeyItemCSerializable? = null,
    val key6_7: KeyItemCSerializable? = null,
    val key6_8: KeyItemCSerializable? = null,
    val key6_9: KeyItemCSerializable? = null,
    val key7_0: KeyItemCSerializable? = null,
    val key7_1: KeyItemCSerializable? = null,
    val key7_2: KeyItemCSerializable? = null,
    val key7_3: KeyItemCSerializable? = null,
    val key7_4: KeyItemCSerializable? = null,
    val key7_5: KeyItemCSerializable? = null,
    val key7_6: KeyItemCSerializable? = null,
    val key7_7: KeyItemCSerializable? = null,
    val key7_8: KeyItemCSerializable? = null,
    val key7_9: KeyItemCSerializable? = null,
    val key8_0: KeyItemCSerializable? = null,
    val key8_1: KeyItemCSerializable? = null,
    val key8_2: KeyItemCSerializable? = null,
    val key8_3: KeyItemCSerializable? = null,
    val key8_4: KeyItemCSerializable? = null,
    val key8_5: KeyItemCSerializable? = null,
    val key8_6: KeyItemCSerializable? = null,
    val key8_7: KeyItemCSerializable? = null,
    val key8_8: KeyItemCSerializable? = null,
    val key8_9: KeyItemCSerializable? = null,
    val key9_0: KeyItemCSerializable? = null,
    val key9_1: KeyItemCSerializable? = null,
    val key9_2: KeyItemCSerializable? = null,
    val key9_3: KeyItemCSerializable? = null,
    val key9_4: KeyItemCSerializable? = null,
    val key9_5: KeyItemCSerializable? = null,
    val key9_6: KeyItemCSerializable? = null,
    val key9_7: KeyItemCSerializable? = null,
    val key9_8: KeyItemCSerializable? = null,
    val key9_9: KeyItemCSerializable? = null,
)

@Serializable
@Keep
data class KeyItemCSerializable(
    val topLeft: KeyCSerializable? = null,
    val top: KeyCSerializable? = null,
    val topRight: KeyCSerializable? = null,
    val left: KeyCSerializable? = null,
    val center: KeyCSerializable? = null,
    val right: KeyCSerializable? = null,
    val bottomLeft: KeyCSerializable? = null,
    val bottom: KeyCSerializable? = null,
    val bottomRight: KeyCSerializable? = null,
    var longPress: KeyCSerializable? = null,
    var widthMultiplier: Int? = null,
    var backgroundColor: ColorVariant? = null,
    var swipeType: SwipeNWay? = null,
    var slideType: SlideType? = null,
)

@Serializable
@Keep
data class KeyCSerializable(
    val text: String? = null,
    val displayText: String? = null,
    val size: FontSizeVariant? = null,
    val color: ColorVariant? = null,
    val remove: Boolean = false,
    val keyAction: KeyActionSerializable? = null,
    val swipeReturnText: String? = null,
    val swipeReturnAction: KeyActionSerializable? = null,
)

@Keep
enum class KeyActionSerializable(
    val commonKeyC: KeyC,
) {
    ToggleNumericMode(TOGGLE_NUMERIC_MODE_TRUE_KEYC),
    ToggleABCMode(TOGGLE_NUMERIC_MODE_FALSE_KEYC),
    ToggleEmojiMode(TOGGLE_EMOJI_MODE_TRUE_KEYC),
    ToggleClipboardMode(TOGGLE_CLIPBOARD_MODE_TRUE_KEYC),
    ToggleSketchMode(TOGGLE_SKETCH_MODE_TRUE_KEYC),
    ToggleCapsLock(TOGGLE_CAPS_KEYC),
    ToggleShiftModeFalse(TOGGLE_SHIFT_FALSE_KEYC),
    ToggleShiftModeTrue(TOGGLE_SHIFT_TRUE_KEYC),
    ToggleCtrlModeTrue(TOGGLE_CTRL_TRUE_KEYC),
    ToggleCtrlModeFalse(TOGGLE_CTRL_FALSE_KEYC),
    ToggleAltModeTrue(TOGGLE_ALT_TRUE_KEYC),
    ToggleAltModeFalse(TOGGLE_ALT_FALSE_KEYC),
    Left(SPACEBAR_LEFT_KEYC),
    Right(SPACEBAR_RIGHT_KEYC),
    Top(SPACEBAR_PROGRAMMING_TOP_KEYC),
    Bottom(SPACEBAR_PROGRAMMING_BOTTOM_KEYC),
    GotoSettings(GOTO_SETTINGS_KEYC),
    SelectAll(SELECT_ALL_KEYC),
    Cut(CUT_KEYC),
    Copy(COPY_KEYC),
    Paste(PASTE_KEYC),
    Undo(UNDO_KEYC),
    Redo(REDO_KEYC),
    SwitchLanguage(SWITCH_LANGUAGE_KEYC),
    SwitchIME(SWITCH_IME_KEYC),
    SwitchIMEVoice(SWITCH_IME_VOICE_KEYC),
    HideKeyboard(HIDE_KEYBOARD_KEYC),
    IMEComplete(RETURN_KEYC),
    PreviousWordBeforeCursor(PREVIOUS_WORD_BEFORE_CURSOR_KEYC),
    NextWordAfterCursor(NEXT_WORD_AFTER_CURSOR_KEYC),
    Delete(BACKSPACE_KEYC),
    DeleteCharacterAfterCursor(DELETE_CHARACTER_AFTER_CURSOR_KEYC),
    DeleteViaTextManipulation(BACKSPACE_TEXT_MANIPULATION_KEYC),
    DeleteWordBeforeCursor(DELETE_WORD_BEFORE_CURSOR_KEYC),
    DeleteWordAfterCursor(DELETE_WORD_AFTER_CURSOR_KEYC),
    StartComposeCombo(COMPOSE_COMBO_KEYC),
    Noop(NOOP_KEYC),
}
