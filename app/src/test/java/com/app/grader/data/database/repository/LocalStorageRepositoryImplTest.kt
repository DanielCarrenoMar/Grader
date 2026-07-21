package com.app.grader.data.database.repository

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.data.appConfig.AppConfigRepository
import com.app.grader.data.database.dao.CourseDao
import com.app.grader.data.database.dao.GradeDao
import com.app.grader.data.database.dao.SemesterDao
import com.app.grader.data.database.dao.SubGradeDao
import com.app.grader.data.database.dao.TypeGradeDao
import com.app.grader.data.database.entitites.GradeEntity
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for percentage-sum validation in [LocalStorageRepositoryImpl].
 *
 * Contract: `saveGrade` and `updateGrade` MUST refuse to persist a grade when
 * the resulting per-course weighting sum would exceed 100%. The error surface
 * is `IllegalArgumentException` with the exact (Spanish) message
 * "La suma de las notas excede el 100%".
 */
class LocalStorageRepositoryImplTest {

    private val semesterDao: SemesterDao = mock()
    private val courseDao: CourseDao = mock()
    private val gradeDao: GradeDao = mock()
    private val subGradeDao: SubGradeDao = mock()
    private val typeGradeDao: TypeGradeDao = mock()
    private val gradeFactory: GradeFactory = mock()
    private val appConfigRepository: AppConfigRepository = mock()

    private val repo = LocalStorageRepositoryImpl(
        semesterDao = semesterDao,
        courseDao = courseDao,
        gradeDao = gradeDao,
        subGradeDao = subGradeDao,
        typeGradeDao = typeGradeDao,
        gradeFactory = gradeFactory,
        appConfigRepository = appConfigRepository,
    )

    private fun gradeModel(percentage: Double, id: Int = -1, courseId: Int = 1): GradeModel =
        GradeModel(
            courseId = courseId,
            title = "Quiz",
            description = "",
            grade = Grade(15.0, 9.5, 20),
            percentage = Percentage(percentage),
            id = id,
        )

    private fun currentGradeEntity(weightingPercentage: Double): GradeEntity =
        GradeEntity(
            id = 7,
            courseId = 1,
            title = "Midterm",
            description = "",
            gradePercentage = 15.0,
            weightingPercentage = weightingPercentage,
        )

    @Test
    fun saveGrade_throwsWhenSumPlusNewExceeds100() {
        runBlocking {
            whenever(gradeDao.getSumPercentageByCourseId(1)).thenReturn(80.0)

            val model = gradeModel(percentage = 30.0) // 80 + 30 = 110 > 100

            val ex = assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repo.saveGrade(model) }
            }
            assertEquals("La suma de las notas excede el 100%", ex.message)
            verify(gradeDao, never()).insertGrade(any())
        }
    }

    @Test
    fun saveGrade_succeedsWhenSumPlusNewAtMost100() {
        runBlocking {
            whenever(gradeDao.getSumPercentageByCourseId(1)).thenReturn(60.0)
            whenever(gradeDao.insertGrade(any())).thenReturn(42L)

            val model = gradeModel(percentage = 40.0) // 60 + 40 = 100 OK
            val result = repo.saveGrade(model)

            assertEquals(42L, result)
            verify(gradeDao).insertGrade(any())
        }
    }

    @Test
    fun updateGrade_excludesCurrentGradeFromSum() {
        runBlocking {
            // Existing grades on the course: 60 + 60 = 120 (overweighted).
            // The current grade being updated has percentage 60.
            // Excluding it: 120 - 60 = 60. New percentage 40 → 60 + 40 = 100 OK.
            whenever(gradeDao.getSumPercentageByCourseId(1)).thenReturn(120.0)
            whenever(gradeDao.getGradeFromId(7)).thenReturn(currentGradeEntity(weightingPercentage = 60.0))
            whenever(gradeDao.updateGradeById(eq(7), any(), any(), any(), any())).thenReturn(1)

            val model = gradeModel(percentage = 40.0, id = 7, courseId = 1)
            val result = repo.updateGrade(model)

            assertEquals(true, result)
            verify(gradeDao).updateGradeById(eq(7), any(), any(), any(), eq(40.0))
        }
    }

    @Test
    fun updateGrade_throwsWhenWithoutCurrentPlusNewExceeds100() {
        runBlocking {
            // Other grades on the course sum to 80. Current grade is 10.
            // Excluding current: 80 - 10 = 70. New 40 → 70 + 40 = 110 > 100.
            whenever(gradeDao.getSumPercentageByCourseId(1)).thenReturn(80.0)
            whenever(gradeDao.getGradeFromId(7)).thenReturn(currentGradeEntity(weightingPercentage = 10.0))

            val model = gradeModel(percentage = 40.0, id = 7, courseId = 1)

            val ex = assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repo.updateGrade(model) }
            }
            assertEquals("La suma de las notas excede el 100%", ex.message)
            verify(gradeDao, never()).updateGradeById(any(), any(), any(), any(), any())
        }
    }
}
