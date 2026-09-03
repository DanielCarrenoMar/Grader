package com.app.grader.domain.model

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.infrastructure.database.entitites.SubGradeEntity
import com.app.grader.domain.types.GradeValue


data class SubGradeModel(
    val gradeId: Int,
    val title: String,
    val gradeValue: GradeValue,
    val id: Int = -1,
)

fun SubGradeModel.toSubGradeEntity(): SubGradeEntity {
    return SubGradeEntity(
        gradeId = this.gradeId,
        title = this.title,
        gradePercentage = this.gradeValue.getGradePercentage(),
    )
}
fun SubGradeEntity.toSubGradeModel(gradeFactory: GradeFactory): SubGradeModel {
    return SubGradeModel(
        gradeId = this.gradeId,
        title = this.title,
        gradeValue = gradeFactory.instGradeFromPercentage(this.gradePercentage),
        id = this.id,
    )
}
