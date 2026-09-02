package com.app.grader.infrastructure.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sub_grade",
    foreignKeys = [
        ForeignKey(
            entity = GradeEntity::class,
            parentColumns = ["id"],
            childColumns = ["grade_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("grade_id")]
)
data class SubGradeEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "grade_id") val gradeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "grade_percentage") val gradePercentage: Double,
)