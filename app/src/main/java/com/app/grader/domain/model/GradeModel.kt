package com.app.grader.domain.model

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
    val gradeValueRaw: Double?,
    val typeGradeModel: TypeGradeModel,
    val weight: Percentage,
    val id: Int = -1,
){
    constructor() : this(
        courseId = -1,
        title = "",
        description = "",
        gradeValueRaw = null,
        typeGradeModel = TypeGradeModel(),
        weight = Percentage(1.0),
    )

    val title = title.ifBlank { "Sin título" }
    val description = description.ifBlank { "Sin descripción" }
    val isDirectPercentage = typeGradeModel.isDirectPercentage

    init {
        val errors = validateBase()
        if (errors.isNotEmpty()) throw GradeDetailValidationException(errors)
    }

    open val gradeValue: GradeValue by lazy {
        if (!isDirectPercentage) {
            GradeValue(gradeValueRaw, typeGradeModel.minToPass, typeGradeModel.max)
        } else {
            GradeValue(gradeValueRaw, typeGradeModel.minToPass, weight.getPercentage())
        }
    }

    open fun validate(): List<GradeFieldError> = validateBase()

    protected fun validateBase(): List<GradeFieldError> = buildList {
        if (courseId <= 0 && courseId != -1) {
            add(GradeFieldError("course", "El parámetro 'courseId' ($courseId) debe ser mayor a 0 o -1."))
        }
        if (weight.getPercentage() <= 0.0) {
            add(GradeFieldError("percentage", "El porcentaje debe ser mayor a 0."))
        }
    }

    fun getGradePercentage(): Double? {
        if (isDirectPercentage) {
            val value = gradeValue.getValue() ?: return null
            return value * 100.0 / weight.getPercentage()
        }
        return gradeValue.getGradePercentage()
    }

    companion object {
        fun createResult(
            courseId: Int,
            title: String,
            description: String,
            gradeValue: Double?,
            weight: Percentage,
            typeGradeModel: TypeGradeModel,
            id: Int = -1,
        ): Result<GradeModel> = runCatching {
            GradeModel(courseId, title, description, gradeValue, typeGradeModel, weight, id)
        }

        fun createFromGradePercentage(
            courseId: Int,
            title: String,
            description: String,
            gradePercentage: Double?,
            weight: Percentage,
            typeGradeModel: TypeGradeModel,
            id: Int = -1,
        ): GradeModel {
            val gradeValueRaw = if (!typeGradeModel.isDirectPercentage) {
                gradePercentage?.let { it * typeGradeModel.max / 100.0 }
            } else {
                gradePercentage?.let { it * weight.getPercentage() / 100.0 }
            }
            return GradeModel(courseId, title, description, gradeValueRaw, typeGradeModel, weight, id)
        }
    }
}

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        gradePercentage = this.getGradePercentage(),
        weightingPercentage = this.weight.getPercentage(),
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
