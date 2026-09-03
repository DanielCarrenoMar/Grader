package com.app.grader.infrastructure.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.infrastructure.database.entitites.GradeEntity

data class CalculatedGrade(
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "course_id") val courseId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "grade_value") val gradeValue: Double?,
    @ColumnInfo(name = "weighting_percentage") val weightingPercentage: Double,
    @ColumnInfo(name = "max") val max: Int,
    @ColumnInfo(name = "min_to_pass") val minToPass: Double?,
    @ColumnInfo(name = "is_direct_percentage") val isDirectPercentage: Boolean,
)

@Dao
interface GradeDao {

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "GROUP BY\n" +
            "    g.id\n" +
            "ORDER BY\n" +
            "    g.created_at DESC")
    suspend fun getAllGrades(): List<CalculatedGrade>

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE course_id = :courseId\n" +
            "GROUP BY\n" +
            "    g.id\n" +
            "ORDER BY\n" +
            "    g.created_at DESC")
    suspend fun getGradesFromCourseId(courseId: Int): List<CalculatedGrade>

    @Query("SELECT \n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM \n" +
            "    course c\n" +
            "INNER JOIN \n" +
            "    grade g ON g.course_id = c.id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN \n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE \n" +
            "   ( (:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId )\n" +
            "GROUP BY \n" +
            "    g.id\n" +
            "ORDER BY\n" +
            "    g.created_at DESC")
    suspend fun getGradesFromSemesterId(semesterId: Int?): List<CalculatedGrade>

    @Query("SELECT \n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM \n" +
            "    course c\n" +
            "INNER JOIN \n" +
            "    grade g ON g.course_id = c.id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN \n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE \n" +
            "   ( (:semesterId IS NULL AND semester_id NOT NULL) OR semester_id != :semesterId )\n" +
            "GROUP BY \n" +
            "    g.id\n" +
            "ORDER BY\n" +
            "    g.created_at DESC")
    suspend fun getGradesFromSemesterLessThanId(semesterId: Int?): List<CalculatedGrade>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGrade(grades: GradeEntity): Long

    @Query("UPDATE grade SET title = :title, description = :description, grade_percentage = :gradePercentage, weighting_percentage = :weightingPercentage WHERE id = :gradeId")
    suspend fun updateGradeById(gradeId: Int, title: String, description: String, gradePercentage: Double?, weightingPercentage: Double): Int

    @Query("DELETE FROM grade WHERE course_id = :courseId")
    suspend fun deleteAllGradesFromCourseId(courseId: Int): Int

    @Query("DELETE FROM grade")
    suspend fun deleteAllGrades(): Int

    @Query("DELETE FROM grade WHERE id = :gradeId")
    suspend fun deleteGradeFromId(gradeId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'grade'")
    suspend fun resetIncrementalGrade()

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE g.id = :gradeId\n" +
            "GROUP BY\n" +
            "    g.id;\n")
    suspend fun getGradeFromId(gradeId: Int): CalculatedGrade?

    @Query("SELECT COALESCE(SUM(weighting_percentage), 0.0) FROM grade WHERE course_id = :courseId")
    suspend fun getSumPercentageByCourseId(courseId: Int): Double?

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.weighting_percentage,\n" +
            "    (COALESCE(AVG(sg.grade_percentage), g.grade_percentage) * tg.max / 100.0) AS grade_value,\n" +
            "    tg.max,\n" +
            "    tg.min_to_pass,\n" +
            "    tg.is_direct_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "INNER JOIN course c ON c.id = g.course_id\n" +
            "INNER JOIN type_grade tg ON tg.id = c.type_grade_id\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE g.course_id = :courseId\n" +
            "GROUP BY\n" +
            "    g.id\n" +
            "ORDER BY g.created_at DESC, g.id DESC\n" +
            "LIMIT 1")
    suspend fun getLastGradeFromCourseId(courseId: Int): CalculatedGrade?
}