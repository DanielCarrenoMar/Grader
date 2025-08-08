package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.CourseEntity

@Dao
interface CourseDao {

    @Query("SELECT * FROM course")
    suspend fun getAllCourses(): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Query("UPDATE course SET title = :title, uc = :uc WHERE id = :courseId")
    suspend fun updateCourseById(courseId: Int, title: String, uc: Int): Int

    @Query("DELETE FROM course")
    suspend fun deleteAllCourses(): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'course'")
    suspend fun resetIncrementalCourse()

    @Query("DELETE FROM course WHERE id = :courseId")
    suspend fun deleteCourseFromId(courseId: Int): Int

    @Query("SELECT * FROM course WHERE id = :courseId")
    suspend fun getCourseFromId(courseId: Int): CourseEntity?
}