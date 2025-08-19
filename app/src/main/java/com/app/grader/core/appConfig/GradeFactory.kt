package com.app.grader.core.appConfig

import android.content.Context
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.TypeGrade

class GradeFactory (context: Context) {
    private val appConfig = AppConfig(context)

    private fun getMinFromTypeGrade(): Double {
        val type = appConfig.getTypeGrade()
        return when (type) {
            TypeGrade.NUMERIC_20 -> 9.5
            TypeGrade.NUMERIC_10 -> 4.5
            TypeGrade.NUMERIC_100 -> 50.0
            TypeGrade.PERCENTAGE -> 50.0
        }
    }

    private fun getMaxFromTypeGrade(): Int {
        val type = appConfig.getTypeGrade()
        return when (type) {
            TypeGrade.NUMERIC_20 -> 20
            TypeGrade.NUMERIC_10 -> 10
            TypeGrade.NUMERIC_100 -> 100
            TypeGrade.PERCENTAGE -> 100
        }
    }

    fun fromDouble(value: Double): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return Grade(value, min, max)
    }

    fun fromInt(value: Int): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return Grade(value.toDouble(), min, max)
    }

    fun blank(): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return Grade(-1.0, min, max)
    }

    fun copyOf(grade: Grade): Grade {
        return Grade(grade.getGrade(), grade.getMin(), grade.getMax())
    }
}