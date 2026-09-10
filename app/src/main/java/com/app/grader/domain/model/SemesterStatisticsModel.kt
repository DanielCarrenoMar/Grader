package com.app.grader.domain.model

import com.app.grader.domain.types.GradeValue

data class SemesterStatisticsModel(
    val totalCourses: Int = 0,
    val totalWeight: Int = 0,
    val totalAverage: GradeValue,
)
