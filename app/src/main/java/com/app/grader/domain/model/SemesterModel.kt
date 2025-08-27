package com.app.grader.domain.model

import com.app.grader.data.database.entitites.SemesterEntity
import com.app.grader.domain.types.Grade

data class SemesterModel (
    val title: String,
    val average: Grade = Grade(0.0, 0.0, 0),
    val size: Int = 0,
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
fun SemesterEntity.toSemesterModel(average: Grade, size: Int):SemesterModel{
    return SemesterModel(
        title = this.title,
        average = average,
        size = size,
        id = this.id
    )
}