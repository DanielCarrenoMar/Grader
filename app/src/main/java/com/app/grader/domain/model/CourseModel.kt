package com.app.grader.domain.model

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage


data class CourseModel(
    val semesterId: Int? = null,
    val title: String,
    val uc: Int,
    val average: Grade = Grade(-1, 0.0, 0),
    val totalPercentage: Percentage = Percentage(),
    val id: Int = -1,
){
    companion object {
        val DEFAULT = CourseModel(
            title = "",
            uc = -1,
        )
    }
}

fun CourseModel.toCourseEntity():CourseEntity{
    return CourseEntity(
        title = this.title,
        uc = this.uc,
        semesterId = this.semesterId
    )
}
