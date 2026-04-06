package com.app.grader.core.appConfig

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit
import com.app.grader.core.appConfig.TypeGrade

class AppConfig(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
    }

    fun isRoundFinalCourseAverage(): Boolean {
        return sharedPreferences.getBoolean("roundAverageEnable", false)
    }
    fun setRoundFinalCourseAverage(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("roundAverageEnable", enabled) }
    }

    fun getTypeTheme(): TypeTheme {
        return TypeTheme.valueOf(
            sharedPreferences.getString("typeTheme", TypeTheme.SYSTEM_DEFAULT.name)!!
        )
    }
    fun setTypeTheme(typeTheme: TypeTheme) {
        sharedPreferences.edit { putString("typeTheme", typeTheme.name) }
    }

    fun getTypeGrade(): TypeGrade {
        return TypeGrade.valueOf(
            sharedPreferences.getString("typeGrade", TypeGrade.NUMERIC_20.name)!!
        )
    }
    fun setTypeGrade(typeGrade: TypeGrade) {
        sharedPreferences.edit { putString("typeGrade", typeGrade.name) }
    }

    fun getLaunchCount(): Int = sharedPreferences.getInt("launchCount", 0)
    fun setLaunchCount(count: Int) = sharedPreferences.edit { putInt("launchCount", count) }

    fun getFirstLaunchTime(): Long = sharedPreferences.getLong("firstLaunchTime", 0L)
    fun setFirstLaunchTime(timeMills: Long) = sharedPreferences.edit { putLong("firstLaunchTime", timeMills) }

    fun getReviewAskedCount(): Int = sharedPreferences.getInt("reviewAskedCount", 0)
    fun setReviewAskedCount(count: Int) = sharedPreferences.edit { putInt("reviewAskedCount", count) }

    fun getLastReviewAskedTime(): Long = sharedPreferences.getLong("lastReviewAskedTime", 0L)
    fun setLastReviewAskedTime(timeMills: Long) = sharedPreferences.edit { putLong("lastReviewAskedTime", timeMills) }

    fun isReviewCompleted(): Boolean = sharedPreferences.getBoolean("reviewCompleted", false)
    fun setReviewCompleted(completed: Boolean) = sharedPreferences.edit { putBoolean("reviewCompleted", completed) }
}