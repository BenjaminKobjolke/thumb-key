package com.dessalines.thumbkey.utils

import androidx.compose.ui.text.font.FontFamily
import arrow.optics.optics
import com.dessalines.thumbkey.textprocessors.TextProcessor

@optics
data class KeyboardDefinitionModes(
    val main: KeyboardC,
    val shifted: KeyboardC,
    val numeric: KeyboardC,
    val ctrled: KeyboardC? = null,
    val alted: KeyboardC? = null,
    val emoji: KeyboardC? = null,
) {
    companion object
}

@optics
data class KeyboardDefinitionSettings(
    val autoCapitalizers: AutoCapitalizers = arrayOf(),
    val autoShift: Boolean = true,
    val textProcessor: TextProcessor? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KeyboardDefinitionSettings

        return autoCapitalizers.contentEquals(other.autoCapitalizers)
    }

    override fun hashCode(): Int = autoCapitalizers.contentHashCode()

    companion object
}

@optics
data class KeyboardDefinition(
    val title: String,
    val modes: KeyboardDefinitionModes,
    val settings: KeyboardDefinitionSettings = KeyboardDefinitionSettings(),
) {
    companion object
}

@optics
// Almost a 4x4 grid, but the bottom is mostly spacebar
data class KeyboardC(
    val arr: List<List<KeyItemC>>,
) {
    companion object
}

@optics
data class KeyItemC(
    val center: KeyC,
    val left: KeyC? = null,
    val topLeft: KeyC? = null,
    val top: KeyC? = null,
    val topRight: KeyC? = null,
    val right: KeyC? = null,
    val bottomRight: KeyC? = null,
    val bottom: KeyC? = null,
    val bottomLeft: KeyC? = null,
    val nextTapActions: List<KeyAction>? = null,
    val widthMultiplier: Int = 1,
    val backgroundColor: ColorVariant = ColorVariant.SURFACE,
    val swipeType: SwipeNWay = SwipeNWay.EIGHT_WAY,
    val slideType: SlideType = SlideType.NONE,
    val longPress: KeyAction? = null,
) {
    companion object

    fun getSwipe(dir: SwipeDirection?) =
        when (dir) {
            null -> null
            SwipeDirection.LEFT -> left
            SwipeDirection.TOP_LEFT -> topLeft
            SwipeDirection.TOP -> top
            SwipeDirection.TOP_RIGHT -> topRight
            SwipeDirection.RIGHT -> right
            SwipeDirection.BOTTOM_RIGHT -> bottomRight
            SwipeDirection.BOTTOM -> bottom
            SwipeDirection.BOTTOM_LEFT -> bottomLeft
        }
}

fun KeyboardC.alterKey(
    row: Int,
    col: Int,
    modifier: (KeyItemC) -> KeyItemC,
): KeyboardC =
    this.copy(
        arr =
            this.arr.toMutableList().apply {
                if (row < this.size) {
                    this[row] =
                        this[row].toMutableList().apply {
                            if (col < this.size) {
                                this[col] = modifier(this[col])
                            }
                        }
                }
            },
    )

fun KeyboardC.alterKeys(vararg modifications: Triple<Int, Int, (KeyItemC) -> KeyItemC>): KeyboardC {
    var result = this
    for ((row, col, modifier) in modifications) {
        result = result.alterKey(row, col, modifier)
    }
    return result
}

@optics
data class KeyC(
    val action: KeyAction,
    val swipeReturnAction: KeyAction? = null,
    val display: KeyDisplay? =
        when (action) {
            is KeyAction.CommitText -> KeyDisplay.TextDisplay(action.text)
            else -> null
        },
    val capsModeDisplay: KeyDisplay? = null,
    val size: FontSizeVariant = FontSizeVariant.SMALL,
    val color: ColorVariant =
        when (size) {
            FontSizeVariant.LARGE -> ColorVariant.PRIMARY
            else -> ColorVariant.SECONDARY
        },
) {
    constructor(
        text: String,
        displayText: String = text,
        displayFont: FontFamily? = null,
        swipeReturnAction: KeyAction? = null,
        display: KeyDisplay = KeyDisplay.TextDisplay(displayText, fontFamily = displayFont),
        capsModeDisplay: KeyDisplay? = null,
        size: FontSizeVariant = FontSizeVariant.SMALL,
        color: ColorVariant =
            when (size) {
                FontSizeVariant.LARGE -> ColorVariant.PRIMARY
                else -> ColorVariant.SECONDARY
            },
    ) : this(
        KeyAction.CommitText(text),
        swipeReturnAction,
        display,
        capsModeDisplay,
        size,
        color,
    )

    companion object
}
