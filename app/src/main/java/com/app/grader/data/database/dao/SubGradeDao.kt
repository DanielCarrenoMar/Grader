package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.SubGradeEntity

@Dao
interface SubGradeDao {

    @Query("SELECT * FROM sub_grade WHERE grade_id = :gradeId")
    suspend fun getSubGradesFromGradeId(gradeId: Int): List<SubGradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubGrade(subGrade: SubGradeEntity): Long

    @Query("UPDATE sub_grade SET title = :title, grade = :grade  WHERE id = :subGradeId")
    suspend fun updateSubGradeById(subGradeId: Int, title: String, grade: Double): Int

    @Query("DELETE FROM sub_grade WHERE grade_id = :gradeId")
    suspend fun deleteAllSubGradesFromGradeId(gradeId: Int): Int

    @Query("DELETE FROM sub_grade")
    suspend fun deleteAllSubGrades(): Int

    @Query("DELETE FROM sub_grade WHERE id = :subGradeId")
    suspend fun deleteSubGradeFromId(subGradeId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'sub_grade'")
    suspend fun resetIncrementalSubGrade()

    @Query("SELECT * FROM sub_grade WHERE id = :subGradeId")
    suspend fun getSubGradeFromId(subGradeId: Int): SubGradeEntity?
}