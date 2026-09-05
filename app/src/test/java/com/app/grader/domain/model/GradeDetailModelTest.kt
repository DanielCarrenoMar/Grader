package com.app.grader.domain.model

import com.app.grader.domain.types.Percentage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
class GradeDetailModelTest {
    private val typeGrade = TypeGradeModel(id = 0, title = "", max = 7, minToPass = 4.0, isFromSystem = false, isDirectPercentage = false)

    @Test
    fun createNormalizesNullAndBlankSubgradeTitles() {
        val nullTitle = SubGradeModel.create(1, null, null, 0.0, 20).getOrThrow()
        val blankTitle = SubGradeModel.create(1, " ", null, 0.0, 20).getOrThrow()

        assertEquals("Sin título", nullTitle.title)
        assertEquals("Sin título", blankTitle.title)
    }

    @Test
    fun createUsesSelectedTypeGradeBoundsForSubgrades() {
        val result = GradeDetailModel.createResult(
            courseId = 1,
            title = "",
            description = "",
            gradeValue = 6.0,
            percentage = Percentage(50.0),
            typeGrade = typeGrade,
            subgrades = listOf(SubGradeModel.create(1, "Part", 6.0, 0.0, 20).getOrThrow()),
        )

        assertTrue(result.isSuccess)
        assertEquals(7.0, result.getOrThrow().subgrades.single().gradeValue.getMax(), 0.0)
    }

    @Test
    fun createRejectsNonblankOutOfRangeSubgrade() {
        val result = SubGradeModel.create(1, "Part", 8.0, 0.0, 7)

        assertTrue(result.isFailure)
        assertEquals("La calificación (8.0) debe estar entre 0 y 7.", result.exceptionOrNull()?.message)
    }

    @Test
    fun createAllowsBlankSubgradeAndBoundaryValues() {
        val blank = SubGradeModel.create(1, "Part", null, 0.0, 7)
        val zero = SubGradeModel.create(1, "Part", 0.0, 0.0, 7)
        val max = SubGradeModel.create(1, "Part", 7.0, 0.0, 7)

        assertTrue(blank.isSuccess)
        assertTrue(blank.getOrThrow().gradeValue.isBlank())
        assertEquals(0.0, zero.getOrThrow().gradeValue.getValue())
        assertEquals(7.0, max.getOrThrow().gradeValue.getValue())
    }

    @Test
    fun createRejectsNegativeAndAboveMaxValues() {
        assertTrue(SubGradeModel.create(1, "Part", -0.1, 0.0, 7).isFailure)
        assertTrue(SubGradeModel.create(1, "Part", 7.1, 0.0, 7).isFailure)
    }

    @Test
    fun createUsesTypeGradeMaxForDirectGradeInput() {
        val result = GradeDetailModel.createResult(
            courseId = 1,
            title = "",
            description = "",
            gradeValue = 5.0,
            percentage = Percentage(50.0),
            typeGrade = TypeGradeModel(id = 0, title = "", max = 10, minToPass = null, isFromSystem = false, isDirectPercentage = true),
        )

        assertTrue(result.isSuccess)
        assertEquals(10.0, result.getOrThrow().gradeValue.getMax(), 0.0)
        assertEquals(50.0, result.getOrThrow().gradeValue.getGradePercentage()!!, 0.0)
        assertTrue(result.getOrThrow().subgrades.isEmpty())
    }

    @Test
    fun createRejectsZeroWeighting() {
        val result = GradeDetailModel.createResult(
            courseId = 1,
            title = "",
            description = "",
            gradeValue = 5.0,
            percentage = Percentage(0.0),
            typeGrade = typeGrade,
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun createRejectsInvalidGradeIdAndMax() {
        assertTrue(SubGradeModel.create(0, "Part", null, 0.0, 7).isFailure)
        assertTrue(SubGradeModel.create(1, "Part", null, 0.0, -1).isFailure)
    }

    @Test
    fun createRejectsOutOfRangeGradeValue() {
        val result = GradeDetailModel.createResult(
            courseId = 1,
            title = "Exam",
            description = "",
            gradeValue = 8.5,
            percentage = Percentage(50.0),
            typeGrade = typeGrade, // max is 7
        )

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as? GradeDetailValidationException
        assertTrue(ex != null)
        val error = ex!!.errors.firstOrNull { it.field == "grade" }
        assertTrue(error != null)
        assertEquals("La calificación (8.5) debe estar entre 0 y 7.", error!!.message)
    }

    @Test
    fun createRejectsNegativeGradeValue() {
        val result = GradeDetailModel.createResult(
            courseId = 1,
            title = "Exam",
            description = "",
            gradeValue = -1.0,
            percentage = Percentage(50.0),
            typeGrade = typeGrade,
        )

        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull() as? GradeDetailValidationException
        assertTrue(ex != null)
        val error = ex!!.errors.firstOrNull { it.field == "grade" }
        assertTrue(error != null)
        assertEquals("La calificación (-1.0) debe estar entre 0 y 7.", error!!.message)
    }
}
