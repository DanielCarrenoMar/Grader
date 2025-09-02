package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.SemesterEntity

@Dao
interface SemesterDao {

    @Query("SELECT * FROM semester ORDER BY id DESC")
    suspend fun getAllSemesters(): List<SemesterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
}