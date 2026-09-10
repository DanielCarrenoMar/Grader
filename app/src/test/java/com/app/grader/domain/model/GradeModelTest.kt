package com.app.grader.domain.model

import com.app.grader.domain.types.Percentage
import com.app.grader.infrastructure.database.dao.CalculatedGrade
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
        ).toGradeModel(TypeGradeModel(id = 0, title = "", max = 10, minToPass = 8.0, isFromSystem = false, isDirectPercentage = true))

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
                gradeValueRaw = 5.0,
                typeGradeModel = TypeGradeModel(
                    id = 0,
                    title = "",
                    max = 10,
                    minToPass = null,
                    isFromSystem = false,
                    isDirectPercentage = true,
                ),
                weight = Percentage(0.0),
            )
        }
    }
}
