package com.app.grader.domain.model

import com.app.grader.data.database.entitites.CourseEntity


data class CourseModel(
    val title: String,
    val description: String,
    val uc: Int,
)

fun CourseModel.toCourseEntity():CourseEntity{
    return CourseEntity(
        title = this.title,
        description = this.description,
        uc = this.uc
    )
}
