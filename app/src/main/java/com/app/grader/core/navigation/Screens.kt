package com.app.grader.core.navigation

import com.app.grader.domain.model.CourseModel
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object AllGrades

@Serializable
object Config

@Serializable
data class Course(val courseId: Int)

@Serializable
data class EditCourse(val courseId: Int)

@Serializable
data class EditGrade(val gradeId: Int, val courseId: Int)

