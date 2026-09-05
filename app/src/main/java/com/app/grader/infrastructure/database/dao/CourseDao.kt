package com.app.grader.infrastructure.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.infrastructure.database.entitites.CourseEntity

data class CalculatedCourse(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "semester_id") val semesterId: Int?,
    @ColumnInfo(name = "type_grade_id") val typeGradeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "uc") val uc: Int,
    @ColumnInfo(name = "average") val average: Double?,
    @ColumnInfo(name = "total_weighting_percentage") val totalWeightingPercentage: Double?,
)

data class CourseAverage(@ColumnInfo(name = "average") val average: Double?)

data class CourseStatistics(
    @ColumnInfo(name = "total_percentage") val totalPercentage: Double,
    @ColumnInfo(name = "accumulate_points") val accumulatePoints: Double,
    @ColumnInfo(name = "evaluated_percentage") val evaluatedPercentage: Double,
)

@Dao
interface CourseDao {
    @Query("SELECT c.id, c.semester_id, c.type_grade_id, c.title, c.uc, (SUM(grade_avg * g_agg.weighting_percentage) / NULLIF(SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END), 0)) AS average, SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END) AS total_weighting_percentage FROM course c LEFT JOIN (SELECT g.id, g.course_id, g.weighting_percentage, g.grade_percentage, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_avg FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id GROUP BY g.id) AS g_agg ON g_agg.course_id = c.id GROUP BY c.id ORDER BY c.id DESC")
    suspend fun getAllCourses(): List<CalculatedCourse>

    @Query("SELECT c.id, c.semester_id, c.type_grade_id, c.title, c.uc, (SUM(grade_avg * g_agg.weighting_percentage) / NULLIF(SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END), 0)) AS average, SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END) AS total_weighting_percentage FROM course c LEFT JOIN (SELECT g.id, g.course_id, g.weighting_percentage, g.grade_percentage, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_avg FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id GROUP BY g.id) AS g_agg ON g_agg.course_id = c.id WHERE ((:semesterId IS NULL AND c.semester_id IS NULL) OR c.semester_id = :semesterId) GROUP BY c.id ORDER BY c.id DESC")
    suspend fun getAllCoursesFromSemesterId(semesterId: Int?): List<CalculatedCourse>

    @Query("SELECT c.id, c.semester_id, c.type_grade_id, c.title, c.uc, (SUM(grade_avg * g_agg.weighting_percentage) / NULLIF(SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END), 0)) AS average, SUM(CASE WHEN g_agg.grade_percentage IS NOT NULL THEN g_agg.weighting_percentage ELSE 0 END) AS total_weighting_percentage FROM course c LEFT JOIN (SELECT g.id, g.course_id, g.weighting_percentage, g.grade_percentage, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_avg FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id GROUP BY g.id) AS g_agg ON g_agg.course_id = c.id WHERE c.id = :courseId GROUP BY c.id")
    suspend fun getCourseFromId(courseId: Int): CalculatedCourse?

    @Query("SELECT (SUM(sub.grade_avg * sub.weighting_percentage) / NULLIF(SUM(sub.weighting_percentage), 0)) AS average FROM (SELECT g.id, g.course_id, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_avg, g.weighting_percentage FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE g.course_id = :courseId AND g.grade_percentage IS NOT NULL GROUP BY g.id) sub GROUP BY sub.course_id")
    suspend fun getAverageFromCourse(courseId: Int): CourseAverage

    @Query("SELECT SUM(weighting_percentage) FROM grade WHERE course_id = :courseId")
    suspend fun getTotalPercentageFromCourse(courseId: Int): Double?

    @Query("SELECT COALESCE(SUM(base.weighting_percentage), 0.0) AS total_percentage, COALESCE(SUM(CASE WHEN base.grade_value IS NOT NULL THEN (base.weighting_percentage / 100.0) * base.grade_value ELSE 0.0 END), 0.0) AS accumulate_points, COALESCE(SUM(CASE WHEN base.grade_value IS NOT NULL THEN base.weighting_percentage ELSE 0.0 END), 0.0) AS evaluated_percentage FROM (SELECT g.weighting_percentage, COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_value FROM grade g LEFT JOIN sub_grade sg ON sg.grade_id = g.id WHERE g.course_id = :courseId GROUP BY g.id) AS base")
    suspend fun getCourseStatistics(courseId: Int): CourseStatistics

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Query("UPDATE course SET title = :title, uc = :uc, type_grade_id = :typeGradeId WHERE id = :courseId")
    suspend fun updateCourseById(courseId: Int, title: String, uc: Int, typeGradeId: Int): Int

    @Query("UPDATE course SET type_grade_id = :typeGradeId")
    suspend fun updateTypeGradeForAllCourses(typeGradeId: Int): Int

    @Query("DELETE FROM course")
    suspend fun deleteAllCourses(): Int

    @Query("DELETE FROM course WHERE ((:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId)")
    suspend fun deleteAllCoursesFromSemesterId(semesterId: Int?): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'course'")
    suspend fun resetIncrementalCourse()

    @Query("DELETE FROM course WHERE id = :courseId")
    suspend fun deleteCourseFromId(courseId: Int): Int
}
