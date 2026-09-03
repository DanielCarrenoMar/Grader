package com.app.grader.core.appConfig

import com.app.grader.domain.types.GradeValue
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

    fun instGrade(value: Double): GradeValue {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return GradeValue(value, min, max)
    }
    fun instGrade(): GradeValue {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        return GradeValue(null, min, max)
    }

    fun instGradeFromPercentage(gradePercentage: Double?): GradeValue {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        if (gradePercentage == null) return GradeValue(min, max)
        return GradeValue(gradePercentage * max / 100, min, max)
    }

    fun convertToActualType(gradeValue: GradeValue): GradeValue {
        val min = getMinFromTypeGrade()
        val max = getMaxFromTypeGrade()
        val percentage = gradeValue.getGradePercentage() ?: return GradeValue(min, max)
        return GradeValue(percentage * max / 100, min, max)
    }
}