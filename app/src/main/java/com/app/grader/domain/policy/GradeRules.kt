package com.app.grader.domain.policy

import javax.inject.Inject

class GradeRules @Inject constructor() {

    companion object {
        private const val MAX_PERCENTAGE = 100.0
    }

    fun validateSumNotExceed100(currentSumWithoutThis: Double, newPercentage: Double) {
        if (currentSumWithoutThis + newPercentage > MAX_PERCENTAGE) {
            throw IllegalArgumentException("La suma de las notas excede el 100%")
        }
    }
}
