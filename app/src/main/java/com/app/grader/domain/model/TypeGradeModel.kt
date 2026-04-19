package com.app.grader.domain.model

import com.app.grader.data.database.entitites.TypeGradeEntity

data class TypeGradeModel(
    val id: Int = 0,
    val title: String = "Sin título",
    val baseAt: Int? = null,
    val active: Boolean = true,
)

private val defaultTitles = hashMapOf(
    100 to "Base 100 (0-100)",
    20 to "Base 20 (0-20)",
    10 to "Base 10 (0-10)",
    7 to "Base 7 (0-7)",
    null to "Porcentual (0-100%)"
)

fun TypeGradeEntity.toTypeGradeModel(): TypeGradeModel {
    return TypeGradeModel(
        id = this.id,
        title = defaultTitles[this.baseAt] ?: "Sin título",
        baseAt = this.baseAt,
        active = this.active,
    )
}

fun TypeGradeModel.toTypeGradeEntity(): TypeGradeEntity {
    return TypeGradeEntity(
        id = this.id,
        baseAt = this.baseAt,
        active = this.active,
    )
}