package com.app.grader.domain.repository

import com.app.grader.domain.types.ThemeType

interface AppConfigRepository {
    fun getTypeTheme(): ThemeType
    fun setTypeTheme(themeType: ThemeType)

    fun isRoundFinalCourseAverage(): Boolean
    fun setRoundFinalCourseAverage(enabled: Boolean)

    fun getDefaultTypeGradeId(): Int
    fun setDefaultTypeGradeId(id: Int)

    fun getLaunchCount(): Int
    fun setLaunchCount(count: Int)
    fun getFirstLaunchTime(): Long

    fun getReviewAskedCount(): Int
    fun setReviewAskedCount(count: Int)
    fun getLastReviewAskedTime(): Long
    fun setLastReviewAskedTime(timeMills: Long)

    fun isReviewCompleted(): Boolean
    fun setReviewCompleted(completed: Boolean)
}
