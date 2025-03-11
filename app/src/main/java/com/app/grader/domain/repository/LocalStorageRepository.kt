package com.app.grader.domain.repository

import com.app.grader.domain.model.CourseModel

interface LocalStorageRepository {
    suspend fun saveCourse(courseModel: CourseModel): Boolean
    suspend fun getAllCourses(): List<CourseModel>
    suspend fun deleteAllCourses() : Int
    suspend fun deleteCourse(courseModel: CourseModel): Int
    suspend fun calculateAverageGrade(courseId: Int): Float
}