package com.app.grader.domain.model

import android.util.Log
import com.app.grader.infrastructure.database.dao.CalculatedGrade
import com.app.grader.infrastructure.database.entitites.GradeEntity
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage

data class GradeFieldError(
    val field: String,
    val message: String,
)

class GradeDetailValidationException(
    val errors: List<GradeFieldError>,
) : IllegalArgumentException(errors.firstOrNull()?.message ?: "Error de validación")

open class GradeModel(
    val courseId: Int,
    title: String,
    description: String,
    val gradeValue: GradeValue,
    val percentage: Percentage,
    val isDirectPercentage: Boolean,
    val id: Int = -1,
){
    val title = title.ifBlank { "Sin título" }
    val description = description.ifBlank { "Sin descripción" }

    init {
        validate()
    }

    open fun validate() {
        val errors = gradeValidationErrors(courseId, percentage)
        if (errors.isNotEmpty()) throw GradeDetailValidationException(errors)
    }

    companion object {
        fun createFromGradePercentage(
            courseId: Int,
            title: String,
            description: String,
            gradePercentage: Double?,
            weight: Percentage,
            typeGradeModel: TypeGradeModel,
            id: Int = -1,
        ): GradeModel {
            val gradeValue = if (!typeGradeModel.isDirectPercentage) {
                GradeValue.createFromGradePercentage(gradePercentage, typeGradeModel.minToPass, typeGradeModel.max)
            } else {
                val value = gradePercentage?.let { weight.getPercentage() * (it / 100.0) }
                GradeValue(value, typeGradeModel.minToPass, weight.getPercentage())
            }
            return GradeModel(courseId, title, description, gradeValue, weight, typeGradeModel.isDirectPercentage, id)
        }

        val DEFAULT = GradeModel(
            courseId = -1,
            title = "",
            description = "",
            gradeValue = GradeValue(null, 0.0, 0),
            percentage = Percentage(100.0),
            isDirectPercentage = false,
        )
    }
}

internal fun gradeValidationErrors(
    courseId: Int,
    percentage: Percentage,
): List<GradeFieldError> = buildList {
    if (courseId <= 0 && courseId != -1) {
        add(GradeFieldError("course", "El parámetro 'courseId' ($courseId) debe ser mayor a 0 o -1."))
    }
    if (percentage.getPercentage() <= 0.0) {
        add(GradeFieldError("percentage", "El porcentaje debe ser mayor a 0."))
    }
}

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        gradePercentage = this.gradeValue.getGradePercentage(),
        weightingPercentage = this.percentage.getPercentage(),
    )
}

fun CalculatedGrade.toGradeModel(typeGradeModel: TypeGradeModel): GradeModel {
    return GradeModel.createFromGradePercentage(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        gradePercentage = this.gradePercentage,
        weight = Percentage(this.weightingPercentage),
        typeGradeModel = typeGradeModel,
        id = this.id
    )
}
