package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun KeyboardFrame(
    style: KeyboardFrameStyle,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        contentAlignment = style.contentAlignment,
        modifier = Modifier.keyboardBackdrop(style.backdropEnabled, style.backdropColor).then(style.modifier),
    ) {
        if (style.backdropEnabled) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }
        content()
    }
}

internal data class KeyboardFrameStyle(
    val backdropEnabled: Boolean,
    val backdropColor: Color,
    val contentAlignment: Alignment = Alignment.TopStart,
    val modifier: Modifier = Modifier,
)

internal fun Modifier.keyboardBackdrop(
    enabled: Boolean,
    color: Color,
) = then(if (enabled) Modifier.background(color) else Modifier)

internal fun Modifier.keyboardBottomInset(
    ignoreBottomPadding: Boolean,
    pushupSize: Dp,
) = then(if (!ignoreBottomPadding) Modifier.safeDrawingPadding() else Modifier)
    .padding(bottom = pushupSize)

internal fun Modifier.keyboardBackdropPadding(
    enabled: Boolean,
    padding: Dp,
) = then(if (enabled) Modifier.padding(top = padding) else Modifier)

internal data class EmojiPickerBoxOptions(
    val keyPadding: Int,
    val cornerRadius: Float,
    val borderWidth: Float,
    val borderColor: Color,
    val shape: Shape = RoundedCornerShape(cornerRadius.dp),
)

@Composable
internal fun emojiPickerBoxModifier(options: EmojiPickerBoxOptions) =
    Modifier
        .padding(options.keyPadding.dp)
        .clip(options.shape)
        .then(
            if (options.borderWidth > 0.0f) {
                Modifier.border(options.borderWidth.dp, options.borderColor, options.shape)
            } else {
                Modifier
            },
        ).background(MaterialTheme.colorScheme.surface)
