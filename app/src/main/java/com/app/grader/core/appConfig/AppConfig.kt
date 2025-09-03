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
}