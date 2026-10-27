package com.dessalines.thumbkey.ui.theme

// Converts ordered palette values into Material color schemes.

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal fun lightSchemeFrom(values: LongArray): ColorScheme = schemeFrom(values, dark = false)

internal fun darkSchemeFrom(
    values: LongArray,
    useDefaultTertiary: Boolean = false,
): ColorScheme = schemeFrom(values, dark = true, useDefaultTertiary = useDefaultTertiary)

private fun schemeFrom(
    values: LongArray,
    dark: Boolean,
    useDefaultTertiary: Boolean = false,
): ColorScheme {
    require(values.size == COLOR_SCHEME_FIELD_COUNT)
    val colors = values.map { Color(it.toULong()) }
    val base = if (dark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = colors[0],
        onPrimary = colors[1],
        primaryContainer = colors[2],
        onPrimaryContainer = colors[3],
        secondary = colors[4],
        onSecondary = colors[5],
        secondaryContainer = colors[6],
        onSecondaryContainer = colors[7],
        tertiary = if (useDefaultTertiary) base.tertiary else colors[8],
        onTertiary = colors[9],
        tertiaryContainer = colors[10],
        onTertiaryContainer = colors[11],
        error = colors[12],
        errorContainer = colors[13],
        onError = colors[14],
        onErrorContainer = colors[15],
        background = colors[16],
        onBackground = colors[17],
        surface = colors[18],
        onSurface = colors[19],
        surfaceVariant = colors[20],
        onSurfaceVariant = colors[21],
        outline = colors[22],
        inverseOnSurface = colors[23],
        inverseSurface = colors[24],
        inversePrimary = colors[25],
        surfaceTint = colors[26],
        outlineVariant = colors[27],
        scrim = colors[28],
    )
}

private const val COLOR_SCHEME_FIELD_COUNT = 29
