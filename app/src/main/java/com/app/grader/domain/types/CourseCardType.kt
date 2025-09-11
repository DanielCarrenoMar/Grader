package com.app.grader.domain.types

import android.util.Log
import com.app.grader.domain.model.CourseModel

enum class CourseCardType {
    Normal,
    Fail,
    Pass,
    Finish,
}

fun cardTypeFromCourse(course: CourseModel): CourseCardType {
    val accumulatedPoints = course.average.getGrade() * (course.totalPercentage.getPercentage() / 100.0)
    val pendingPoints = (100.0 - course.totalPercentage.getPercentage()) / 100.0 * course.average.getMax()

    return if (course.average.isFailValue(pendingPoints + accumulatedPoints)) {
        CourseCardType.Fail
    } else if (pendingPoints == 0.0) {
        CourseCardType.Finish
    } else if (!course.average.isFailValue(accumulatedPoints)) {
        CourseCardType.Pass
    } else {
        CourseCardType.Normal
    }
}