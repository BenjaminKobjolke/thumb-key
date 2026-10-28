@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.dessalines.thumbkey.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import arrow.optics.*
import com.charleskorn.kaml.Yaml
import com.dessalines.thumbkey.textprocessors.ComposeComboProcessor
import com.dessalines.thumbkey.utils.KeyAction.CommitText
import com.dessalines.thumbkey.utils.KeyAction.Noop
import com.dessalines.thumbkey.utils.KeyAction.StartComposeCombo
import com.dessalines.thumbkey.utils.KeyDisplay.TextDisplay
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

/**
 * Shared by every layout whose compose key comes from key modifications. Modified definitions
 * are rebuilt on each recomposition, so a fresh processor per build would lose a sequence
 * that is still being typed.
 */
private val modificationsComposeComboProcessor = ComposeComboProcessor()

/**
 * The definition the keyboard should use for [keyboardLayout]: the modified one if
 * [keyModifications] has entries for it and they apply cleanly, else the built-in one.
 */
fun resolveKeyboardDefinition(
    keyboardLayout: KeyboardLayout,
    keyModifications: String?,
): KeyboardDefinition =
    keyModifications
        ?.takeIf { it.isNotEmpty() }
        ?.let { getModifiedKeyboardDefinition(keyboardLayout, it) }
        ?: keyboardLayout.keyboardDefinition

/**
 * @param keyboardLayout The layout of the keyboard to be modified.
 * @param keyModifications The key modifications YAML string.
 * @return A modified `KeyboardDefinition` if modifications exist for the layout, else null.
 */
fun getModifiedKeyboardDefinition(
    keyboardLayout: KeyboardLayout,
    keyModifications: String,
): KeyboardDefinition? =
    try {
        val keyMods = deserializeKeyModifications(keyModifications)
        keyMods[keyboardLayout.name]?.let {
            val modifiedKeyboardDefinition = modifyKeyboardDefinition(keyboardLayout, it)
            Log.d(TAG, "key modifications applied to layout ${keyboardLayout.name}")
            modifiedKeyboardDefinition
        }
    } catch (e: Exception) {
        val errorMessage = e.message ?: e.stackTraceToString()
        Log.d(TAG, "Error applying key modifications: $errorMessage")
        null
    }

fun checkAllKeyboardModifications(
    keyModifications: String,
    keyModificationsErrorState: MutableState<String?>,
) {
    keyModificationsErrorState.value = null
    try {
        val keyModifications = deserializeKeyModifications(keyModifications)
        keyModifications.forEach {
            val keyboardLayout = KeyboardLayout.entries.find { layout -> it.key == layout.name }
            if (keyboardLayout == null) {
                // This should never happen
                keyModificationsErrorState.value = "Keyboard layout '${it.key}' not found."
                return
            }

            modifyKeyboardDefinition(keyboardLayout, it.value)
        }
    } catch (e: Exception) {
        val errorMessage = e.message ?: e.stackTraceToString()
        keyModificationsErrorState.value = errorMessage
        Log.d(TAG, "Error applying key modifications: $errorMessage")
    }
}

/**
 * @param keyModifications The YAML string representation of key modifications.
 * @return A map of keyboard definitions.
 * @throws kotlinx.serialization.SerializationException - in case of any decoding-specific error.
 * @throws IllegalArgumentException - if the decoded input is not a valid instance of [KeyModifications].
 */
fun deserializeKeyModifications(keyModifications: String): KeyModifications {
    val serializer =
        MapSerializer(String.serializer(), KeyboardDefinitionModesSerializable.serializer())
    return getYaml().decodeFromString(serializer, keyModifications)
}

fun modifyKeyboardDefinition(
    layout: KeyboardLayout,
    modifications: KeyboardDefinitionModesSerializable,
): KeyboardDefinition {
    val originalKeyboardDefinition = layout.keyboardDefinition

    val mainShiftedSame = originalKeyboardDefinition.modes.main == originalKeyboardDefinition.modes.shifted
    val mainModifications = if (!mainShiftedSame) modifications.main else modifications.main ?: modifications.shifted
    val shiftedModifications = if (!mainShiftedSame) modifications.shifted else modifications.shifted ?: modifications.main

    val modifiedKeyboardDefinition =
        originalKeyboardDefinition.copy {
            inside(KeyboardDefinition.modes) {
                KeyboardDefinitionModes.main transform { it.withModifications(mainModifications) }
                KeyboardDefinitionModes.shifted transform { it.withModifications(shiftedModifications) }
                KeyboardDefinitionModes.numeric transform { it.withModifications(modifications.numeric) }
                KeyboardDefinitionModes.ctrled transform { it?.withModifications(modifications.ctrled) }
                KeyboardDefinitionModes.alted transform { it?.withModifications(modifications.alted) }
            }
        }

    return attachComposeComboProcessor(layout, modifiedKeyboardDefinition)
}

private fun KeyboardC.withModifications(modifications: KeyboardCSerializable?): KeyboardC = modifyKeyboardC(this, modifications) ?: this

/**
 * A compose key only arms a [ComposeComboProcessor], which then reads the keystrokes that follow.
 * If the modifications put a compose key on a layout without a text processor, attach one.
 */
