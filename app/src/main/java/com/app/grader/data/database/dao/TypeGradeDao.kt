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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypeGrade(typeGrade: TypeGradeEntity): Long

    @Query("DELETE FROM type_grade WHERE id = :typeGradeId")
    suspend fun deleteTypeGradeFromId(typeGradeId: Int): Int
}