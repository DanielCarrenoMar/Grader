package com.app.grader.domain.types

import com.app.grader.domain.types.Grade

data class Percentage(
    private var percentage: Double
) {
    init {
        require(percentage in 0.0..100.0) { "Percentage must be between 0 and 100" }
    }
    constructor() : this(0.0)
    constructor(grade:Percentage) : this(grade.getPercentage())
    fun setPercentage(percentage:Double){
        if (percentage < 0.0) this.percentage = 0.0
        else if (percentage > 100.0) this.percentage = 100.0
        else this.percentage = percentage
    }
    fun setPercentage(percentage:Int){
        setPercentage(percentage.toDouble())
    }
    fun setPercentage(percentage: Percentage) {
        setPercentage(percentage.getPercentage())
    }
    fun getPercentage(): Double {
        return percentage
    }

    override fun toString(): String {
        if (percentage % 1.0 == 0.0) {
            return percentage.toLong().toString() // Convertir a Long y luego a String para quitar ".0"
        }

        val maxDecimalPlaces = 2
        val formattedString = String.format(java.util.Locale.US, "%.${maxDecimalPlaces}f", percentage)
        return formattedString.trimEnd('0').removeSuffix(".")
    }

    companion object {
        fun check(percentage: Double): Boolean {
            return percentage in 0.0..100.0
        }
        fun check(percentage: Int): Boolean {
            return percentage in 0..100
        }
    }
}