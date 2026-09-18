package com.app.grader.infrastructure.database.repository

import com.app.grader.infrastructure.database.dao.CalculatedCourse
import com.app.grader.infrastructure.database.dao.CourseDao
import com.app.grader.infrastructure.database.dao.GradeDao
import com.app.grader.infrastructure.database.dao.SemesterDao
import com.app.grader.infrastructure.database.dao.SubGradeDao
import com.app.grader.infrastructure.database.dao.TypeGradeDao
import com.app.grader.infrastructure.database.entitites.TypeGradeEntity
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.types.GradeValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [LocalStorageRepositoryImpl.getAverageFromSemester].
 *
 * Contract: the semester average is derived from the percentage average returned by the
 * database (SemesterDao.getAverageFromSemester / getAverageRoundFromSemester) converted to the
 * scale of the DEFAULT grade type configured on the device
 * (AppConfigRepository.getDefaultTypeGradeId):
 * - Numeric grade type (`isDirectPercentage = false`): the percentage scale (0-100) is mapped to
 *   the grade-type scale (0..max) via `percentage * max / 100`.
 * - Direct percentage grade type (`isDirectPercentage = true`): max is 100, so the percentage
 *   stays on the 0-100 scale unchanged.
 * - The result is a [GradeValue] holding the converted value, the type grade `max` and the type
 *   grade `minToPass`, so fail/blank semantics follow the configured type grade.
 * - When the database has no evaluated grades (`average == null`) the repository returns a blank
 *   [GradeValue] on the configured scale (value null, `max` and `minToPass` from the type grade).
 * - When [AppConfigRepository.isRoundFinalCourseAverage] is enabled the rounded DAO query is used;
 *   otherwise the plain one.
 * - When no default grade type is configured the repository fails with
 *   [IllegalStateException].
 */
class SemesterAverageRepositoryTest {

    private val semesterDao: SemesterDao = mock()
    private val courseDao: CourseDao = mock()
    private val gradeDao: GradeDao = mock()
    private val subGradeDao: SubGradeDao = mock()
    private val typeGradeDao: TypeGradeDao = mock()
    private val appConfigRepository: AppConfigRepository = mock()

    private val repo = LocalStorageRepositoryImpl(
        semesterDao = semesterDao,
        courseDao = courseDao,
        gradeDao = gradeDao,
        subGradeDao = subGradeDao,
        typeGradeDao = typeGradeDao,
        appConfigRepository = appConfigRepository,
    )

    private fun typeGradeEntity(
        max: Int,
        minToPass: Double? = null,
        isDirectPercentage: Boolean = false,
    ): TypeGradeEntity = TypeGradeEntity(
        id = 1,
        title = "Test",
        max = max,
        minToPass = minToPass,
        isFromSystem = false,
        isDirectPercentage = isDirectPercentage,
        active = true,
    )

    private suspend fun stubDefaultTypeGrade(
        max: Int = 20,
        minToPass: Double? = null,
        isDirectPercentage: Boolean = false,
    ) {
        whenever(appConfigRepository.getDefaultTypeGradeId()).thenReturn(1)
        whenever(typeGradeDao.getTypeGradeById(1)).thenReturn(
            typeGradeEntity(max = max, minToPass = minToPass, isDirectPercentage = isDirectPercentage)
        )
    }

    private suspend fun stubDatabaseAverage(percentage: Double?, semesterId: Int? = 1) {
        whenever(semesterDao.getAverageFromSemester(semesterId)).thenReturn(percentage)
    }

    private fun course(
        id: Int = 1,
        semesterId: Int? = 1,
        typeGradeId: Int = 1,
        title: String = "Course",
        uc: Int = 1,
        average: Double? = null,
    ): CalculatedCourse = CalculatedCourse(
        id = id,
        semesterId = semesterId,
        typeGradeId = typeGradeId,
        title = title,
        uc = uc,
        average = average,
        totalWeightingPercentage = null,
    )

    @Test
    fun numericTypeGrade_max20_scalesSemesterAverageFromPercentageToGradeScale() {
        runBlocking {
            stubDefaultTypeGrade(max = 20, minToPass = 9.5)
            stubDatabaseAverage(percentage = 45.0)

            val result = repo.getAverageFromSemester(1)

            // 45.0 * 20 / 100 = 9.0 points on the 0..20 numeric scale.
            assertEquals(9.0, result.getValue()!!, 0.000001)
            assertEquals(20.0, result.getMax(), 0.000001)
            assertEquals(9.5, result.getMinToPass(), 0.000001)
            verify(appConfigRepository).getDefaultTypeGradeId()
            verify(semesterDao).getAverageFromSemester(1)
        }
    }

