package com.app.grader.infrastructure.database.repository

import com.app.grader.infrastructure.database.dao.CalculatedCourse
import com.app.grader.infrastructure.database.dao.CalculatedGrade
import com.app.grader.infrastructure.database.dao.CourseDao
import com.app.grader.infrastructure.database.dao.CourseStatistics
import com.app.grader.infrastructure.database.dao.GradeDao
import com.app.grader.infrastructure.database.dao.SemesterDao
import com.app.grader.infrastructure.database.dao.SubGradeDao
import com.app.grader.infrastructure.database.dao.TypeGradeDao
import com.app.grader.infrastructure.database.entitites.TypeGradeEntity
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.types.GradeValue
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
    private val appConfigRepository: AppConfigRepository = mock()

    private val repo = LocalStorageRepositoryImpl(
        semesterDao = semesterDao,
        courseDao = courseDao,
        gradeDao = gradeDao,
        subGradeDao = subGradeDao,
        typeGradeDao = typeGradeDao,
        appConfigRepository = appConfigRepository,
    )

    private fun gradeModel(percentage: Double, id: Int = -1, courseId: Int = 1): GradeModel =
        GradeModel(
            courseId = courseId,
            title = "Quiz",
            description = "",
            gradeValueRaw = 15.0,
            typeGradeModel = TypeGradeModel(id = 0, title = "", max = 20, minToPass = 9.5, isFromSystem = false, isDirectPercentage = false),
            weight = Percentage(percentage),
            id = id,
        )

    private fun currentGradeEntity(weightingPercentage: Double): CalculatedGrade =
        CalculatedGrade(
            id = 7,
            courseId = 1,
            title = "Midterm",
            description = "",
            gradePercentage = 75.0,
            weightingPercentage = weightingPercentage
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

    @Test
    fun getCourseById_resolvesAndStoresCourseTypeGradeModel() {
        runBlocking {
            whenever(courseDao.getCourseFromId(1)).thenReturn(
                CalculatedCourse(
                    id = 1,
                    semesterId = null,
                    typeGradeId = 1,
                    title = "Course",
                    uc = 4,
                    average = 75.0,
                    totalWeightingPercentage = 50.0,
                )
            )
            whenever(typeGradeDao.getTypeGradeById(1)).thenReturn(
                typeGradeEntity(max = 20, isDirectPercentage = false)
            )

            val result = repo.getCourseById(1)

            assertEquals(TypeGradeModel(id = 1, title = "Test", max = 20, minToPass = null, isFromSystem = false, isDirectPercentage = false), result?.typeGradeModel)
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
}
