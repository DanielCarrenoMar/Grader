package com.app.grader.data.database.repository

import android.util.Log
import com.app.grader.data.database.dao.CourseDao
import com.app.grader.data.database.dao.GradeDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.toCourseEntity
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
                courseModel.description,
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
                    description = courseEntity.description,
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
                    description = courseEntity.description,
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
            return courseDao.deleteCourseFromId(courseId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAverageFromCourse(courseId:Int): Double {
        try {
            val grades = gradeDao.getAllGrades()
                .filter { gradeEntity -> gradeEntity.courseId == courseId }
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
            return gradeDao.getAllGrades().filter { it.courseId == courseId }.map { gradeEntity ->
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
}