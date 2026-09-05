package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

class GradeDetailModel(
    courseId: Int,
    title: String,
    description: String,
    gradeValue: Double?,
    percentage: Percentage,
    id: Int = -1,
    subgrades: List<SubGradeModel> = emptyList(),
    typeGradeModel: TypeGradeModel
): GradeModel(
    courseId,
    title,
    description,
    gradeValue,
    typeGradeModel,
    percentage,
    id,
) {
    private val validationSubgrades = subgrades
    val subgrades = if (isDirectPercentage) emptyList() else subgrades

    init {
        val errors = subgradeValidationErrors(this.gradeValue, validationSubgrades, typeGradeModel)
        if (errors.isNotEmpty()) throw GradeDetailValidationException(errors)
    }

    companion object {
        fun createResult(
            courseId: Int,
            title: String,
            description: String,
            gradeValue: Double?,
            percentage: Percentage,
            typeGrade: TypeGradeModel,
            id: Int = -1,
            subgrades: List<SubGradeModel> = emptyList(),
        ): Result<GradeDetailModel> = runCatching {
            GradeDetailModel(
                courseId = courseId,
                title = title,
                description = description,
                gradeValue = gradeValue,
                percentage = percentage,
                id = id,
                subgrades = subgrades,
                typeGradeModel = typeGrade
            )
        }
    }
}

private fun subgradeValidationErrors(
    gradeValue: GradeValue,
    subgrades: List<SubGradeModel>,
    typeGradeModel: TypeGradeModel
): List<GradeFieldError> = buildList {
    if (!typeGradeModel.isDirectPercentage) {
        subgrades.forEachIndexed { index, subgrade ->
            val value = subgrade.gradeValue.getValue()
            if (value != null && !gradeValue.check(value)) {
                add(GradeFieldError("subgrade:$index", "La calificación ($value) debe estar entre 0 y ${gradeValue.getMax()}."))
            }
        }
    }
}
