package com.app.grader.domain.model

import com.app.grader.infrastructure.database.entitites.SemesterEntity
import com.app.grader.domain.types.GradeValue

data class SemesterModel (
    val title: String,
    val average: GradeValue = GradeValue(0.0, 0.0, 0),
    val size: Int = 0,
    val weight: Int = 0,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = SemesterModel(
            title = "",
        )
    }
}
fun SemesterModel.toSemesterEntity():SemesterEntity{
    return SemesterEntity(
        title = this.title,
    )
}
fun SemesterEntity.toSemesterModel(average: GradeValue, size: Int, weight: Int):SemesterModel{
    return SemesterModel(
        title = this.title,
        average = average,
        size = size,
        weight = weight,
        id = this.id
    )
}