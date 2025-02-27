package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.CourseEntity

@Dao
interface CourseDao {

    @Query("SELECT * FROM course")
    suspend fun getAllCourses():List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course:CourseEntity):Long

    @Query("DELETE FROM course")
    suspend fun deleteAllCourses():Int
}