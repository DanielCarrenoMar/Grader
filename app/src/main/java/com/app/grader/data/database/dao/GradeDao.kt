package com.app.grader.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.data.database.entitites.GradeEntity

@Dao
interface GradeDao {

    @Query("SELECT * FROM grade")
    suspend fun getAllGrades():List<GradeEntity>
    /*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(quotes:List<QuoteEntity>)

    @Query("DELETE FROM course")
    suspend fun deleteAllQuotes()*/
}