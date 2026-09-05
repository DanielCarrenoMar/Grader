package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

class GradeDetailModel(
    courseId: Int,
    title: String,
    description: String,
    gradeValue: GradeValue,
    percentage: Percentage,
    id: Int = -1,
    subgrades: List<SubGradeModel> = emptyList(),
    isDirectPercentage: Boolean = false,
): GradeModel(
    courseId,
    title,
    description,
    gradeValue,
    percentage,
    isDirectPercentage,
    id,
) {
    private val validationSubgrades = subgrades
    val subgrades = if (isDirectPercentage) emptyList() else subgrades

    init {
        val errors = subgradeValidationErrors(gradeValue, validationSubgrades, isDirectPercentage)
        if (errors.isNotEmpty()) throw GradeDetailValidationException(errors)
    }

    companion object {
        fun createResult(
            courseId: Int,
            title: String,
            description: String,
            gradeValue: Double?,
            percentage: Percentage,
            typeGrade: TypeGradeModel? = null,
            id: Int = -1,
            subgrades: List<SubGradeModel> = emptyList(),
        ): Result<GradeDetailModel> = runCatching {
            val formattedGradeValue = typeGrade?.let {
                GradeValue(gradeValue, it.minToPass, it.max)
            } ?: GradeValue(gradeValue, 0.0, 0)

            GradeDetailModel(
                courseId = courseId,
                title = title,
                description = description,
                gradeValue = formattedGradeValue,
                percentage = percentage,
                id = id,
                subgrades = subgrades,
                isDirectPercentage = typeGrade?.isDirectPercentage == true,
            )
        }

        val DEFAULT = GradeDetailModel(
            courseId = -1,
            title = "",
            description = "",
            gradeValue = GradeValue(null, 0.0, 0),
            percentage = Percentage(100.0),
        )
    }
}

private fun subgradeValidationErrors(
    gradeValue: GradeValue,
    subgrades: List<SubGradeModel>,
    isDirectPercentage: Boolean,
): List<GradeFieldError> = buildList {
    if (!isDirectPercentage) {
        subgrades.forEachIndexed { index, subgrade ->
            val value = subgrade.gradeValue.getValue()
            if (value != null && !gradeValue.check(value)) {
                add(GradeFieldError("subgrade:$index", "La calificación ($value) debe estar entre 0 y ${gradeValue.getMax()}."))
            }
        }
    }
}
