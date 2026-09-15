package com.app.grader.infrastructure.database.repository

import com.app.grader.infrastructure.database.dao.CourseDao
import com.app.grader.infrastructure.database.dao.CourseStatistics
import com.app.grader.infrastructure.database.dao.GradeDao
import com.app.grader.infrastructure.database.dao.SemesterDao
import com.app.grader.infrastructure.database.dao.SubGradeDao
import com.app.grader.infrastructure.database.dao.TypeGradeDao
import com.app.grader.infrastructure.database.entitites.TypeGradeEntity
import com.app.grader.domain.repository.AppConfigRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [LocalStorageRepositoryImpl.getCourseStatistics].
 *
 * Contract: the repository response must be derived from the [CourseStatistics] row returned by
 * the database (CourseDao.getCourseStatistics) plus the grade type configured on the course:
 * - Numeric grade type (`isDirectPercentage = false`): accumulated points are scaled from the
 *   percentage scale (0-100) to the grade-type scale (0..max) via `accumulated * max / 100`.
 * - Direct percentage grade type (`isDirectPercentage = true`): the max is treated as 100, so
 *   accumulated points stay on the 0-100 percentage scale unchanged.
 * - `pendingPoints` is always `(100 - evaluatedPercentage) * max / 100`, so it is expressed on the
 *   same grade scale as `accumulatePoints` (base 100 for direct percentage, base `max` otherwise).
 * - `totalPercentage` passes through the database total weighting percentage unchanged.
 * - When no grade type is configured on the course the repository fails with
 *   [IllegalStateException].
 */
class CourseStatisticsRepositoryTest {

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

    private fun typeGradeEntity(max: Int, isDirectPercentage: Boolean): TypeGradeEntity =
        TypeGradeEntity(
            id = 1,
            title = "Test",
            max = max,
            minToPass = null,
            isFromSystem = false,
            isDirectPercentage = isDirectPercentage,
            active = true,
        )

    private fun dbStatistics(
        totalPercentage: Double,
        accumulatePoints: Double,
        evaluatedPercentage: Double,
    ): CourseStatistics = CourseStatistics(
        totalPercentage = totalPercentage,
        accumulatePoints = accumulatePoints,
        evaluatedPercentage = evaluatedPercentage,
    )

    private suspend fun stubDatabaseStatistics(courseId: Int = 1, stats: CourseStatistics) {
        whenever(courseDao.getCourseStatistics(courseId)).thenReturn(stats)
    }

