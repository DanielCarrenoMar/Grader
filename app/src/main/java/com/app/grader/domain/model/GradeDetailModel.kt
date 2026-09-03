package com.app.grader.domain.model

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

class GradeDetailModel private constructor(
    courseId: Int,
    title: String,
    description: String,
    gradeValue: GradeValue,
    percentage: Percentage,
    id: Int = -1,
    val subgrades: List<SubGradeModel> = emptyList(),
): GradeModel(courseId, title, description, gradeValue, percentage, false, id) {
    companion object {
        fun create(
            courseId: Int,
            title: String,
            description: String,
            gradeValue: Double?,
            gradeFactory: GradeFactory,
            percentage: Percentage,
            id: Int = -1,
            subgrades: List<SubGradeModel> = emptyList(),
        ): Result<GradeDetailModel> {
            var formatTitle = title
            var formatDescription = description
            val formatGradeValue = gradeFactory.instGrade()

            if (title.isBlank()) {
                formatTitle = "Sin título"
            }
            if (description.isBlank()) {
                formatDescription = "Sin descripción"
            }
            if (courseId <= 0 && courseId != -1) {
                return Result.failure(
                    IllegalArgumentException("El parámetro 'courseId' ($courseId) debe ser mayor a 0 o -1.")
                )
            }
            if (percentage.getPercentage() <= 0.0) {
                return Result.failure(
                    IllegalArgumentException("El porcentaje debe ser mayor a 0.")
                )
            }
            if (gradeValue != null) {
                if (formatGradeValue.check(gradeValue)) {
                    formatGradeValue.setValue(gradeValue)
                } else {
                    return Result.failure(
                        IllegalArgumentException("La calificación ($gradeValue) debe estar entre 0 y ${formatGradeValue.getMax()}.")
                    )
                }
            }

            return Result.success(GradeDetailModel(courseId, formatTitle, formatDescription, formatGradeValue, percentage, id, subgrades))
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
