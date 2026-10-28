package com.dessalines.thumbkey.ui.components.keyboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dessalines.thumbkey.ui.theme.colorVariantToColor
import com.dessalines.thumbkey.ui.theme.fontSizeVariantToFontSize
import com.dessalines.thumbkey.ui.theme.pxToSp
import com.dessalines.thumbkey.ui.theme.toPx
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.KeyC
import com.dessalines.thumbkey.utils.KeyDisplay
import com.dessalines.thumbkey.utils.KeyItemC
import com.dessalines.thumbkey.utils.SwipeDirection

/** Renders the swipe legends and release animation for one keyboard key. */
internal data class KeyLegendSizes(
    val keySize: Dp,
    val keyBorderWidth: Dp,
    val keyRadius: Dp,
)

internal data class KeyLegendDisplay(
    val hideLetters: Boolean,
    val hideSymbols: Boolean,
    val capsLock: Boolean,
)

private enum class LegendPadding {
    Diagonal,
    Vertical,
    Horizontal,
}

private data class KeyLegendPlacement(
    val direction: SwipeDirection,
    val alignment: Alignment,
    val padding: LegendPadding,
)

private val KEY_LEGEND_LAYOUT =
    listOf(
        KeyLegendPlacement(SwipeDirection.TOP_LEFT, Alignment.TopStart, LegendPadding.Diagonal),
        KeyLegendPlacement(SwipeDirection.TOP, Alignment.TopCenter, LegendPadding.Vertical),
        KeyLegendPlacement(SwipeDirection.TOP_RIGHT, Alignment.TopEnd, LegendPadding.Diagonal),
        KeyLegendPlacement(SwipeDirection.LEFT, Alignment.CenterStart, LegendPadding.Horizontal),
        KeyLegendPlacement(SwipeDirection.RIGHT, Alignment.CenterEnd, LegendPadding.Horizontal),
        KeyLegendPlacement(SwipeDirection.BOTTOM_LEFT, Alignment.BottomStart, LegendPadding.Diagonal),
        KeyLegendPlacement(SwipeDirection.BOTTOM, Alignment.BottomCenter, LegendPadding.Vertical),
        KeyLegendPlacement(SwipeDirection.BOTTOM_RIGHT, Alignment.BottomEnd, LegendPadding.Diagonal),
    )

@Composable
internal fun KeyLegends(
    key: KeyItemC,
    sizes: KeyLegendSizes,
    display: KeyLegendDisplay,
) {
    KEY_LEGEND_LAYOUT.forEach { placement ->
        KeyLegend(key, placement, sizes, display)
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        KeyText(key.center, textSize(sizes), display)
    }
}

@Composable
private fun KeyLegend(
    key: KeyItemC,
    placement: KeyLegendPlacement,
    sizes: KeyLegendSizes,
    display: KeyLegendDisplay,
) {
    Box(
        contentAlignment = placement.alignment,
        modifier = Modifier.fillMaxSize().then(legendPadding(placement.padding, sizes)),
    ) {
        key.getSwipe(placement.direction)?.let { KeyText(it, textSize(sizes), display) }
    }
}

private fun legendPadding(
    kind: LegendPadding,
    sizes: KeyLegendSizes,
): Modifier =
    when (kind) {
        LegendPadding.Diagonal -> {
            val radiusPercent = sizes.keyRadius.value / 100F
            val xPadding = 2.dp + sizes.keyBorderWidth
            val yPadding = sizes.keyBorderWidth
            val diagonalXPadding = lerp(xPadding, xPadding + 20.dp, radiusPercent)
            val diagonalYPadding = lerp(yPadding, yPadding + 20.dp, radiusPercent)
            Modifier.padding(horizontal = diagonalXPadding, vertical = diagonalYPadding)
        }

        LegendPadding.Vertical -> {
            Modifier.padding(vertical = sizes.keyBorderWidth)
        }

        LegendPadding.Horizontal -> {
            Modifier.padding(horizontal = 2.dp + sizes.keyBorderWidth)
        }
    }

