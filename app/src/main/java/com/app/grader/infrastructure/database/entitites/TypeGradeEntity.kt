package com.app.grader.infrastructure.database.entitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "type_grade")
data class TypeGradeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "max") val max: Int,
    @ColumnInfo(name = "min_to_pass") val minToPass: Double?,
    @ColumnInfo(name = "is_from_system") val isFromSystem: Boolean,
    @ColumnInfo(name = "is_direct_percentage") val isDirectPercentage: Boolean,
    @ColumnInfo(name = "active") val active: Boolean,
)