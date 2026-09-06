package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

class GradeDetailModel(
    courseId: Int,
    title: String,
    description: String,
    gradeValueRaw: Double?,
    percentage: Percentage,
    id: Int = -1,
    subgrades: List<SubGradeModel> = emptyList(),
    typeGradeModel: TypeGradeModel
): GradeModel(
    courseId,
    title,
    description,
    gradeValueRaw,
    typeGradeModel,
    percentage,
    id,
) {
    private val validationSubgrades = subgrades
    val subgrades = if (isDirectPercentage) emptyList() else subgrades.map { it.actTypeGrade(typeGradeModel) }

    override fun validate(): List<GradeFieldError> {
        return validateBase() + subgradeValidationErrors(gradeValue, validationSubgrades, typeGradeModel)
    }

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
        ): Result<GradeDetailModel> {
            val errors = mutableListOf<GradeFieldError>()
            if (!typeGrade.isDirectPercentage) {
                subgrades.forEachIndexed { index, subgrade ->
                    val value = subgrade.gradeValue.getValue()
                    if (value != null && (value < 0.0 || value > typeGrade.max.toDouble())) {
                        val maxFormatted = if (typeGrade.max.toDouble() % 1.0 == 0.0) typeGrade.max.toString() else typeGrade.max.toDouble().toString()
                        errors += GradeFieldError("subgrade:$index", "La calificación ($value) debe estar entre 0 y $maxFormatted.")
                    }
                }
            }
            if (errors.isNotEmpty()) {
                return Result.failure(GradeDetailValidationException(errors))
            }
            return createResult(
                courseId = courseId,
                title = title,
                description = description,
                gradeValue = gradeValue,
                weight = percentage,
                typeGradeModel = typeGrade,
                id = id,
            ).map { gradeModel ->
                GradeDetailModel(
                    courseId = gradeModel.courseId,
                    title = gradeModel.title,
                    description = gradeModel.description,
                    gradeValueRaw = gradeModel.gradeValueRaw,
                    percentage = gradeModel.weight,
                    id = gradeModel.id,
                    subgrades = subgrades,
                    typeGradeModel = typeGrade,
                )
            }
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