private fun textSize(sizes: KeyLegendSizes): Dp = sizes.keySize - sizes.keyBorderWidth

@Composable
internal fun KeyReleaseAnimation(
    releasedKey: String?,
    isPasswordField: Boolean,
    animationSpeed: Int,
    keySize: Dp,
) {
    ReleaseAnimationBackdrop(releasedKey, isPasswordField, animationSpeed)
    ReleaseAnimationText(releasedKey, isPasswordField, animationSpeed, keySize)
}

@Composable
private fun ReleaseAnimationBackdrop(
    releasedKey: String?,
    isPasswordField: Boolean,
    animationSpeed: Int,
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize().background(color = Color(0, 0, 0, 0)),
        visible = releasedKey != null && !isPasswordField,
        enter = EnterTransition.None,
        exit = fadeOut(tween(animationSpeed)),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.tertiaryContainer),
        ) {}
    }
}

@Composable
private fun ReleaseAnimationText(
    releasedKey: String?,
    isPasswordField: Boolean,
    animationSpeed: Int,
    keySize: Dp,
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize().background(color = Color(0, 0, 0, 0)),
        visible = releasedKey != null && !isPasswordField,
        enter = slideInVertically(tween(animationSpeed)),
        exit = fadeOut(tween(animationSpeed)),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            releasedKey?.let { ReleasedKeyText(it, keySize) }
        }
    }
}

@Composable
private fun ReleasedKeyText(
    text: String,
    keySize: Dp,
) {
    val spSize =
        fontSizeVariantToFontSize(
            fontSizeVariant = FontSizeVariant.LARGE,
            keySize = keySize,
            isUpperCase = false,
        ).toPx.pxToSp
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = spSize,
        lineHeight = spSize,
        color = MaterialTheme.colorScheme.tertiary,
    )
}

@Composable
internal fun KeyText(
    key: KeyC,
    keySize: Dp,
    displayOptions: KeyLegendDisplay,
) {
    val color = colorVariantToColor(colorVariant = key.color)
    val fontSize =
        fontSizeVariantToFontSize(
            fontSizeVariant = key.size,
            keySize = keySize,
            isUpperCase = key.displayIsUpperCase(),
        )

    val display =
        if (displayOptions.capsLock) {
            key.capsModeDisplay ?: key.display
        } else {
            key.display
        }

    when (display) {
        is KeyDisplay.IconDisplay -> {
            KeyIcon(display, color, fontSize)
        }

        is KeyDisplay.TextDisplay -> {
            KeyTextContent(display, color, fontSize, displayOptions)
        }

        null -> {}
    }
}

private fun KeyC.displayIsUpperCase(): Boolean =
    when (val currentDisplay = display) {
        is KeyDisplay.TextDisplay -> currentDisplay.text.firstOrNull()?.isUpperCase() == true
        else -> false
    }

@Composable
private fun KeyIcon(
    display: KeyDisplay.IconDisplay,
    color: Color,
    fontSize: Dp,
) {
    Icon(
        imageVector = display.icon,
        contentDescription = display.icon.name,
        tint = color,
        modifier = Modifier.size(fontSize),
    )
}

@Composable
private fun KeyTextContent(
    display: KeyDisplay.TextDisplay,
    color: Color,
    fontSize: Dp,
    displayOptions: KeyLegendDisplay,
) {
    // Only hide the letters for text, not symbols.
    if (shouldHideKeyText(display.text, displayOptions)) return
    val spSize = fontSize.toPx.pxToSp
    Text(
        text = display.text,
        fontWeight = FontWeight.Bold,
        fontFamily = display.fontFamily,
        fontSize = spSize,
        lineHeight = spSize,
        color = color,
    )
}

private fun shouldHideKeyText(
    text: String,
    display: KeyLegendDisplay,
): Boolean {
    val containsLetters = text.any { it.isLetter() }
    val containsNumbers = text.any { it.isDigit() }
    return when {
        containsLetters -> display.hideLetters
        containsNumbers -> false
        else -> display.hideSymbols
    }
}
