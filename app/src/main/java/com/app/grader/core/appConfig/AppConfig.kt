package com.app.grader.core.appConfig

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

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

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean("darkModeEnable", false)
    }
    fun setDarkMode(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("darkModeEnable", enabled) }
    }
}