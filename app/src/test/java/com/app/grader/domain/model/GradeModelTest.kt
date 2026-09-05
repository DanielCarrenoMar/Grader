package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage
import com.app.grader.infrastructure.database.dao.CalculatedGrade
import com.app.grader.domain.model.TypeGradeModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GradeModelTest {
    @Test
    fun calculatedDirectGradeUsesWeightingAsGradeValueMax() {
        val model = CalculatedGrade(
            courseId = 1,
            title = "Exam",
            description = "",
            gradePercentage = 50.0,
            weightingPercentage = 40.0,
        ).toGradeModel(TypeGradeModel(max = 10, minToPass = 8.0, isDirectPercentage = true))

        assertEquals(40.0, model.gradeValue.getMax(), 0.0)
        assertEquals(50.0, model.gradeValue.getGradePercentage()!!, 0.0)
    }

    @Test
    fun constructorRejectsZeroWeighting() {
        assertThrows(GradeDetailValidationException::class.java) {
            GradeModel(
                courseId = 1,
                title = "Quiz",
                description = "",
                gradeValue = GradeValue(5.0, null, 10.0),
                percentage = Percentage(0.0),
                isDirectPercentage = true,
            )
        }
    }
}
