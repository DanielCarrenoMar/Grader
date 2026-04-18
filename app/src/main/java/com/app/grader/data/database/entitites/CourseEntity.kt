package com.app.grader.data.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "course",
    foreignKeys = [
        ForeignKey(
            entity = TypeGradeEntity::class,
            parentColumns = ["id"],
            childColumns = ["type_grade_id"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION,
        )
    ]
)
data class CourseEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "semester_id") val semesterId: Int?,
    @ColumnInfo(name = "type_grade_id") val typeGradeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "uc") val uc: Int,
)