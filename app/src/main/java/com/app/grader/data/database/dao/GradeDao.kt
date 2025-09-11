package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.GradeEntity

@Dao
interface GradeDao {

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.percentage,\n" +
            "    COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "GROUP BY\n" +
            "    g.id;")
    suspend fun getAllGrades(): List<GradeEntity>

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.percentage,\n" +
            "    COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE course_id = :courseId\n" +
            "GROUP BY\n" +
            "    g.id;\n")
    suspend fun getGradesFromCourseId(courseId: Int): List<GradeEntity>

    @Query("SELECT \n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.percentage,\n" +
            "    COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage\n" +
            "FROM \n" +
            "    course c\n" +
            "INNER JOIN \n" +
            "    grade g ON g.course_id = c.id\n" +
            "LEFT JOIN \n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE \n" +
            "   ( (:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId )\n" +
            "GROUP BY \n" +
            "    g.id;")
    suspend fun getGradesFromSemesterId(semesterId: Int?): List<GradeEntity>

    @Query("SELECT \n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.percentage,\n" +
            "    COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage\n" +
            "FROM \n" +
            "    course c\n" +
            "INNER JOIN \n" +
            "    grade g ON g.course_id = c.id\n" +
            "LEFT JOIN \n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE \n" +
            "   ( (:semesterId IS NULL AND semester_id NOT NULL) OR semester_id != :semesterId )\n" +
            "GROUP BY \n" +
            "    g.id;")
    suspend fun getGradesFromSemesterLessThanId(semesterId: Int?): List<GradeEntity>

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

    @Query("SELECT\n" +
            "    g.id,\n" +
            "    g.course_id,\n" +
            "    g.title,\n" +
            "    g.description,\n" +
            "    g.percentage,\n" +
            "    COALESCE(AVG(sg.grade_percentage), g.grade_percentage) AS grade_percentage\n" +
            "FROM\n" +
            "    grade g\n" +
            "LEFT JOIN\n" +
            "    sub_grade sg ON sg.grade_id = g.id\n" +
            "WHERE g.id = :gradeId\n" +
            "GROUP BY\n" +
            "    g.id;\n")
    suspend fun getGradeFromId(gradeId: Int): GradeEntity?
}