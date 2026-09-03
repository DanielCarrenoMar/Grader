package com.app.grader.domain.model

import com.app.grader.infrastructure.database.entitites.TypeGradeEntity

data class TypeGradeModel(
    val id: Int = 0,
    val title: String = "Sin título",
    val max: Int = 0,
    val minToPass: Double? = null,
    val isFromSystem: Boolean = false,
    val isDirectPercentage: Boolean = false,
)

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