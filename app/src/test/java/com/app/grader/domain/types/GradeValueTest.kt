package com.app.grader.domain.types

import org.junit.Assert.assertEquals
import org.junit.Test

class GradeValueTest {
    @Test
    fun directPercentageConversionAndRoundTripUsesMaxTen() {
        val grade = GradeValue.createFromGradePercentage(50.0, null, 10.0)

        assertEquals(5.0, grade.getValue()!!, 0.000001)
        assertEquals(50.0, grade.getGradePercentage()!!, 0.000001)
    }

    @Test
    fun supportsFractionalMax() {
        val grade = GradeValue(1.25, null, 2.5)

        assertEquals(50.0, grade.getGradePercentage()!!, 0.000001)
    }

    @Test
    fun nullMinToPassFallsBackToMax() {
        val grade = GradeValue(5.0, null, 10.0)

        assertEquals(10.0, grade.getMinToPass(), 0.000001)
        assertEquals(false, grade.isFail())
    }
}
