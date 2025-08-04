package com.app.grader.domain.model

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage


data class CourseModel(
    val title: String,
    val uc: Int,
    val average: Grade = Grade(),
    val totalPercentage: Percentage = Percentage(),
    val id: Int = -1,
){
    companion object {
        val DEFAULT = CourseModel(
            title = "",
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
