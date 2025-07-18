package com.app.grader.domain.model

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.domain.types.Grade


data class CourseModel(
    val title: String,
    val uc: Int,
    val average: Grade = Grade(),
    val id: Int = -1,
){
    companion object {
        val DEFAULT = CourseModel(
            title = "NULL",
            uc = -1,
            average = Grade()
        )
    }
}

fun CourseModel.toCourseEntity():CourseEntity{
    return CourseEntity(
        title = this.title,
        uc = this.uc
    )
}
