package com.app.grader.domain.model

import com.app.grader.data.database.entitites.SemesterEntity

data class SemesterModel (
    val title: String,
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
fun SemesterEntity.toSemesterModel():SemesterModel{
    return SemesterModel(
        title = this.title,
        id = this.id
    )
}