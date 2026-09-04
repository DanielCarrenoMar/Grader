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
    @ColumnInfo(name = "max") val max: Int,
    @ColumnInfo(name = "min_to_pass") val minToPass: Double?,
)

@Dao
interface SubGradeDao {

    @Query("SELECT\n" +
            "    sg.id,\n" +
            "    sg.grade_id,\n" +
            "    sg.title,\n" +
            "    sg.grade_percentage,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass\n" +
            "FROM\n" +
            "    sub_grade sg\n" +
            "INNER JOIN grade g ON g.id = sg.grade_id\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "WHERE sg.grade_id = :gradeId")
    suspend fun getSubGradesFromGradeId(gradeId: Int): List<SubGradeCalculate>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubGrade(subGrade: SubGradeEntity): Long

    @Query("UPDATE sub_grade SET title = :title, grade_percentage = :gradePercentage  WHERE id = :subGradeId")
    suspend fun updateSubGradeById(subGradeId: Int, title: String, gradePercentage: Double?): Int

    @Query("DELETE FROM sub_grade WHERE grade_id = :gradeId")
    suspend fun deleteAllSubGradesFromGradeId(gradeId: Int): Int

    @Query("DELETE FROM sub_grade")
    suspend fun deleteAllSubGrades(): Int

    @Query("DELETE FROM sub_grade WHERE id = :subGradeId")
    suspend fun deleteSubGradeFromId(subGradeId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'sub_grade'")
    suspend fun resetIncrementalSubGrade()

    @Query("SELECT\n" +
            "    sg.id,\n" +
            "    sg.grade_id,\n" +
            "    sg.title,\n" +
            "    sg.grade_percentage,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass\n" +
            "FROM\n" +
            "    sub_grade sg\n" +
            "INNER JOIN grade g ON g.id = sg.grade_id\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "WHERE sg.id = :subGradeId")
    suspend fun getSubGradeFromId(subGradeId: Int): SubGradeCalculate?
}