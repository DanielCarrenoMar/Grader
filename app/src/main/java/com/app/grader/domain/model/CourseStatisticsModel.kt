package com.app.grader.domain.model

import com.app.grader.domain.types.Percentage

data class CourseStatisticsModel(
    val totalPercentage: Percentage,
    val accumulatePoints: Double,
    val pendingPoints: Double,
)
