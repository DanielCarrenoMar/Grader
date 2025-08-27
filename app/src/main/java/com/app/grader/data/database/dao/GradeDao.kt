package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.GradeEntity

@Dao
interface GradeDao {

    @Query("SELECT * FROM grade")
    suspend fun getAllGrades(): List<GradeEntity>

    @Query("SELECT * FROM grade WHERE course_id = :courseId")
    suspend fun getGradesFromCourseId(courseId: Int): List<GradeEntity>

    @Query("SELECT g.*\n" +
            "FROM course c\n" +
            "INNER JOIN grade g\n" +
            "ON g.course_id = c.id\n" +
            "WHERE ( (:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId )")
    suspend fun getGradesFromSemesterId(semesterId: Int?): List<GradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrade(grades: GradeEntity): Long

    @Query("UPDATE grade SET title = :title, description = :description, grade_percentage = :gradePercentage, percentage = :percentage  WHERE id = :gradeId")
    suspend fun updateGradeById(gradeId: Int, title: String, description: String, gradePercentage: Double, percentage: Double): Int

    @Query("DELETE FROM grade WHERE course_id = :courseId")
    suspend fun deleteAllGradesFromCourseId(courseId: Int): Int

    @Query("DELETE FROM grade")
    suspend fun deleteAllGrades(): Int

    @Query("DELETE FROM grade WHERE id = :gradeId")
    suspend fun deleteGradeFromId(gradeId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'grade'")
    suspend fun resetIncrementalGrade()

    @Query("SELECT * FROM grade WHERE id = :gradeId")
    suspend fun getGradeFromId(gradeId: Int): GradeEntity?
}