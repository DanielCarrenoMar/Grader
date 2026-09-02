package com.app.grader.infrastructure.appConfig

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.types.ThemeType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

object AppConfig {
    const val PREFS_NAME = "app_config"
    const val ROUND_AVERAGE_ENABLE = "roundAverageEnable"
    const val TYPE_THEME = "typeTheme"
    const val TYPE_GRADE = "typeGrade"
    const val DEFAULT_TYPE_GRADE_ID = "defaultTypeGradeId"
    const val LAUNCH_COUNT = "launchCount"
    const val FIRST_LAUNCH_TIME = "firstLaunchTime"
    const val REVIEW_ASKED_COUNT = "reviewAskedCount"
    const val LAST_REVIEW_ASKED_TIME = "lastReviewAskedTime"
    const val REVIEW_COMPLETED = "reviewCompleted"
}

class SharedPreferencesAppConfigRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : AppConfigRepository {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(AppConfig.PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun isRoundFinalCourseAverage(): Boolean {
        return sharedPreferences.getBoolean(AppConfig.ROUND_AVERAGE_ENABLE, false)
    }
    override fun setRoundFinalCourseAverage(enabled: Boolean) {
        sharedPreferences.edit { putBoolean(AppConfig.ROUND_AVERAGE_ENABLE, enabled) }
    }

    override fun getTypeTheme(): ThemeType {
        return ThemeType.valueOf(
            sharedPreferences.getString(AppConfig.TYPE_THEME, ThemeType.SYSTEM_DEFAULT.name)!!
        )
    }
    override fun setTypeTheme(themeType: ThemeType) {
        sharedPreferences.edit { putString(AppConfig.TYPE_THEME, themeType.name) }
    }

    override fun getDefaultTypeGradeId(): Int {
        return sharedPreferences.getInt(
            AppConfig.DEFAULT_TYPE_GRADE_ID,
            3 // id de base 20, de las opciones predefinidas.
        )
    }

    override fun setDefaultTypeGradeId(id: Int) {
        sharedPreferences.edit { putInt(AppConfig.DEFAULT_TYPE_GRADE_ID, id) }
    }

    @Deprecated("Use getDefaultTypeGradeId() for course defaults. Deprecated in version 2.0.2")
    fun getTypeGrade(): TypeGrade {
        return TypeGrade.valueOf(
            sharedPreferences.getString(AppConfig.TYPE_GRADE, TypeGrade.NUMERIC_20.name)!!
        )
    }

    @Deprecated("Use setDefaultTypeGradeId() for course defaults. Deprecated in version 2.0.2")
    fun setTypeGrade(typeGrade: TypeGrade) {
        sharedPreferences.edit { putString(AppConfig.TYPE_GRADE, typeGrade.name) }
    }

    override fun getLaunchCount(): Int = sharedPreferences.getInt(AppConfig.LAUNCH_COUNT, 0)
    override fun setLaunchCount(count: Int) {
        sharedPreferences.edit { putInt(AppConfig.LAUNCH_COUNT, count) }
    }

    override fun getFirstLaunchTime(): Long = sharedPreferences.getLong(AppConfig.FIRST_LAUNCH_TIME, System.currentTimeMillis())

    override fun getReviewAskedCount(): Int = sharedPreferences.getInt(AppConfig.REVIEW_ASKED_COUNT, 0)
    override fun setReviewAskedCount(count: Int) {
        sharedPreferences.edit { putInt(AppConfig.REVIEW_ASKED_COUNT, count) }
    }

    override fun getLastReviewAskedTime(): Long = sharedPreferences.getLong(AppConfig.LAST_REVIEW_ASKED_TIME, 0L)
    override fun setLastReviewAskedTime(timeMills: Long) {
        sharedPreferences.edit { putLong(AppConfig.LAST_REVIEW_ASKED_TIME, timeMills) }
    }

    override fun isReviewCompleted(): Boolean = sharedPreferences.getBoolean(AppConfig.REVIEW_COMPLETED, false)
    override fun setReviewCompleted(completed: Boolean) {
        sharedPreferences.edit { putBoolean(AppConfig.REVIEW_COMPLETED, completed) }
    }
}
