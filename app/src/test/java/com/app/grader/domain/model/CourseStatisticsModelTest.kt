package com.app.grader.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CourseStatisticsModelTest {
    @Test
    fun directGradesAtMaximumWithTotalWeightingHundredAccumulateToHundred() {
        val gradeValues = listOf(
            com.app.grader.domain.types.GradeValue(10.0, null, 10.0),
            com.app.grader.domain.types.GradeValue(10.0, null, 10.0),
        )
        val weightings = listOf(40.0, 60.0)
        val accumulated = gradeValues.zip(weightings).sumOf { (grade, weighting) ->
            grade.getGradePercentage()!! / 100.0 * weighting
        }

        assertEquals(100.0, accumulated, 0.000001)
    }
}
