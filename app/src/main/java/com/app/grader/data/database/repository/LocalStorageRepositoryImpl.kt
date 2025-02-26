package com.app.grader.data.database.repository

import android.util.Log
import com.app.grader.data.database.dao.CourseDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.toCourseEntity
import com.app.grader.domain.repository.LocalStorageRepository
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao
) : LocalStorageRepository {
    override suspend fun saveCourse(courseModel: CourseModel): Boolean {
        try {
            Log.i("LocalStorageRepositoryImpl", "Guardando Course: ${courseModel.title}, Description: ${courseModel.description}, UC: ${courseModel.uc}")
            val result = courseDao.insertCourse(courseModel.toCourseEntity())
            return result.toInt() != -1
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
                    uc = courseEntity.uc
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }
}