package com.app.grader.core.appConfig

import com.app.grader.domain.types.Grade
import com.app.grader.infrastructure.appConfig.SharedPreferencesAppConfigRepository

class GradeFactory (private val appConfigRepository: SharedPreferencesAppConfigRepository) {

    private fun getMinFromTypeGrade(): Double {
        val type = appConfigRepository.getTypeGrade()
        return when (type) {
            TypeGrade.NUMERIC_7_CHI -> 4.0
            TypeGrade.NUMERIC_10_ARG -> 4.0
            TypeGrade.NUMERIC_10_ESP -> 5.0
            TypeGrade.NUMERIC_10_MEX -> 6.0
            TypeGrade.NUMERIC_20 -> 9.5
            TypeGrade.NUMERIC_100 -> 50.0
        }
    }

    private fun getMaxFromTypeGrade(): Int {
        val type = appConfigRepository.getTypeGrade()
        return when (type) {
            TypeGrade.NUMERIC_7_CHI -> 7
            TypeGrade.NUMERIC_10_ARG -> 10
            TypeGrade.NUMERIC_10_ESP -> 10
            TypeGrade.NUMERIC_10_MEX -> 10
            TypeGrade.NUMERIC_20 -> 20
            TypeGrade.NUMERIC_100 -> 100
        }
    }

    fun instGrade(value: Double): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return Grade(value, min, max)
    }
    fun instGrade(): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return Grade(null, min, max)
    }

    fun instGradeFromPercentage(gradePercentage: Double?): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        if (gradePercentage == null) return Grade(min, max)
        return Grade(gradePercentage * max / 100, min, max)
    }

    fun convertToActualType(grade: Grade): Grade {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        val percentage = grade.getGradePercentage() ?: return Grade(min, max)
        return Grade(percentage * max / 100, min, max)
    }
}