    @Test
    fun numericTypeGrade_max10_scalesSemesterAverageProportionally() {
        runBlocking {
            stubDefaultTypeGrade(max = 10)
            stubDatabaseAverage(percentage = 45.0)

            val result = repo.getAverageFromSemester(1)

            // 45.0 * 10 / 100 = 4.5 points on the 0..10 numeric scale.
            assertEquals(4.5, result.getValue()!!, 0.000001)
            assertEquals(10.0, result.getMax(), 0.000001)
        }
    }

    @Test
    fun numericTypeGrade_max100_keepsPercentageScale() {
        runBlocking {
            stubDefaultTypeGrade(max = 100)
            stubDatabaseAverage(percentage = 45.0)

            val result = repo.getAverageFromSemester(1)

            // 45.0 * 100 / 100 = 45.0: a numeric scale of 100 matches the percentage scale.
            assertEquals(45.0, result.getValue()!!, 0.000001)
        }
    }

    @Test
    fun directPercentageTypeGrade_keepsSemesterAverageUnscaled() {
        runBlocking {
            stubDefaultTypeGrade(max = 100, isDirectPercentage = true)
            stubDatabaseAverage(percentage = 45.0)

            val result = repo.getAverageFromSemester(1)

            // Direct percentage: max is 100, so the percentage is not rescaled.
            assertEquals(45.0, result.getValue()!!, 0.000001)
            assertEquals(100.0, result.getMax(), 0.000001)
        }
    }

    @Test
    fun minToPassFromTypeGrade_isPreservedInSemesterAverage() {
        runBlocking {
            stubDefaultTypeGrade(max = 20, minToPass = 9.5)
            stubDatabaseAverage(percentage = 60.0)

            val result = repo.getAverageFromSemester(1)

            // 60.0 * 20 / 100 = 12.0 points, above the configured minimum to pass.
            assertEquals(12.0, result.getValue()!!, 0.000001)
            assertEquals(9.5, result.getMinToPass(), 0.000001)
            assertFalse(result.isFail())
        }
    }

    @Test
    fun averageBelowMinToPass_isFail() {
        runBlocking {
            stubDefaultTypeGrade(max = 20, minToPass = 9.5)
            stubDatabaseAverage(percentage = 40.0)

            val result = repo.getAverageFromSemester(1)

            // 40.0 * 20 / 100 = 8.0 points, below the 9.5 minimum to pass.
            assertEquals(8.0, result.getValue()!!, 0.000001)
            assertTrue(result.isFail())
        }
    }

    @Test
    fun noEvaluatedGrades_nullAverage_yieldsBlankGradeValueOnConfiguredScale() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            stubDatabaseAverage(percentage = null)

            val result = repo.getAverageFromSemester(1)

