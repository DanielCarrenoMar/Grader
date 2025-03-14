package com.app.grader.core.navigation

import com.app.grader.domain.model.CourseModel
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class AllGrades(val text: String)

@Serializable
object Config

@Serializable
data class Course(val courseId: Int)

@Serializable
object EditCourse

@Serializable
object Grade

@Serializable
object EditGrade

