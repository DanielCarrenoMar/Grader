package com.app.grader.infrastructure.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.grader.infrastructure.database.entitites.TypeGradeEntity

@Dao
interface TypeGradeDao {
    @Query("SELECT * FROM type_grade ORDER BY id ASC")
    suspend fun getAllTypeGrades(): List<TypeGradeEntity>

    @Query("SELECT * FROM type_grade WHERE id = :typeGradeId")
    suspend fun getTypeGradeById(typeGradeId: Int): TypeGradeEntity?

    @Query(
        """
        SELECT tg.id, tg.title, tg.max, tg.min_to_pass, tg.is_from_system, tg.is_direct_percentage, tg.active
        FROM course c
        INNER JOIN type_grade tg ON tg.id = c.type_grade_id
        WHERE c.id = :courseId
        """
    )
    suspend fun getTypeGradeFromCourseId(courseId: Int): TypeGradeEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTypeGrade(typeGrade: TypeGradeEntity): Long

    @Query(
        "UPDATE type_grade SET title = :title, max = :max, min_to_pass = :minToPass, " +
            "is_from_system = :isFromSystem, is_direct_percentage = :isDirectPercentage, active = :active " +
            "WHERE id = :typeGradeId"
    )
    suspend fun updateTypeGradeById(
        typeGradeId: Int,
        title: String,
        max: Int,
        minToPass: Double?,
        isFromSystem: Boolean,
        isDirectPercentage: Boolean,
        active: Boolean
    ): Int

    @Query("DELETE FROM type_grade WHERE id = :typeGradeId")
    suspend fun deleteTypeGradeFromId(typeGradeId: Int): Int
}