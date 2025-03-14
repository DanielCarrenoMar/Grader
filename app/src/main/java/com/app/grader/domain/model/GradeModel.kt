package com.app.grader.domain.model

import com.app.grader.data.database.entitites.GradeEntity


data class GradeModel(
    val id: Int,
    val courseId: Int,
    val title: String,
    val description: String,
    val grade: Double,
    val percentage: Double
)

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        title = this.title,
        description = this.description,
        grade = this.grade,
        id = this.id,
        courseId = this.courseId,
        percentage = this.percentage,
    )
}
