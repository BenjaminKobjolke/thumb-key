package com.dessalines.thumbkey.ui.theme

import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.dessalines.thumbkey.db.AppSettings
import com.dessalines.thumbkey.utils.ColorVariant
import com.dessalines.thumbkey.utils.FontSizeVariant
import com.dessalines.thumbkey.utils.ThemeColor
import com.dessalines.thumbkey.utils.ThemeMode

@Composable
fun colorVariantToColor(colorVariant: ColorVariant): Color =
    when (colorVariant) {
        ColorVariant.SURFACE -> MaterialTheme.colorScheme.surface
        ColorVariant.SURFACE_VARIANT -> MaterialTheme.colorScheme.surfaceVariant
        ColorVariant.PRIMARY -> MaterialTheme.colorScheme.primary
        ColorVariant.SECONDARY -> MaterialTheme.colorScheme.secondary
        ColorVariant.MUTED -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5F)
    }

fun fontSizeVariantToFontSize(
    fontSizeVariant: FontSizeVariant,
    keySize: Dp,
    isUpperCase: Boolean,
): Dp {
    val divFactor =
        when (fontSizeVariant) {
            FontSizeVariant.LARGE -> 2.5f
            FontSizeVariant.MEDIUM -> 3.5f
            FontSizeVariant.SMALL -> 5f
            FontSizeVariant.SMALLEST -> 8f
        }
    val upperCaseFactor = if (isUpperCase) 0.8f else 1f
    return keySize.times(upperCaseFactor).div(divFactor)
}

val Dp.toPx get() = (this * Resources.getSystem().displayMetrics.density).value
val Float.pxToSp
    get() = TextUnit(this / Resources.getSystem().displayMetrics.scaledDensity, TextUnitType.Sp)

@Composable
fun ThumbkeyTheme(
    settings: AppSettings?,
    content: @Composable () -> Unit,
) {
    val themeMode = ThemeMode.entries[settings?.theme ?: 0]
    val themeColor = ThemeColor.entries[settings?.themeColor ?: 0]

    val ctx = LocalContext.current
    val android12OrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    // Dynamic schemes crash on lower than android 12
    val dynamicPair =
        if (android12OrLater) {
            Pair(dynamicLightColorScheme(ctx), dynamicDarkColorScheme(ctx))
        } else {
            pink()
        }

    val colorPair = colorPairFor(themeColor, dynamicPair)

    val systemTheme =
        if (!isSystemInDarkTheme()) {
            colorPair.first
        } else {
            colorPair.second
        }

    val colors =
        when (themeMode) {
            ThemeMode.System -> systemTheme
            ThemeMode.Light -> colorPair.first
            ThemeMode.Dark -> colorPair.second
        }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}

internal fun colorPairFor(
    themeColor: ThemeColor,
    dynamicPair: Pair<ColorScheme, ColorScheme>,
): Pair<ColorScheme, ColorScheme> = if (themeColor == ThemeColor.Dynamic) dynamicPair else STATIC_COLOR_SCHEMES.getValue(themeColor)()

private val STATIC_COLOR_SCHEMES: Map<ThemeColor, () -> Pair<ColorScheme, ColorScheme>> =
    mapOf(
        ThemeColor.Green to ::green,
        ThemeColor.Pink to ::pink,
        ThemeColor.Matrix to ::matrix,
        ThemeColor.Srcery to ::srcery,
        ThemeColor.Blue to ::blue,
        ThemeColor.Dracula to ::dracula,
        ThemeColor.Twilight to ::twilight,
        ThemeColor.HighContrast to ::highContrast,
        ThemeColor.HighContrastColorful to ::highContrastColorful,
        ThemeColor.Ancom to ::ancom,
        ThemeColor.Neon to ::neon,
    )
