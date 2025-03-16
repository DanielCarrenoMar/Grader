package com.app.grader.domain.repository

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel

interface LocalStorageRepository {
    suspend fun saveCourse(courseModel: CourseModel): Boolean

    /**
     * Actualiza la informacion de una asignatura guiandose por el id
     */
    suspend fun updateCourse(courseModel: CourseModel): Boolean
    suspend fun getAllCourses(): List<CourseModel>

    /**
     * Si existe devuelve un CoruseModel sino devuelve null
     */
    suspend fun getCourseFromId(courseId: Int):CourseModel?
    suspend fun deleteAllCourses() : Int
    suspend fun getAverageFromCourse(courseId:Int) : Double
    suspend fun deleteCourseFromId(courseId: Int): Boolean
    suspend fun getGradesFromCourse(courseId: Int): List<GradeModel>
}