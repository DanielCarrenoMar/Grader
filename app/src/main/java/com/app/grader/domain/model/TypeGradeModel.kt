package com.app.grader.domain.model

import com.app.grader.infrastructure.database.entitites.TypeGradeEntity

data class TypeGradeModel(
    val id: Int,
    val title: String,
    val max: Int,
    val minToPass: Double?,
    val isFromSystem: Boolean,
    val isDirectPercentage: Boolean,
) {
    constructor() : this(
        id = 0,
        title = "Sin título",
        max = 0,
        minToPass = null,
        isFromSystem = false,
        isDirectPercentage = false,
    )
}

fun TypeGradeEntity.toTypeGradeModel(): TypeGradeModel {
    return TypeGradeModel(
        id = this.id,
        title = this.title,
        max = this.max,
        minToPass = this.minToPass,
        isFromSystem = this.isFromSystem,
        isDirectPercentage = this.isDirectPercentage,
    )
}

fun TypeGradeModel.toTypeGradeEntity(): TypeGradeEntity {
    return TypeGradeEntity(
        id = this.id,
        title = this.title,
        max = this.max,
        minToPass = this.minToPass,
        isFromSystem = this.isFromSystem,
        isDirectPercentage = this.isDirectPercentage,
        active = true,
    )
}