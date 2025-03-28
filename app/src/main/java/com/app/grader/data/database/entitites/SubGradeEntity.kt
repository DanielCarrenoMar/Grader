package com.app.grader.data.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_grade")
data class SubGradeEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "grade_id") val gradeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "grade") val grade: Double,
)