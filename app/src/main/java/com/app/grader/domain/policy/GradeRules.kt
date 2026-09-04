package com.app.grader.domain.policy

import javax.inject.Inject

class GradeRules @Inject constructor() {

    companion object {
        private const val MAX_PERCENTAGE = 100.0
        const val WEIGHTING_OVERFLOW_MESSAGE = "La suma de las notas excede el 100%"

        fun isWeightingOverflow(message: String?): Boolean = message == WEIGHTING_OVERFLOW_MESSAGE
    }

    fun validateSumNotExceed100(currentSumWithoutThis: Double, newPercentage: Double) {
        if (currentSumWithoutThis + newPercentage > MAX_PERCENTAGE) {
            throw IllegalArgumentException(WEIGHTING_OVERFLOW_MESSAGE)
        }
    }
}
