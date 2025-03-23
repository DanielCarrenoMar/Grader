package com.app.grader.data.database.repository

import com.app.grader.data.database.dao.CourseDao
import com.app.grader.data.database.dao.GradeDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.toCourseEntity
import com.app.grader.domain.model.toGradeEntity
import com.app.grader.domain.repository.LocalStorageRepository
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val gradeDao: GradeDao,
) : LocalStorageRepository {
    override suspend fun saveCourse(courseModel: CourseModel): Boolean {
        try {
            val result = courseDao.insertCourse(courseModel.toCourseEntity())
            return result.toInt() != -1
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
                    id = courseEntity.id
                )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllCourses(): Int {
        try {
            courseDao.resetIncremetalCourse()
            return courseDao.deleteAllCourses()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteCourseFromId(courseId: Int): Boolean {
        try {
            gradeDao.deleteGradeFromCourseId(courseId)
            return courseDao.deleteCourseFromId(courseId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAverageFromCourse(courseId:Int): Double {
        try {
            val grades = gradeDao.getGradesFromCourseId(courseId)
            if (grades.isEmpty()) return 0.0

            val totalWeight = grades.sumOf { it.percentage }
            val weightedAverage = grades.sumOf { it.grade * it.percentage } / totalWeight

            return weightedAverage
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradesFromCourse(courseId: Int): List<GradeModel> {
        try {
            return gradeDao.getGradesFromCourseId(courseId).map { gradeEntity ->
                GradeModel(
                    id = gradeEntity.id,
                    courseId = gradeEntity.courseId,
                    title = gradeEntity.title,
                    description = gradeEntity.description,
                    grade = gradeEntity.grade,
                    percentage = gradeEntity.percentage
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveGrade(gradeModel: GradeModel): Boolean {
        try {
            val result = gradeDao.insertGrade(gradeModel.toGradeEntity())
            return result.toInt() != -1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGradesFromCourseId(courseId: Int): Int {
        try {
            return gradeDao.deleteAllGradesFromCourseId(courseId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGrades(): Int {
        try {
            gradeDao.resetIncremetalGrade()
            return gradeDao.deleteAllGrades()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteGradeFromId(gradeId: Int): Boolean {
        try {
            return gradeDao.deleteGradeFromId(gradeId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllGrades(): List<GradeModel> {
        try {
            return gradeDao.getAllGrades().map { gradeEntity ->
                GradeModel(
                    title = gradeEntity.title,
                    description = gradeEntity.description,
                    grade = gradeEntity.grade,
                    percentage = gradeEntity.percentage,
                    id = gradeEntity.id,
                    courseId = gradeEntity.courseId,
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradeFromId(gradeId: Int): GradeModel? {
        try {
            val gradeEntity = gradeDao.getGradeFromId(gradeId) ?: return null
            return GradeModel(
                title = gradeEntity.title,
                description = gradeEntity.description,
                grade = gradeEntity.grade,
                percentage = gradeEntity.percentage,
                id = gradeEntity.id,
                courseId = gradeEntity.courseId,
            )
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
                gradeModel.grade,
                gradeModel.percentage
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }
}