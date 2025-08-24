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

    private fun isSystemInDarkMode(): Boolean {
        val uiModeManager = context.resources.configuration.uiMode
        return (uiModeManager and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    fun isRoundFinalCourseAverage(): Boolean {
        return sharedPreferences.getBoolean("roundAverageEnable", isSystemInDarkMode())
    }
    fun setRoundFinalCourseAverage(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("roundAverageEnable", enabled) }
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean("darkModeEnable", false)
    }
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("darkModeEnable", enabled) }
    }

    fun getTypeGrade(): TypeGrade {
        return TypeGrade.valueOf(sharedPreferences.getString("typeGrade", TypeGrade.NUMERIC_20.name)!!)
    }
    fun setTypeGrade(typeGrade: TypeGrade) {
        sharedPreferences.edit { putString("typeGrade", typeGrade.name) }
    }
}