fun attachComposeComboProcessor(
    layout: KeyboardLayout,
    keyboardDefinition: KeyboardDefinition,
): KeyboardDefinition {
    val textProcessor = keyboardDefinition.settings.textProcessor
    if (textProcessor is ComposeComboProcessor || !usesComposeCombo(keyboardDefinition)) {
        return keyboardDefinition
    }
    if (textProcessor != null) {
        throw IllegalArgumentException(
            "`StartComposeCombo` cannot be used on ${layout.name}, which has its own text processor.",
        )
    }
    return keyboardDefinition.copy(
        settings = keyboardDefinition.settings.copy(textProcessor = modificationsComposeComboProcessor),
    )
}

private fun usesComposeCombo(keyboardDefinition: KeyboardDefinition): Boolean =
    with(keyboardDefinition.modes) { listOfNotNull(main, shifted, numeric, ctrled, alted, emoji) }
        .flatMap { it.arr.flatten() }
        .any { keyItemC ->
            val keyCs =
                with(keyItemC) {
                    listOfNotNull(center, left, topLeft, top, topRight, right, bottomRight, bottom, bottomLeft)
                }
            keyItemC.longPress == StartComposeCombo ||
                keyCs.any { it.action == StartComposeCombo || it.swipeReturnAction == StartComposeCombo }
        }

fun modifyKeyboardC(
    keyboardC: KeyboardC?,
    keyboardCSerializable: KeyboardCSerializable?,
): KeyboardC? {
    if (keyboardC == null || keyboardCSerializable == null) return null

    return keyboardC.copy {
        KeyboardC.arr transform {
            it.mapIndexed { i, row ->
                row.mapIndexed { j, keyItemC ->
                    val propertyName = "key${i}_$j"
                    val property =
                        KeyboardCSerializable::class.members.find { it.name == propertyName }
                    val keyItemCSerializable =
                        property?.call(keyboardCSerializable) as? KeyItemCSerializable

                    keyItemC.copy {
                        keyItemCSerializable?.let { serializable ->
                            modifyKeyItemC(serializable)
                        }
                    }
                }
            }
        }
    }
}

fun Copy<KeyItemC>.modifyKeyItemC(serializable: KeyItemCSerializable) {
    KeyItemC.topLeft transform { modifyKeyC(it, serializable.topLeft) }
    KeyItemC.top transform { modifyKeyC(it, serializable.top) }
    KeyItemC.topRight transform { modifyKeyC(it, serializable.topRight) }
    KeyItemC.left transform { modifyKeyC(it, serializable.left) }
    KeyItemC.center transform { modifyKeyC(it, serializable.center) ?: KeyC(action = Noop) }
    KeyItemC.right transform { modifyKeyC(it, serializable.right) }
    KeyItemC.bottomLeft transform { modifyKeyC(it, serializable.bottomLeft) }
    KeyItemC.bottom transform { modifyKeyC(it, serializable.bottom) }
    KeyItemC.bottomRight transform { modifyKeyC(it, serializable.bottomRight) }

    KeyItemC.longPress transform { modifyLongPress(it, serializable.longPress) }

    KeyItemC.widthMultiplier transform { serializable.widthMultiplier ?: it }
    KeyItemC.backgroundColor transform { serializable.backgroundColor ?: it }
    KeyItemC.swipeType transform { serializable.swipeType ?: it }
    KeyItemC.slideType transform { serializable.slideType ?: it }
}

fun modifyKeyC(
    keyC: KeyC?,
    keyCSerializable: KeyCSerializable?,
): KeyC? {
    if (keyCSerializable == null) {
        return keyC
    }

    val keyActionKeyC = getCommonKeyCFromKeyAction(keyCSerializable.keyAction)
    val swipeReturnActionKeyC = getCommonKeyCFromKeyAction(keyCSerializable.swipeReturnAction)
    checkTextAndKeyActionValidity(keyActionKeyC, swipeReturnActionKeyC, keyCSerializable)
    if (keyCSerializable.remove) {
        return null
    }

    return (keyC ?: KeyC(action = Noop)).copy {
        keyCSerializable.text?.let {
            KeyC.action.set(CommitText(it))
            KeyC.display.set(TextDisplay(it))
            KeyC.capsModeDisplay.set(null)
        }

        keyActionKeyC?.let {
            KeyC.action.set(it.action)
            KeyC.swipeReturnAction.set(it.swipeReturnAction)
            KeyC.display.set(it.display)
            KeyC.capsModeDisplay.set(it.capsModeDisplay)
        }

        keyCSerializable.swipeReturnText?.let {
            KeyC.swipeReturnAction.set(CommitText(it))
        }

        swipeReturnActionKeyC?.let {
            KeyC.swipeReturnAction.set(it.action)
        }

        keyCSerializable.displayText?.let {
            KeyC.display.set(TextDisplay(it))
            KeyC.capsModeDisplay.set(null)
        }
        keyCSerializable.color?.let {
            KeyC.color.set(it)
        }
        keyCSerializable.size?.let {
            KeyC.size.set(it)
        }
    }
}

fun modifyLongPress(
    keyAction: KeyAction?,
    keyCSerializable: KeyCSerializable?,
): KeyAction? {
    if (keyCSerializable == null) {
        return keyAction
    }

    val keyActionKeyC = getCommonKeyCFromKeyAction(keyCSerializable.keyAction)
    val swipeReturnActionKeyC = getCommonKeyCFromKeyAction(keyCSerializable.swipeReturnAction)
    checkTextAndKeyActionValidity(keyActionKeyC, swipeReturnActionKeyC, keyCSerializable)

    if (keyCSerializable.remove) {
        return null
    }

    keyCSerializable.text?.let {
        return CommitText(it)
    }

    keyActionKeyC?.let {
        return it.action
    }

    return keyAction
}
