package com.app.grader.data.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course")
data class CourseEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "semester_id") val semesterId: Int?,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "uc") val uc: Int,
)