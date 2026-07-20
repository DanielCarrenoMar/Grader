package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.TypeGradeEntity

@Dao
interface TypeGradeDao {
    @Query("SELECT * FROM type_grade ORDER BY id ASC")
    suspend fun getAllTypeGrades(): List<TypeGradeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTypeGrade(typeGrade: TypeGradeEntity): Long

    @Query("UPDATE type_grade SET base_at = :baseAt, active = :active WHERE id = :typeGradeId")
    suspend fun updateTypeGradeById(typeGradeId: Int, baseAt: Int?, active: Boolean): Int

    @Query("DELETE FROM type_grade WHERE id = :typeGradeId")
    suspend fun deleteTypeGradeFromId(typeGradeId: Int): Int
}