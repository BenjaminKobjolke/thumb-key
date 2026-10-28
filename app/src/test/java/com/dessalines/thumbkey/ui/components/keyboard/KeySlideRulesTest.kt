package com.dessalines.thumbkey.ui.components.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class KeySlideRulesTest {
    @Test
    fun cursorSlideStartMatchesExistingDeadzoneConditions() {
        for (deadzoneEnabled in listOf(false, true)) {
            for (beyondDeadzone in listOf(false, true)) {
                for (alreadyTriggered in listOf(false, true)) {
                    val expected =
                        (deadzoneEnabled && beyondDeadzone && !alreadyTriggered) ||
                            ((!alreadyTriggered) && (!deadzoneEnabled)) ||
                            alreadyTriggered

                    assertEquals(
                        "deadzone=$deadzoneEnabled beyond=$beyondDeadzone triggered=$alreadyTriggered",
                        expected,
                        shouldStartCursorSlide(deadzoneEnabled, beyondDeadzone, alreadyTriggered),
                    )
                }
            }
        }
    }

    @Test
    fun slideHoldDelaySlowsForTheFirstFourTicks() {
        for (tick in 0..3) assertEquals(300L, slideHoldDelayMs(tick))
        for (tick in 4..6) assertEquals(100L, slideHoldDelayMs(tick))
    }
}