    @Test
    fun numericTypeGrade_scalesAccumulatePointsFromPercentageToGradeScale() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // 45.0 * 20 / 100 = 9.0 points on the 0..20 numeric scale.
            assertEquals(9.0, result.accumulatePoints, 0.000001)
            // (100 - 60) * 20 / 100 = 8.0 remaining points on the 0..20 numeric scale.
            assertEquals(8.0, result.pendingPoints, 0.000001)
            assertEquals(60.0, result.totalPercentage.getPercentage(), 0.000001)
            verify(courseDao).getCourseStatistics(1)
        }
    }

    @Test
    fun numericTypeGrade_max10_scalesAccumulatePointsProportionally() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 10, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // 45.0 * 10 / 100 = 4.5 points on the 0..10 numeric scale.
            assertEquals(4.5, result.accumulatePoints, 0.000001)
            // (100 - 60) * 10 / 100 = 4.0 remaining points on the 0..10 numeric scale.
            assertEquals(4.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun numericTypeGrade_max100_keepsPercentageScale() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 100, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // 45.0 * 100 / 100 = 45.0: a numeric scale of 100 matches the percentage scale.
            assertEquals(45.0, result.accumulatePoints, 0.000001)
        }
    }

    @Test
    fun directPercentageTypeGrade_keepsAccumulatePointsUnscaled() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 100, isDirectPercentage = true)
            )

            val result = repo.getCourseStatistics(1)

            // Direct percentage: max is treated as 100, so points are not rescaled.
            assertEquals(45.0, result.accumulatePoints, 0.000001)
            assertEquals(40.0, result.pendingPoints, 0.000001)
            assertEquals(60.0, result.totalPercentage.getPercentage(), 0.000001)
        }
    }

    @Test
    fun fullyEvaluatedCourse_pendingPointsIsZero() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 100.0, accumulatePoints = 50.0, evaluatedPercentage = 100.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            assertEquals(10.0, result.accumulatePoints, 0.000001) // 50.0 * 20 / 100
            assertEquals(0.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun noGrades_zeroStatistics_yieldZeroAccumulatedAndFullPendingInGradeScale() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 0.0, accumulatePoints = 0.0, evaluatedPercentage = 0.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            assertEquals(0.0, result.accumulatePoints, 0.000001)
            assertEquals(20.0, result.pendingPoints, 0.000001)
            assertEquals(0.0, result.totalPercentage.getPercentage(), 0.000001)
        }
    }

    // -------------------------------------------------------------------------------------------
    // Group: accumulated AND pending points must share the configured base.
    //
    // Both values must live on the same scale as the configured grade type: 0..max for a numeric
    // type (isDirectPercentage = false) and 0..100 for a direct percentage type. Together they may
    // never exceed that base, because the UI donut renders them against cap = max.
    //
    // Regression group for the bug where pendingPoints stayed on base 100 when a numeric type
    // grade (e.g. max = 20) was configured, so the donut sections exceeded the circle cap.
    // -------------------------------------------------------------------------------------------
    private fun assertPointsShareConfiguredBase(
        max: Double,
        accumulatePoints: Double,
        pendingPoints: Double,
    ) {
        val delta = 0.000001
        assertTrue(
            "accumulatePoints must stay inside the configured base 0..$max",
            accumulatePoints >= 0.0 && accumulatePoints <= max + delta
        )
        assertTrue(
            "pendingPoints must stay inside the configured base 0..$max",
            pendingPoints >= 0.0 && pendingPoints <= max + delta
        )
        assertTrue(
            "accumulatePoints + pendingPoints must not exceed the configured base $max",
            accumulatePoints + pendingPoints <= max + delta
        )
    }

    @Test
    fun pointsShareConfiguredBase_numericMax20() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // Both values are on the 0..20 scale: 9.0 accumulated + 8.0 pending = 17.0 <= 20.0.
            assertPointsShareConfiguredBase(20.0, result.accumulatePoints, result.pendingPoints)
            assertEquals(9.0, result.accumulatePoints, 0.000001)
            assertEquals(8.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun pointsShareConfiguredBase_numericMax10() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 10, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // Both values are on the 0..10 scale: 4.5 accumulated + 4.0 pending = 8.5 <= 10.0.
            assertPointsShareConfiguredBase(10.0, result.accumulatePoints, result.pendingPoints)
            assertEquals(4.5, result.accumulatePoints, 0.000001)
            assertEquals(4.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun pointsShareConfiguredBase_directPercentageBase100() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 100, isDirectPercentage = true)
            )

            val result = repo.getCourseStatistics(1)

            // Direct percentage keeps both values on the 0..100 scale.
            assertPointsShareConfiguredBase(100.0, result.accumulatePoints, result.pendingPoints)
            assertEquals(45.0, result.accumulatePoints, 0.000001)
            assertEquals(40.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun pointsShareConfiguredBase_fullyEvaluatedCourseStaysInsideBase() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 100.0, accumulatePoints = 100.0, evaluatedPercentage = 100.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseStatistics(1)

            // 100% evaluated: accumulate hits the configured max (20.0) and pending is 0.0.
            assertPointsShareConfiguredBase(20.0, result.accumulatePoints, result.pendingPoints)
            assertEquals(20.0, result.accumulatePoints, 0.000001)
            assertEquals(0.0, result.pendingPoints, 0.000001)
        }
    }

    @Test
    fun missingTypeGrade_throwsIllegalStateException() {
        runBlocking {
            stubDatabaseStatistics(
                stats = dbStatistics(totalPercentage = 60.0, accumulatePoints = 45.0, evaluatedPercentage = 60.0)
            )
            whenever(typeGradeDao.getTypeGradeFromCourseId(1)).thenReturn(null)

            assertThrows(IllegalStateException::class.java) {
                runBlocking { repo.getCourseStatistics(1) }
            }
        }
    }
}