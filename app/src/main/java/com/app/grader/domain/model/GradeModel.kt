package com.app.grader.domain.model

import com.app.grader.data.database.entitites.GradeEntity


data class GradeModel(
    val courseId: Int,
    val title: String,
    val description: String,
    val grade: Double,
    val percentage: Double,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = GradeModel(
            courseId = -1,
            title = "NULL",
            description = "NULL",
            grade = 0.0,
            percentage = 0.0,
        )
    }
}

fun GradeModel.toGradeEntity(): GradeEntity {
    return GradeEntity(
        courseId = this.courseId,
        title = this.title,
        description = this.description,
        grade = this.grade,
        percentage = this.percentage,
    )
}
