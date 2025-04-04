package com.app.grader.domain.model

import com.app.grader.data.database.entitites.CourseEntity


data class CourseModel(
    val title: String,
    val uc: Int,
    val average: Double = 0.0,
    val id: Int = -1,
){
    companion object {
        val DEFAULT = CourseModel(
            title = "NULL",
            uc = -1
        )
    }
}

fun CourseModel.toCourseEntity():CourseEntity{
    return CourseEntity(
        title = this.title,
        uc = this.uc
    )
}
