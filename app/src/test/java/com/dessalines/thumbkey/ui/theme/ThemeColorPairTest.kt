package com.dessalines.thumbkey.ui.theme

import com.dessalines.thumbkey.utils.ThemeColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeColorPairTest {
    @Test
    fun everyThemeColorSelectsItsExistingPalette() {
        val dynamicPair = pink()
        val expectedPairs =
            mapOf(
                ThemeColor.Dynamic to dynamicPair,
                ThemeColor.Green to green(),
                ThemeColor.Pink to pink(),
                ThemeColor.Matrix to matrix(),
                ThemeColor.Srcery to srcery(),
                ThemeColor.Blue to blue(),
                ThemeColor.Dracula to dracula(),
                ThemeColor.Twilight to twilight(),
                ThemeColor.HighContrast to highContrast(),
                ThemeColor.HighContrastColorful to highContrastColorful(),
                ThemeColor.Ancom to ancom(),
                ThemeColor.Neon to neon(),
            )

        assertEquals(ThemeColor.entries.toSet(), expectedPairs.keys)
        expectedPairs.forEach { (themeColor, expectedPair) ->
            val actualPair = colorPairFor(themeColor, dynamicPair)
            assertTrue(
                "$themeColor light palette",
                expectedPair.first.primary == actualPair.first.primary,
            )
            assertTrue(
                "$themeColor dark palette",
                expectedPair.second.primary == actualPair.second.primary,
            )
        }
    }
}
