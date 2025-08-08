package com.app.grader.data.database.repository

import com.app.grader.data.database.dao.CourseDao
import com.app.grader.data.database.dao.GradeDao
import com.app.grader.data.database.dao.SubGradeDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.model.toCourseEntity
import com.app.grader.domain.model.toGradeEntity
import com.app.grader.domain.model.toGradeModel
import com.app.grader.domain.model.toSubGradeEntity
import com.app.grader.domain.model.toSubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val gradeDao: GradeDao,
    private val subGradeDao: SubGradeDao,
) : LocalStorageRepository {
    override suspend fun saveCourse(courseModel: CourseModel): Long {
        try {
            return courseDao.insertCourse(courseModel.toCourseEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateCourse(courseModel: CourseModel): Boolean {
        try {
            val result = courseDao.updateCourseById(
                courseModel.id,
                courseModel.title,
                courseModel.uc
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllCourses(): List<CourseModel> {
        try {
            return courseDao.getAllCourses().map { courseEntity ->
                CourseModel(
                    title = courseEntity.title,
                    uc = courseEntity.uc,
                    average = getAverageFromCourse(courseEntity.id),
                    totalPercentage = getTotalPercentageFromCourse(courseEntity.id),
                    id = courseEntity.id
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCourseFromId(courseId: Int): CourseModel? {
        try {
            val courseEntity = courseDao.getCourseFromId(courseId) ?: return null
            return CourseModel(
                    title = courseEntity.title,
                    uc = courseEntity.uc,
                    average = getAverageFromCourse(courseEntity.id),
                    totalPercentage = getTotalPercentageFromCourse(courseEntity.id),
                    id = courseEntity.id
                )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllCourses(): Int {
        try {
            courseDao.resetIncrementalCourse()
            return courseDao.deleteAllCourses()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteCourseFromId(courseId: Int): Boolean {
        try {
            deleteAllGradesFromCourseId(courseId)
            return courseDao.deleteCourseFromId(courseId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAverageFromCourse(courseId:Int): Grade {
        try {
            val grades = getGradesFromCourse(courseId)
            val filteredGrades = grades.filter { it.grade.isNotBlank() }
            if (filteredGrades.isEmpty()) return Grade()

            val totalWeight = filteredGrades.sumOf { it.percentage.getPercentage() }
            val weightedAverage = filteredGrades.sumOf { it.grade.getGrade() * it.percentage.getPercentage() } / totalWeight

            return Grade(weightedAverage)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getTotalPercentageFromCourse(courseId: Int): Percentage {
        try {
            val grades = getGradesFromCourse(courseId)
            if (grades.isEmpty()) return Percentage()
            val totalPercentage = grades.sumOf { it.percentage.getPercentage() }
            return Percentage(totalPercentage)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradesFromCourse(courseId: Int): List<GradeModel> {
        try {
            return gradeDao.getGradesFromCourseId(courseId).map { gradeEntity ->
                gradeEntity.toGradeModel()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveGrade(gradeModel: GradeModel): Long {
        try {
            return gradeDao.insertGrade(gradeModel.toGradeEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGradesFromCourseId(courseId: Int): Int {
        try {
            gradeDao.getGradesFromCourseId(courseId).forEach { grade ->
                subGradeDao.deleteAllSubGradesFromGradeId(grade.id)
            }
            return gradeDao.deleteAllGradesFromCourseId(courseId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGrades(): Int {
        try {
            gradeDao.resetIncrementalGrade()
            return gradeDao.deleteAllGrades()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteGradeFromId(gradeId: Int): Boolean {
        try {
            subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
            return gradeDao.deleteGradeFromId(gradeId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllGrades(): List<GradeModel> {
        try {
            return gradeDao.getAllGrades().map { gradeEntity ->
                gradeEntity.toGradeModel()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradeFromId(gradeId: Int): GradeModel? {
        try {
            val gradeEntity = gradeDao.getGradeFromId(gradeId) ?: return null
            return gradeEntity.toGradeModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateGrade(gradeModel: GradeModel): Boolean {
        try {
            val result = gradeDao.updateGradeById(
                gradeModel.id,
                gradeModel.title,
                gradeModel.description,
                gradeModel.grade.getGrade(),
                gradeModel.percentage.getPercentage()
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubGradesFromGrade(gradeId: Int): List<SubGradeModel> {
        try {
            return subGradeDao.getSubGradesFromGradeId(gradeId).map { subGradeEntity ->
                subGradeEntity.toSubGradeModel()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSubGrade(subGradeModel: SubGradeModel): Long {
        try {
            return subGradeDao.insertSubGrade(subGradeModel.toSubGradeEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSubGrades(): Int {
        try {
            subGradeDao.resetIncrementalSubGrade()
            return subGradeDao.deleteAllSubGrades()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSubGradesFromGradeId(gradeId: Int): Int {
        try {
            return subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSubGradeFromId(subGradeId: Int): Boolean {
        try {
            return subGradeDao.deleteSubGradeFromId(subGradeId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubGradeFromId(subGradeId: Int): SubGradeModel? {
        try {
            val subGradeEntity = subGradeDao.getSubGradeFromId(subGradeId) ?: return null
            return subGradeEntity.toSubGradeModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateSubGrade(subGradeModel: SubGradeModel): Boolean {
        try {
            val result = subGradeDao.updateSubGradeById(
                subGradeModel.id,
                subGradeModel.title,
                subGradeModel.grade.getGrade(),
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }
}