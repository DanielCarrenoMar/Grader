package com.app.grader.domain.model

import com.app.grader.infrastructure.database.dao.SubGradeCalculate
import com.app.grader.infrastructure.database.entitites.SubGradeEntity
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.averageGrade

class SubGradeModel private constructor(
    val gradeId: Int,
    title: String?,
    gradeValue: Double?,
    private val minToPass: Double?,
    private val max: Int,
    val id: Int = -1,
) {
    val title: String = title?.ifBlank { "Sin título" } ?: "Sin título"
    val gradeValue: GradeValue = GradeValue(gradeValue, minToPass, max)

    fun copy(
        gradeId: Int = this.gradeId,
        title: String? = this.title,
        gradeValue: Double? = this.gradeValue.getValue(),
        minToPass: Double? = this.minToPass,
        max: Int = this.max,
        id: Int = this.id,
    ): SubGradeModel {
        return create(gradeId, title, gradeValue, minToPass, max, id).getOrThrow()
    }

    companion object {
        fun create(
            gradeId: Int,
            title: String?,
            gradeValue: Double?,
            minToPass: Double?,
            max: Int,
            id: Int = -1,
        ): Result<SubGradeModel> {
            if (gradeId <= 0 && gradeId != -1) {
                return Result.failure(IllegalArgumentException("El parámetro 'gradeId' ($gradeId) debe ser mayor a 0 o -1."))
            }
            if (max < 0) {
                return Result.failure(IllegalArgumentException("El parámetro 'max' ($max) debe ser mayor o igual a 0."))
            }
            if (minToPass != null && (minToPass < 0.0 || minToPass > max.toDouble())) {
                return Result.failure(IllegalArgumentException("El parámetro 'minToPass' ($minToPass) debe estar entre 0 y $max."))
            }
            if (gradeValue != null && gradeValue !in 0.0..max.toDouble()) {
                return Result.failure(IllegalArgumentException("La calificación ($gradeValue) debe estar entre 0 y $max."))
            }
            return Result.success(SubGradeModel(gradeId, title, gradeValue, minToPass, max, id))
        }
    }
}

fun SubGradeModel.toSubGradeEntity(): SubGradeEntity {
    return SubGradeEntity(
        gradeId = this.gradeId,
        title = this.title,
        gradePercentage = this.gradeValue.getGradePercentage(),
    )
}

fun SubGradeCalculate.toSubGradeModel(): SubGradeModel {
    val gradeValue = GradeValue.createFromGradePercentage(this.gradePercentage, this.minToPass, this.max)
    return SubGradeModel.create(
        gradeId = this.gradeId,
        title = this.title,
        gradeValue = gradeValue.getValue(),
        minToPass = gradeValue.getMinToPass(),
        max = gradeValue.getMax(),
        id = this.id,
    ).getOrThrow()
}

fun Iterable<SubGradeModel>.average(): Double? {
    return this
        .map { it.gradeValue }
        .averageGrade()
}
