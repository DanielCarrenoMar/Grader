package com.app.grader.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Course

@Serializable
object Grade

@Serializable
data class AllGrades(val text: String)