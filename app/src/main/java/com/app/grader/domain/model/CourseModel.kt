package com.app.grader.domain.model

import com.app.grader.infrastructure.database.dao.CalculatedCourse
import com.app.grader.infrastructure.database.entitites.CourseEntity
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage


data class CourseModel(
    val semesterId: Int? = null,
    val typeGradeId: Int = 0,
    val title: String,
    val uc: Int,
    val average: GradeValue = GradeValue(),
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
        semesterId = this.semesterId,
        typeGradeId = this.typeGradeId,
    )
}
fun CalculatedCourse.toCourseModel(): CourseModel {
    return CourseModel(
        id = this.id,
        title = this.title,
        uc = this.uc,
        semesterId = this.semesterId,
        typeGradeId = this.typeGradeId,
        average = GradeValue(this.average, this.minToPass ?: this.max.toDouble(), this.max),
        totalPercentage = Percentage(this.totalWeightingPercentage ?: 0.0)
    )
}
