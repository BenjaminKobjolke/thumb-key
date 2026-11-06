package com.dessalines.thumbkey.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun pink(): Pair<ColorScheme, ColorScheme> = Pair(pinkLight(), pinkDark())

private fun pinkLight() =
    lightSchemeFrom(
        longArrayOf(
            0xFFA63166,
            0xFFFFFFFF,
            0xFFFFD9E3,
            0xFF3E001F,
            0xFF745660,
            0xFFFFFFFF,
            0xFFFFD9E3,
            0xFF2A151D,
            0xFF7D5636,
            0xFFFFFFFF,
            0xFFFFDCC3,
            0xFF2F1500,
            0xFFBA1A1A,
            0xFFFFDAD6,
            0xFFFFFFFF,
            0xFF410002,
            0xFFFFFBFF,
            0xFF201A1C,
            0xFFFFFBFF,
            0xFF201A1C,
            0xFFF2DDE2,
            0xFF514347,
            0xFF837377,
            0xFFFAEEF0,
            0xFF352F30,
            0xFFFFB0CB,
            0xFFA63166,
            0xFFD5C2C6,
            0xFF000000,
        ),
    )

private fun pinkDark() =
    darkSchemeFrom(
        longArrayOf(
            0xFFFFB0CB,
            0xFF640036,
            0xFF87164E,
            0xFFFFD9E3,
            0xFFE2BDC7,
            0xFF422932,
            0xFF5A3F48,
            0xFFFFD9E3,
            0xFFF0BC95,
            0xFF48290D,
            0xFF623F21,
            0xFFFFDCC3,
            0xFFFFB4AB,
            0xFF93000A,
            0xFF690005,
            0xFFFFDAD6,
            0xFF201A1C,
            0xFFEBE0E1,
            0xFF201A1C,
            0xFFEBE0E1,
            0xFF514347,
            0xFFD5C2C6,
            0xFF9E8C91,
            0xFF201A1C,
            0xFFEBE0E1,
            0xFFA63166,
            0xFFFFB0CB,
            0xFF514347,
            0xFF000000,
        ),
    )

fun green(): Pair<ColorScheme, ColorScheme> = Pair(greenLight(), greenDark())

private fun greenLight() =
    lightSchemeFrom(
        longArrayOf(
            0xFF216C29,
            0xFFFFFFFF,
            0xFFA7F5A1,
            0xFF002204,
            0xFF52634F,
            0xFFFFFFFF,
            0xFFD5E8CF,
            0xFF101F10,
            0xFF38656A,
            0xFFFFFFFF,
            0xFFBCEBF1,
            0xFF002023,
            0xFFBA1A1A,
            0xFFFFDAD6,
            0xFFFFFFFF,
            0xFF410002,
            0xFFFCFDF6,
            0xFF1A1C19,
            0xFFFCFDF6,
            0xFF1A1C19,
            0xFFDEE5D8,
            0xFF424940,
            0xFF72796F,
            0xFFF0F1EB,
            0xFF2F312D,
            0xFF8BD987,
            0xFF216C29,
            0xFFC2C9BD,
            0xFF000000,
        ),
    )

private fun greenDark() =
    darkSchemeFrom(
        longArrayOf(
            0xFF8BD987,
            0xFF00390B,
            0xFF005314,
            0xFFA7F5A1,
            0xFFB9CCB3,
            0xFF253423,
            0xFF3B4B38,
            0xFFD5E8CF,
            0xFFA0CFD4,
            0xFF00363B,
            0xFF1F4D52,
            0xFFBCEBF1,
            0xFFFFB4AB,
            0xFF93000A,
            0xFF690005,
            0xFFFFDAD6,
            0xFF1A1C19,
            0xFFE2E3DD,
            0xFF1A1C19,
            0xFFE2E3DD,
            0xFF424940,
            0xFFC2C9BD,
            0xFF8C9388,
            0xFF1A1C19,
            0xFFE2E3DD,
            0xFF216C29,
            0xFF8BD987,
            0xFF424940,
            0xFF000000,
        ),
    )

fun magma(): Pair<ColorScheme, ColorScheme> = Pair(magmaLight(), magmaDark())

private fun magmaLight() =
    lightColorScheme(
        primary = Color(0xFF61000B), // Primary Legends
        onPrimary = Color(0xFFFFE8C2), // Menu Button Text
        primaryContainer = Color(0xFFFFFFFF),
        onPrimaryContainer = Color(0xFF000000),
        secondary = Color(0xFFDD3300), // Secondary Legends
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFFFE8C2),
        onSecondaryContainer = Color(0xFF000000),
        tertiary = Color(0xFFA01300), // Legend Pressed
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFF855F), // Key Up
        onTertiaryContainer = Color(0xFF000000),
        error = Color(0xFFCCCC00),
        errorContainer = Color(0xFFFFFFFF),
        onError = Color(0xFFFFFFFF),
        onErrorContainer = Color(0xFF410000),
        background = Color(0xFFFFFDE2), // Background
        onBackground = Color(0xFF000000),
        surface = Color(0xFFFFFDE2), // Primary Keys
        onSurface = Color(0xFF000000), // Menu Text
        surfaceVariant = Color(0xFFFFE8C2), // Secondary Keys
        onSurfaceVariant = Color(0xFF202020), // Menu Icons
        outline = Color(0xFFFFAC66), // Outline
        inverseOnSurface = Color(0xFFF1F1F1),
        inverseSurface = Color(0xFF202020),
        inversePrimary = Color(0xFFFFBF91), // Key Down
        surfaceTint = Color(0xFF404040),
        outlineVariant = Color(0xFFFFAC66),
        scrim = Color(0xFF000000),
        surfaceContainer = Color(0xFFFFFDE2),
        surfaceContainerHigh = Color(0xFFFFE8C2),
        surfaceContainerHighest = Color(0xFFFFE8C2),
    )

private fun magmaDark() =
    darkColorScheme(
        primary = Color(0xFFFFDB71), // Primary Legends
        onPrimary = Color(0xFFC62E00), // Menu Button Text
        primaryContainer = Color(0xFFCC0000),
        onPrimaryContainer = Color(0xFFE0E0E0),
        secondary = Color(0xFFC62E00), // Secondary Legends
        onSecondary = Color(0xFFE0E0E0),
        secondaryContainer = Color(0xFF310006),
        onSecondaryContainer = Color(0xFFE0E0E0),
        tertiary = Color(0xFFF14934), // Legend Pressed
        onTertiary = Color(0xFFFF4040),
        tertiaryContainer = Color(0xFF490006), // Key Up
        onTertiaryContainer = Color(0xFFFFFF40),
        error = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFCCAA00),
        onError = Color(0xFFE0E0E0),
        onErrorContainer = Color(0xFFE0E0E0),
        background = Color(0xFF080003), // Background
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF080003), // Primary Keys
        onSurface = Color(0xFFFFFFFF), // Menu Text
        surfaceVariant = Color(0xFF180004), // Secondary Keys
        onSurfaceVariant = Color(0xFFC62E00), // Menu Icons
        outline = Color(0xFFFF9300), // Outline
        inverseOnSurface = Color(0xFFCC0000),
        inverseSurface = Color(0xFFE0E0E0),
        inversePrimary = Color(0xFF380015), // Key Down
        surfaceTint = Color(0xFF000000),
        outlineVariant = Color(0xFFFF9300),
        scrim = Color(0xFF000000),
        surfaceContainer = Color(0xFF080003),
        surfaceContainerHigh = Color(0xFF180004),
        surfaceContainerHighest = Color(0xFF180004),
    )
