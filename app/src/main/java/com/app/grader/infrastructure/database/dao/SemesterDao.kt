package com.app.grader.infrastructure.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.infrastructure.database.entitites.SemesterEntity

data class SemesterStatistics(
    @ColumnInfo(name = "totalCourses") val totalCourses: Int,
    @ColumnInfo(name = "totalWeight") val totalWeight: Int,
    @ColumnInfo(name = "totalAverage") val totalAverage: Double?,
)

@Dao
interface SemesterDao {

    @Query("SELECT * FROM semester ORDER BY id DESC")
    suspend fun getAllSemesters(): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSemester(semester: SemesterEntity): Long

    @Query("UPDATE semester SET title = :title  WHERE id = :semesterId")
    suspend fun updateSemesterById(semesterId: Int, title: String): Int

    @Query("DELETE FROM semester")
    suspend fun deleteAllSemesters(): Int

    @Query("DELETE FROM semester WHERE id = :semesterId")
    suspend fun deleteSemesterById(semesterId: Int): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'semester'")
    suspend fun resetIncrementalSemester()

    @Query("SELECT * FROM semester WHERE id = :semesterId")
    suspend fun getSemesterById(semesterId: Int): SemesterEntity?

    @Query("SELECT COUNT(*) FROM course WHERE ( (:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId )")
    suspend fun getCoursesCountById(semesterId: Int?): Int

    @Query("SELECT SUM(uc) FROM course WHERE ( (:semesterId IS NULL AND semester_id IS NULL) OR semester_id = :semesterId )")
    suspend fun getSemesterUCSum(semesterId: Int?): Int

    @Query(
        "SELECT " +
            "SUM(course_average * uc) / SUM(uc) " +
            "FROM (" +
                "SELECT c.id AS course_id, c.uc AS uc, " +
                "SUM((COALESCE(g.grade_percentage, 0) * g.weighting_percentage) / 100.0) AS course_average " +
                "FROM course c " +
                "INNER JOIN grade g ON c.id = g.course_id " +
                "WHERE ( (:semesterId IS NULL AND c.semester_id IS NULL) OR c.semester_id = :semesterId ) " +
                "GROUP BY c.id, c.uc" +
            ")"
    )
    suspend fun getAverageFromSemester(semesterId: Int?): Double?

    @Query(
        "SELECT " +
            "SUM(ROUND(course_average) * uc) / SUM(uc) " +
            "FROM (" +
                "SELECT c.id AS course_id, c.uc AS uc, " +
                "SUM((COALESCE(g.grade_percentage, 0) * g.weighting_percentage) / 100.0) AS course_average " +
                "FROM course c " +
                "INNER JOIN grade g ON c.id = g.course_id " +
                "WHERE ( (:semesterId IS NULL AND c.semester_id IS NULL) OR c.semester_id = :semesterId ) " +
                "GROUP BY c.id, c.uc" +
            ")"
    )
    suspend fun getAverageRoundFromSemester(semesterId: Int?): Double?

    /**
     * Agrega estadísticas globales sobre todos los semestres guardados
     * (todas las asignaturas cuyo semester_id apunta a un semestre existente).
     *
     * @param round si es true redondea el promedio de cada asignatura antes de
     *              ponderarlo por UC (mismo criterio que getAverageRoundFromSemester).
     */
    @Query(
        "SELECT " +
            "(SELECT COUNT(*) FROM course c INNER JOIN semester s ON c.semester_id = s.id) AS totalCourses, " +
            "(SELECT COALESCE(SUM(c.uc), 0) FROM course c INNER JOIN semester s ON c.semester_id = s.id) AS totalWeight, " +
            "(SELECT " +
                "(CASE WHEN :averageCoursesRounded = 1 THEN SUM(ROUND(course_average) * uc) ELSE SUM(course_average * uc) END) / NULLIF(SUM(uc), 0) " +
                "FROM (" +
                    "SELECT c.id AS course_id, c.uc AS uc, " +
                    "SUM((COALESCE(g.grade_percentage, 0) * g.weighting_percentage) / 100.0) AS course_average " +
                    "FROM course c " +
                    "INNER JOIN grade g ON c.id = g.course_id " +
                    "GROUP BY c.id, c.uc" +
                ")" +
            ") AS totalAverage"
    )
    suspend fun getSemestersStatistics(averageCoursesRounded: Boolean): SemesterStatistics

    @Query("UPDATE course SET semester_id = :semesterIdReceiver WHERE ( (:semesterIdSender IS NULL AND semester_id IS NULL) OR semester_id = :semesterIdSender )")
    suspend fun transferSemesterToSemester(semesterIdSender: Int?, semesterIdReceiver: Int?): Int
}