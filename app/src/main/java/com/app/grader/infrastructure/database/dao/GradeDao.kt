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
    @ColumnInfo(name = "grade_percentage") val gradePercentage: Double?,
    @ColumnInfo(name = "weighting_percentage") val weightingPercentage: Double,
)

@Dao
interface GradeDao {
    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id GROUP BY g.id ORDER BY g.created_at DESC")
    suspend fun getAllGrades(): List<CalculatedGrade>

    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE g.course_id = :courseId GROUP BY g.id ORDER BY g.created_at DESC")
    suspend fun getGradesFromCourseId(courseId: Int): List<CalculatedGrade>

    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM course c INNER JOIN grade g ON g.course_id = c.id LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE ((:semesterId IS NULL AND c.semester_id IS NULL) OR c.semester_id = :semesterId) GROUP BY g.id ORDER BY g.created_at DESC")
    suspend fun getGradesFromSemesterId(semesterId: Int?): List<CalculatedGrade>

    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM course c INNER JOIN grade g ON g.course_id = c.id LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE ((:semesterId IS NULL AND c.semester_id IS NOT NULL) OR c.semester_id != :semesterId) GROUP BY g.id ORDER BY g.created_at DESC")
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

    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE g.id = :gradeId GROUP BY g.id")
    suspend fun getGradeFromId(gradeId: Int): CalculatedGrade?

    @Query("SELECT COALESCE(SUM(weighting_percentage), 0.0) FROM grade WHERE course_id = :courseId")
    suspend fun getSumPercentageByCourseId(courseId: Int): Double?

    @Query("SELECT g.id, g.course_id, g.title, g.description, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage, g.weighting_percentage FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE g.course_id = :courseId GROUP BY g.id ORDER BY g.created_at DESC, g.id DESC LIMIT 1")
    suspend fun getLastGradeFromCourseId(courseId: Int): CalculatedGrade?
}
