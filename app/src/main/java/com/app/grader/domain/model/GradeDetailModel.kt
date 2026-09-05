package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

data class GradeFieldError(
    val field: String,
    val message: String,
)

class GradeDetailValidationException(
    val errors: List<GradeFieldError>,
) : IllegalArgumentException(errors.firstOrNull()?.message ?: "Error de validación")

class GradeDetailModel private constructor(
    courseId: Int,
    title: String,
    description: String,
    gradeValue: GradeValue,
    percentage: Percentage,
    id: Int = -1,
    val subgrades: List<SubGradeModel> = emptyList(),
    isDirectPercentage: Boolean = false,
): GradeModel(courseId, title, description, gradeValue, percentage, isDirectPercentage, id) {
    companion object {
        fun create(
            courseId: Int,
            title: String,
            description: String,
            gradeValue: Double?,
            percentage: Percentage,
            typeGrade: TypeGradeModel? = null,
            id: Int = -1,
            subgrades: List<SubGradeModel> = emptyList(),
        ): Result<GradeDetailModel> {
            val errors = mutableListOf<GradeFieldError>()
            val formatTitle = title.ifBlank { "Sin título" }
            val formatDescription = description.ifBlank { "Sin descripción" }
            val formatGradeValue = typeGrade?.let {
                GradeValue(null, it.minToPass, it.max.toDouble())
            } ?: GradeValue()
            val formatSubgrades = if (typeGrade?.isDirectPercentage == true) {
                emptyList()
            } else {
                subgrades.map { subgrade ->
                    subgrade.normalize(typeGrade)
                }
            }

            if (courseId <= 0 && courseId != -1) {
                errors += GradeFieldError("course", "El parámetro 'courseId' ($courseId) debe ser mayor a 0 o -1.")
            }
            if (percentage.getPercentage() <= 0.0) {
                errors += GradeFieldError("percentage", "El porcentaje debe ser mayor a 0.")
            }
            if (gradeValue != null) {
                if (formatGradeValue.check(gradeValue)) formatGradeValue.setValue(gradeValue)
                else errors += GradeFieldError("grade", "La calificación ($gradeValue) debe estar entre 0 y ${formatGradeValue.getMax()}.")
            }
            if (!typeGrade?.isDirectPercentage.orFalse()) subgrades.forEachIndexed { index, subgrade ->
                val value = subgrade.gradeValue.getValue()
                if (value != null && !formatGradeValue.check(value)) {
                    errors += GradeFieldError("subgrade:$index", "La calificación ($value) debe estar entre 0 y ${formatGradeValue.getMax()}.")
                }
            }
            if (errors.isNotEmpty()) return Result.failure(GradeDetailValidationException(errors))

            return Result.success(GradeDetailModel(courseId, formatTitle, formatDescription, formatGradeValue, percentage, id, formatSubgrades, typeGrade?.isDirectPercentage == true))
        }

        val DEFAULT = GradeDetailModel(
            courseId = -1,
            title = "",
            description = "",
            gradeValue = GradeValue(null, 0.0, 0),
            percentage = Percentage(),
            subgrades = emptyList(),
        )
    }
}

private fun Boolean?.orFalse(): Boolean = this == true

fun SubGradeModel.normalize(typeGrade: TypeGradeModel?): SubGradeModel {
    val value = gradeValue.getValue()
    val normalized = typeGrade?.let { GradeValue(null, it.minToPass, it.max) } ?: GradeValue(gradeValue)
    if (value != null && normalized.check(value)) normalized.setValue(value) else normalized.setBlank()
    return copy(gradeValue = normalized.getValue(), minToPass = normalized.getMinToPass(), max = normalized.getMax().toInt())
}
