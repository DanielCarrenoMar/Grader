package com.app.grader.domain.types

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [Percentage] value object.
 *
 * Contract: The constructor MUST silently clamp the input to the [0.0, 100.0]
 * range instead of throwing. This is what allows the data layer to keep the
 * database consistent after a repair migration: even if the DB returns a
 * value outside the historical bounds, the in-memory representation is safe.
 */
class PercentageTest {

    @Test
    fun constructor_clampsValueAbove100() {
        val percentage = Percentage(150.0)
        assertEquals(100.0, percentage.getPercentage(), 0.0)
    }

    @Test
    fun constructor_clampsNegativeValue() {
        val percentage = Percentage(-5.0)
        assertEquals(0.0, percentage.getPercentage(), 0.0)
    }

    @Test
    fun constructor_keepsValueInRangeUnchanged() {
        val percentage = Percentage(50.0)
        assertEquals(50.0, percentage.getPercentage(), 0.0)
    }

    @Test
    fun setPercentage_clampsValueAbove100() {
        val percentage = Percentage(0.0)
        percentage.setPercentage(120.0)
        assertEquals(100.0, percentage.getPercentage(), 0.0)
    }
}
