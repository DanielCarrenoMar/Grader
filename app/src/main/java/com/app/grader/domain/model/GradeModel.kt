package com.app.grader.domain.model

import com.app.grader.data.database.entitites.GradeEntity
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage


data class GradeModel(
    val courseId: Int,
    val title: String,
    val description: String,
    val grade: Grade,
    val percentage: Percentage,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = GradeModel(
            courseId = -1,
            title = "",
            description = "",
            grade = Grade(),
            percentage = Percentage(),
        )
    }
}

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        grade = this.grade.getGrade(),
        percentage = this.percentage.getPercentage(),
    )
}

fun GradeEntity.toGradeModel(): GradeModel {
    return GradeModel(
        id = this.id,
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        grade = Grade(this.grade),
        percentage = Percentage(this.percentage),
    )
}
