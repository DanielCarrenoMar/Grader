package com.app.grader.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class AllGrades(val text: String)

@Serializable
object Config

@Serializable
object Course

@Serializable
object EditCourse

@Serializable
object Grade

@Serializable
object EditGrade

