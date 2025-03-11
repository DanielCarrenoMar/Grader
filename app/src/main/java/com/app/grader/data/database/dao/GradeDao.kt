package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.GradeEntity

@Dao
interface GradeDao {

    @Query("SELECT * FROM grade WHERE course_id = :courseId")
    suspend fun getGradesByCourseId(courseId: Int): List<GradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grades: List<GradeEntity>)

    @Query("DELETE FROM grade")
    suspend fun deleteAllGrades(): Int
}