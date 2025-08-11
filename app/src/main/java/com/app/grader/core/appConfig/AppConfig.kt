package com.app.grader.core.appConfig

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppConfig(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("app_config", Context.MODE_PRIVATE)
    }

    fun isRoundAverageEnable(): Boolean {
        return sharedPreferences.getBoolean("roundAverageEnable", false)
    }
    fun setRoundAverageEnable(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("roundAverageEnable", enabled) }
    }

    fun isDarkModeEnable(): Boolean {
        return sharedPreferences.getBoolean("darkModeEnable", false)
    }
    fun setDarkModeEnable(enabled: Boolean) {
        sharedPreferences.edit { putBoolean("darkModeEnable", enabled) }
    }
}