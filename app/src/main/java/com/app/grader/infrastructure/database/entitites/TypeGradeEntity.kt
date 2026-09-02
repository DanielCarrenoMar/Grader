package com.app.grader.infrastructure.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_grade")
data class TypeGradeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "base_at") val baseAt: Int?,
    @ColumnInfo(name = "active") val active: Boolean,
)