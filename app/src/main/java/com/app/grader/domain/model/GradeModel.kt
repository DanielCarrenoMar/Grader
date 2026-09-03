package com.app.grader.domain.model

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.infrastructure.database.entitites.GradeEntity
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage


open class GradeModel(
    val courseId: Int,
    val title: String,
    val description: String,
    val grade: Grade,
    val percentage: Percentage,
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
            grade = Grade(null, 0.0, 0),
            percentage = Percentage(),
        )
    }
}

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        gradePercentage = this.grade.getGradePercentage(),
        weightingPercentage = this.percentage.getPercentage(),
    )
}

fun GradeEntity.toGradeModel(gradeFactory: GradeFactory): GradeModel {
    return GradeModel(
        id = this.id,
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        grade = gradeFactory.instGradeFromPercentage(this.gradePercentage),
        percentage = Percentage(this.weightingPercentage),
    )
}
