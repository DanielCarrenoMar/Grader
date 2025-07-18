package com.app.grader.domain.model

import com.app.grader.data.database.entitites.SubGradeEntity
import com.app.grader.domain.types.Grade


data class SubGradeModel(
    val gradeId: Int,
    val title: String,
    val grade: Grade,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = SubGradeModel(
            gradeId = -1,
            title = "",
            grade = Grade(),
        )
    }
}

fun SubGradeModel.toSubGradeEntity(): SubGradeEntity {
    return SubGradeEntity(
        gradeId = this.gradeId,
        title = this.title,
        grade = this.grade.getGrade(),
    )
}
fun SubGradeEntity.toSubGradeModel(): SubGradeModel {
    return SubGradeModel(
        gradeId = this.gradeId,
        title = this.title,
        grade = Grade(this.grade),
        id = this.id,
    )
}
