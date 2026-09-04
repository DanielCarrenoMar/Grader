package com.app.grader.domain.model

import com.app.grader.infrastructure.database.dao.CalculatedGrade
import com.app.grader.infrastructure.database.entitites.GradeEntity
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage


open class GradeModel(
    val courseId: Int,
    val title: String,
    val description: String,
    val gradeValue: GradeValue,
    val percentage: Percentage,
    val isDirectPercentage: Boolean,
    val id: Int = -1,
){
    open fun validate() {
        if (percentage.getPercentage() <= 0.0) {
            throw IllegalArgumentException("El porcentaje de la nota no puede ser 0%")
        }
    }

    companion object {
        val DEFAULT = GradeModel(
            courseId = -1,
            title = "",
            description = "",
            gradeValue = GradeValue(null, 0.0, 0),
            percentage = Percentage(),
            isDirectPercentage = false,
        )
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

fun CalculatedGrade.toGradeModel(): GradeModel {
    return GradeModel(
        id = this.id,
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        gradeValue = GradeValue(this.gradeValue, this.minToPass ?: this.max.toDouble(), this.max),
        percentage = Percentage(this.weightingPercentage),
        isDirectPercentage = this.isDirectPercentage
    )
}
