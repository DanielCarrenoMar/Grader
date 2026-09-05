package com.app.grader.infrastructure.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.infrastructure.database.entitites.SubGradeEntity

data class SubGradeCalculate(
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "grade_id") val gradeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "grade_percentage") val gradePercentage: Double?,
)

@Dao
interface SubGradeDao {
    @Query("SELECT sg.id, sg.grade_id, sg.title, sg.grade_percentage FROM sub_grade sg WHERE sg.grade_id = :gradeId")
    suspend fun getSubGradesFromGradeId(gradeId: Int): List<SubGradeCalculate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubGrade(subGrade: SubGradeEntity): Long

    @Query("UPDATE sub_grade SET title = :title, grade_percentage = :gradePercentage WHERE id = :subGradeId")
    suspend fun updateSubGradeById(subGradeId: Int, title: String, gradePercentage: Double?): Int

    @Query("DELETE FROM sub_grade WHERE grade_id = :gradeId")
    suspend fun deleteAllSubGradesFromGradeId(gradeId: Int): Int

    @Query("DELETE FROM sub_grade")
    suspend fun deleteAllSubGrades(): Int

    @Query("DELETE FROM sub_grade WHERE id = :subGradeId")
    suspend fun deleteSubGradeFromId(subGradeId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'sub_grade'")
    suspend fun resetIncrementalSubGrade()

    @Query("SELECT sg.id, sg.grade_id, sg.title, sg.grade_percentage FROM sub_grade sg WHERE sg.id = :subGradeId")
    suspend fun getSubGradeFromId(subGradeId: Int): SubGradeCalculate?
}
