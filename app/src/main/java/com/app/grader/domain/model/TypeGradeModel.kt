package com.app.grader.domain.model

import com.app.grader.data.database.entitites.TypeGradeEntity

data class TypeGradeModel(
    val id: Int = 0,
    val baseAt: Int? = null,
    val active: Boolean = true,
)

fun TypeGradeEntity.toTypeGradeModel(): TypeGradeModel {
    return TypeGradeModel(
        id = this.id,
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