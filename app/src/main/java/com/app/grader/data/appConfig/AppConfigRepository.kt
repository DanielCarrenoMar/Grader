package com.app.grader.data.appConfig

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.domain.types.ThemeType

object AppConfig {
    const val PREFS_NAME = "app_config"
    const val ROUND_AVERAGE_ENABLE = "roundAverageEnable"
    const val TYPE_THEME = "typeTheme"
    const val TYPE_GRADE = "typeGrade"
    const val LAUNCH_COUNT = "launchCount"
    const val FIRST_LAUNCH_TIME = "firstLaunchTime"
    const val REVIEW_ASKED_COUNT = "reviewAskedCount"
    const val LAST_REVIEW_ASKED_TIME = "lastReviewAskedTime"
    const val REVIEW_COMPLETED = "reviewCompleted"
}

class AppConfigRepository(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(AppConfig.PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isRoundFinalCourseAverage(): Boolean {
        return sharedPreferences.getBoolean(AppConfig.ROUND_AVERAGE_ENABLE, false)
    }
    fun setRoundFinalCourseAverage(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(AppConfig.ROUND_AVERAGE_ENABLE, enabled) }
    }

    fun getTypeTheme(): ThemeType {
        return ThemeType.valueOf(
            sharedPreferences.getString(AppConfig.TYPE_THEME, ThemeType.SYSTEM_DEFAULT.name)!!
        )
    }
    fun setTypeTheme(themeType: ThemeType) {
        sharedPreferences.edit { putString(AppConfig.TYPE_THEME, themeType.name) }
    }

    fun getTypeGrade(): TypeGrade {
        return TypeGrade.valueOf(
            sharedPreferences.getString(AppConfig.TYPE_GRADE, TypeGrade.NUMERIC_20.name)!!
        )
    }
    fun setTypeGrade(typeGrade: TypeGrade) {
        sharedPreferences.edit { putString(AppConfig.TYPE_GRADE, typeGrade.name) }
    }

    fun getLaunchCount(): Int = sharedPreferences.getInt(AppConfig.LAUNCH_COUNT, 0)
    fun setLaunchCount(count: Int) = sharedPreferences.edit { putInt(AppConfig.LAUNCH_COUNT, count) }

    fun getFirstLaunchTime(): Long = sharedPreferences.getLong(AppConfig.FIRST_LAUNCH_TIME, System.currentTimeMillis())

    fun getReviewAskedCount(): Int = sharedPreferences.getInt(AppConfig.REVIEW_ASKED_COUNT, 0)
    fun setReviewAskedCount(count: Int) = sharedPreferences.edit { putInt(AppConfig.REVIEW_ASKED_COUNT, count) }

    fun getLastReviewAskedTime(): Long = sharedPreferences.getLong(AppConfig.LAST_REVIEW_ASKED_TIME, 0L)
    fun setLastReviewAskedTime(timeMills: Long) = sharedPreferences.edit { putLong(AppConfig.LAST_REVIEW_ASKED_TIME, timeMills) }

    fun isReviewCompleted(): Boolean = sharedPreferences.getBoolean(AppConfig.REVIEW_COMPLETED, false)
    fun setReviewCompleted(completed: Boolean) = sharedPreferences.edit { putBoolean(AppConfig.REVIEW_COMPLETED, completed) }
}