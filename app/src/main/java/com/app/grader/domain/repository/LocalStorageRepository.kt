package com.app.grader.domain.repository

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel

interface LocalStorageRepository {
    suspend fun saveCourse(courseModel: CourseModel): Boolean
    suspend fun getAllCourses(): List<CourseModel>
    suspend fun getCourseFromId(courseId: Int):CourseModel
    suspend fun deleteAllCourses() : Int
    suspend fun getAverageFromCourse(courseId:Int) : Double
    suspend fun deleteCourse(courseModel: CourseModel): Int
    suspend fun getGradesFromCourse(courseId: Int): List<GradeModel>
}