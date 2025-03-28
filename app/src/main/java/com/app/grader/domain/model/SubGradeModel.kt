package com.app.grader.domain.model

import com.app.grader.data.database.entitites.SubGradeEntity


data class SubGradeModel(
    val gradeId: Int,
    val title: String,
    val grade: Double,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = SubGradeModel(
            gradeId = -1,
            title = "NULL",
            grade = 0.0,
        )
    }
}

fun SubGradeModel.toSubGradeEntity(): SubGradeEntity {
    return SubGradeEntity(
        gradeId = this.gradeId,
        title = this.title,
        grade = this.grade,
    )
}