            assertNull(result.getValue())
            assertTrue(result.isBlank())
            // The blank semester still carries the configured scale and its default minimum.
            assertEquals(20.0, result.getMax(), 0.000001)
            assertEquals(10.0, result.getMinToPass(), 0.000001)
        }
    }

    @Test
    fun nullSemesterId_isForwardedToTheSemesterQuery() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            stubDatabaseAverage(percentage = 45.0, semesterId = null)

            val result = repo.getAverageFromSemester(null)

            // Courses without a semester are queried with a null semester id.
            assertEquals(9.0, result.getValue()!!, 0.000001)
            verify(semesterDao).getAverageFromSemester(null)
        }
    }

    @Test
    fun missingDefaultTypeGrade_throwsIllegalStateException() {
        runBlocking {
            whenever(appConfigRepository.getDefaultTypeGradeId()).thenReturn(1)
            whenever(typeGradeDao.getTypeGradeById(1)).thenReturn(null)

            assertThrows(IllegalStateException::class.java) {
                runBlocking { repo.getAverageFromSemester(1) }
            }
        }
    }

    // -------------------------------------------------------------------------------------------
    // Group: the semester average must always live on the configured grade scale.
    //
    // The converted value and the minimum-to-pass are bound to the configured type grade base:
    // 0..max for a numeric type (isDirectPercentage = false) and 0..100 for a direct percentage
    // type. The semester average may never appear outside that scale, because the UI renders it
    // against the configured maximum.
    // -------------------------------------------------------------------------------------------
    private fun assertAverageStaysOnConfiguredScale(max: Double, result: GradeValue) {
        val delta = 0.000001
        val value = result.getValue()
        if (value != null) {
            assertTrue(
                "average value must stay inside the configured base 0..$max",
                value >= 0.0 && value <= max + delta
            )
        }
        assertTrue(
            "minToPass must stay inside the configured base 0..$max",
            result.getMinToPass() >= 0.0 && result.getMinToPass() <= max + delta
        )
    }

    @Test
    fun averageStaysOnConfiguredScale_numericMax20() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            stubDatabaseAverage(percentage = 95.0)

            val result = repo.getAverageFromSemester(1)

            // 95.0 * 20 / 100 = 19.0 <= 20.0.
            assertAverageStaysOnConfiguredScale(20.0, result)
            assertEquals(19.0, result.getValue()!!, 0.000001)
        }
    }

    @Test
    fun averageStaysOnConfiguredScale_numericMax10() {
        runBlocking {
            stubDefaultTypeGrade(max = 10)
            stubDatabaseAverage(percentage = 95.0)

            val result = repo.getAverageFromSemester(1)

            // 95.0 * 10 / 100 = 9.5 <= 10.0.
            assertAverageStaysOnConfiguredScale(10.0, result)
            assertEquals(9.5, result.getValue()!!, 0.000001)
        }
    }

    @Test
    fun averageStaysOnConfiguredScale_directPercentageBase100() {
        runBlocking {
            stubDefaultTypeGrade(max = 100, isDirectPercentage = true)
            stubDatabaseAverage(percentage = 95.0)

            val result = repo.getAverageFromSemester(1)

            // Direct percentage keeps the average on the 0..100 scale.
            assertAverageStaysOnConfiguredScale(100.0, result)
            assertEquals(95.0, result.getValue()!!, 0.000001)
        }
    }

    @Test
    fun averageStaysOnConfiguredScale_noEvaluatedGrades() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            stubDatabaseAverage(percentage = null)

            val result = repo.getAverageFromSemester(1)

            // A blank average is still bound to the configured scale and its minimum.
            assertAverageStaysOnConfiguredScale(20.0, result)
            assertEquals(10.0, result.getMinToPass(), 0.000001)
        }
    }

    @Test
    fun whenCourseAverageRounded_thenSemesterAverageIsWeightedByUcAfterConvertingEachCourseToTypeGradeScale() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            whenever(appConfigRepository.isRoundFinalCourseAverage()).thenReturn(true)
            whenever(courseDao.getAllCoursesFromSemesterId(1)).thenReturn(
                listOf(
                    course(id = 1, uc = 3, average = 58.0),
                    course(id = 2, uc = 1, average = 38.0),
                )
            )

            val result = repo.getAverageFromSemester(1)

            // Each course average is converted to the type-grade scale first: 60% -> 12.0, 40% -> 8.0.
            // Then each converted value is rounded individually: 12.0 and 8.0.
            // Weighted average by UC: (12*3 + 8*1) / (3 + 1) = 11.0.
            assertEquals(11.0, result.getValue()!!, 0.000001)
            assertEquals(20.0, result.getMax(), 0.000001)
        }
    }

    @Test
    fun whenCourseAverageNotRounded_thenSemesterAverageIsWeightedByUcUsingRawTypeGradeValues() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            whenever(appConfigRepository.isRoundFinalCourseAverage()).thenReturn(false)
            whenever(semesterDao.getAverageFromSemester(1)).thenReturn(50.0)

            val result = repo.getAverageFromSemester(1)

            // Without course rounding, the DAO average already represents the weighted percentage average
            // for the semester, and it is converted once to the configured grade scale.
            assertEquals(10.0, result.getValue()!!, 0.000001)
            assertEquals(20.0, result.getMax(), 0.000001)
        }
    }

    @Test
    fun whenCourseAverageRounded_thenRoundingOccursAfterTypeGradeConversionAndNotBefore() {
        runBlocking {
            stubDefaultTypeGrade(max = 20)
            whenever(appConfigRepository.isRoundFinalCourseAverage()).thenReturn(true)
            whenever(courseDao.getAllCoursesFromSemesterId(1)).thenReturn(
                listOf(
                    course(id = 1, uc = 2, average = 64.999),
                    course(id = 2, uc = 2, average = 60.0),
                )
            )

            val result = repo.getAverageFromSemester(1)

            // 64.999% -> 12.9998 on a 0..20 scale, then roundToInt() -> 13.
            // 60.0% -> 12.0, then roundToInt() -> 12.
            // Weighted: (13*2 + 12*2) / 4 = 12.5.
            assertEquals(12.5, result.getValue()!!, 0.000001)
        }
    }
}