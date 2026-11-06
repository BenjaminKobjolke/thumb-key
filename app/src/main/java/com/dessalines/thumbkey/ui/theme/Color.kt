package com.dessalines.thumbkey.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun pink(): Pair<ColorScheme, ColorScheme> =
    Pair(
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
        ),
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
        ),
    )

fun green(): Pair<ColorScheme, ColorScheme> =
    Pair(
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
        ),
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
        ),
    )

fun magma(): Pair<ColorScheme, ColorScheme> {
    val md_theme_light_primary = Color(0xFF61000B) // Primary Legends
    val md_theme_light_onPrimary = Color(0xFFFFE8C2) // Menu Button Text
    val md_theme_light_primaryContainer = Color(0xFFFFFFFF)
    val md_theme_light_onPrimaryContainer = Color(0xFF000000)
    val md_theme_light_secondary = Color(0xFFDD3300) // Secondary Legends
    val md_theme_light_onSecondary = Color(0xFFFFFFFF)
    val md_theme_light_secondaryContainer = Color(0xFFFFE8C2)
    val md_theme_light_onSecondaryContainer = Color(0xFF000000)
    val md_theme_light_tertiary = Color(0xFFA01300) // Legend Pressed
    val md_theme_light_onTertiary = Color(0xFFFFFFFF)
    val md_theme_light_tertiaryContainer = Color(0xFFFF855F) // Key Up
    val md_theme_light_onTertiaryContainer = Color(0xFF000000)
    val md_theme_light_error = Color(0xFFCCCC00)
    val md_theme_light_errorContainer = Color(0xFFFFFFFF)
    val md_theme_light_onError = Color(0xFFFFFFFF)
    val md_theme_light_onErrorContainer = Color(0xFF410000)
    val md_theme_light_background = Color(0xFFFFFDE2) // Background
    val md_theme_light_onBackground = Color(0xFF000000)
    val md_theme_light_surface = Color(0xFFFFFDE2) // Primary Keys
    val md_theme_light_onSurface = Color(0xFF000000) // Menu Text
    val md_theme_light_surfaceVariant = Color(0xFFFFE8C2) // Secondary Keys
    val md_theme_light_onSurfaceVariant = Color(0xFF202020) // Menu Icons
    val md_theme_light_outline = Color(0xFFFFAC66) // Outline
    val md_theme_light_inverseOnSurface = Color(0xFFF1F1F1)
    val md_theme_light_inverseSurface = Color(0xFF202020)
    val md_theme_light_inversePrimary = Color(0xFFFFBF91) // Key Down
    val md_theme_light_shadow = Color(0xFF000000)
    val md_theme_light_surfaceTint = Color(0xFF404040)
    val md_theme_light_outlineVariant = Color(0xFFFFAC66)
    val md_theme_light_scrim = Color(0xFF000000)
    val md_theme_light_surfaceContainer = Color(0xFFFFFDE2)
    val md_theme_light_surfaceContainerHigh = Color(0xFFFFE8C2)
    val md_theme_light_surfaceContainerHighest = Color(0xFFFFE8C2)

    val md_theme_dark_primary = Color(0xFFFFDB71) // Primary Legends
    val md_theme_dark_onPrimary = Color(0xFFC62E00) // Menu Button Text
    val md_theme_dark_primaryContainer = Color(0xFFCC0000)
    val md_theme_dark_onPrimaryContainer = Color(0xFFE0E0E0)
    val md_theme_dark_secondary = Color(0xFFC62E00) // Secondary Legends
    val md_theme_dark_onSecondary = Color(0xFFE0E0E0)
    val md_theme_dark_secondaryContainer = Color(0xFF310006)
    val md_theme_dark_onSecondaryContainer = Color(0xFFE0E0E0)
    val md_theme_dark_tertiary = Color(0xFFF14934) // Legend Pressed
    val md_theme_dark_onTertiary = Color(0xFFFF4040)
    val md_theme_dark_tertiaryContainer = Color(0xFF490006) // Key Up
    val md_theme_dark_onTertiaryContainer = Color(0xFFFFFF40)
    val md_theme_dark_error = Color(0xFFFFFFFF)
    val md_theme_dark_errorContainer = Color(0xFFCCAA00)
    val md_theme_dark_onError = Color(0xFFE0E0E0)
    val md_theme_dark_onErrorContainer = Color(0xFFE0E0E0)
    val md_theme_dark_background = Color(0xFF080003) // Background
    val md_theme_dark_onBackground = Color(0xFFE0E0E0)
    val md_theme_dark_surface = Color(0xFF080003) // Primary Keys
    val md_theme_dark_onSurface = Color(0xFFFFFFFF) // Menu Text
    val md_theme_dark_surfaceVariant = Color(0xFF180004) // Secondary Keys
    val md_theme_dark_onSurfaceVariant = Color(0xFFC62E00) // Menu Icons
    val md_theme_dark_outline = Color(0xFFFF9300) // Outline
    val md_theme_dark_inverseOnSurface = Color(0xFFCC0000)
    val md_theme_dark_inverseSurface = Color(0xFFE0E0E0)
    val md_theme_dark_inversePrimary = Color(0xFF380015) // Key Down
    val md_theme_dark_shadow = Color(0xFF000000)
    val md_theme_dark_surfaceTint = Color(0xFF000000)
    val md_theme_dark_outlineVariant = Color(0xFFFF9300)
    val md_theme_dark_scrim = Color(0xFF000000)
    val md_theme_dark_surfaceContainer = Color(0xFF080003)
    val md_theme_dark_surfaceContainerHigh = Color(0xFF180004)
    val md_theme_dark_surfaceContainerHighest = Color(0xFF180004)

    val seed = Color(0xFF404040)

    val light =
        lightColorScheme(
            primary = md_theme_light_primary,
            onPrimary = md_theme_light_onPrimary,
            primaryContainer = md_theme_light_primaryContainer,
            onPrimaryContainer = md_theme_light_onPrimaryContainer,
            secondary = md_theme_light_secondary,
            onSecondary = md_theme_light_onSecondary,
            secondaryContainer = md_theme_light_secondaryContainer,
            onSecondaryContainer = md_theme_light_onSecondaryContainer,
            tertiary = md_theme_light_tertiary,
            onTertiary = md_theme_light_onTertiary,
            tertiaryContainer = md_theme_light_tertiaryContainer,
            onTertiaryContainer = md_theme_light_onTertiaryContainer,
            error = md_theme_light_error,
            errorContainer = md_theme_light_errorContainer,
            onError = md_theme_light_onError,
            onErrorContainer = md_theme_light_onErrorContainer,
            background = md_theme_light_background,
            onBackground = md_theme_light_onBackground,
            surface = md_theme_light_surface,
            onSurface = md_theme_light_onSurface,
            surfaceVariant = md_theme_light_surfaceVariant,
            onSurfaceVariant = md_theme_light_onSurfaceVariant,
            outline = md_theme_light_outline,
            inverseOnSurface = md_theme_light_inverseOnSurface,
            inverseSurface = md_theme_light_inverseSurface,
            inversePrimary = md_theme_light_inversePrimary,
            surfaceTint = md_theme_light_surfaceTint,
            outlineVariant = md_theme_light_outlineVariant,
            scrim = md_theme_light_scrim,
            surfaceContainer = md_theme_light_surfaceContainer,
            surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
            surfaceContainerHighest = md_theme_light_surfaceContainerHighest,
        )

    val dark =
        darkColorScheme(
            primary = md_theme_dark_primary,
            onPrimary = md_theme_dark_onPrimary,
            primaryContainer = md_theme_dark_primaryContainer,
            onPrimaryContainer = md_theme_dark_onPrimaryContainer,
            secondary = md_theme_dark_secondary,
            onSecondary = md_theme_dark_onSecondary,
            secondaryContainer = md_theme_dark_secondaryContainer,
            onSecondaryContainer = md_theme_dark_onSecondaryContainer,
            tertiary = md_theme_dark_tertiary,
            onTertiary = md_theme_dark_onTertiary,
            tertiaryContainer = md_theme_dark_tertiaryContainer,
            onTertiaryContainer = md_theme_dark_onTertiaryContainer,
            error = md_theme_dark_error,
            errorContainer = md_theme_dark_errorContainer,
            onError = md_theme_dark_onError,
            onErrorContainer = md_theme_dark_onErrorContainer,
            background = md_theme_dark_background,
            onBackground = md_theme_dark_onBackground,
            surface = md_theme_dark_surface,
            onSurface = md_theme_dark_onSurface,
            surfaceVariant = md_theme_dark_surfaceVariant,
            onSurfaceVariant = md_theme_dark_onSurfaceVariant,
            outline = md_theme_dark_outline,
            inverseOnSurface = md_theme_dark_inverseOnSurface,
            inverseSurface = md_theme_dark_inverseSurface,
            inversePrimary = md_theme_dark_inversePrimary,
            surfaceTint = md_theme_dark_surfaceTint,
            outlineVariant = md_theme_dark_outlineVariant,
            scrim = md_theme_dark_scrim,
            surfaceContainer = md_theme_dark_surfaceContainer,
            surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
            surfaceContainerHighest = md_theme_dark_surfaceContainerHighest,
        )
    return Pair(light, dark)
}
