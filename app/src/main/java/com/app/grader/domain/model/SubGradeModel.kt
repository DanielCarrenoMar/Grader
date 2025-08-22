package com.app.grader.domain.model

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.data.database.entitites.SubGradeEntity
import com.app.grader.domain.types.Grade


data class SubGradeModel(
    val gradeId: Int,
    val title: String,
    val grade: Grade,
    val id: Int = -1,
)

fun SubGradeModel.toSubGradeEntity(): SubGradeEntity {
    return SubGradeEntity(
        gradeId = this.gradeId,
        title = this.title,
        gradePercentage = this.grade.getGradePercentage(),
    )
}
fun SubGradeEntity.toSubGradeModel(gradeFactory: GradeFactory): SubGradeModel {
    return SubGradeModel(
        gradeId = this.gradeId,
        title = this.title,
        grade = gradeFactory.instGradeFromPercentage(this.gradePercentage),
        id = this.id,
    )
}
