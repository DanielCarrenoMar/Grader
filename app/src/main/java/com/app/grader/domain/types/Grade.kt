package com.app.grader.domain.types

import java.util.Locale
import kotlin.math.roundToInt

data class Grade(
    private var value: Double,
    private var minToPass:Double,
    private var max:Int,
){
    init {
        require(value in 0.0..max.toDouble() || value == -1.0) { "Grade must be between 0 and $max or -1. Not $value" }
        require(minToPass >= 0) { "Min must be greater than 0. Not $minToPass" }
        require(max >= 0) { "Max must be greater than 0. Not $max" }
    }
    constructor(min:Double, max:Int) : this(-1.0, min, max)
    constructor(grade:Grade) : this(grade.getGrade(), grade.getMinToPass(), grade.getMax())
    constructor(grade:Int, min:Double, max:Int) : this(grade.toDouble(), min, max)
    fun setGrade(grade:Double){
        if ((grade < 0.0 && !isBlankValue(grade)
                    || grade > max)) throw IllegalArgumentException("Grade must be between 0 and $max or -1. Not $grade")
        else this.value = grade
    }
    fun setGrade(grade:Int){
        setGrade(grade.toDouble())
    }
    fun setGrade(grade:Grade){
        setGrade(grade.getGrade())
    }
    fun getGrade(): Double {
        return value
    }
    fun getMinToPass(): Double {
        return minToPass
    }
    fun getMax(): Int {
        return max
    }
    fun getRounded(): Grade {
        return Grade(value.roundToInt().toDouble(), minToPass, max)
    }
    fun getRoundedGrade(): Double {
        return value.roundToInt().toDouble()
    }

    fun getGradePercentage(): Double {
        return if (isBlank()) -1.0 else (value / max) * 100.0
    }

    fun isFail(): Boolean {
        return value < minToPass
    }
    fun isFailValue(grade: Double): Boolean {
        return grade < minToPass
    }

    fun getGradeRating(): Float {
        return (value / max).toFloat()
    }

    fun isBlank(): Boolean{
        return isBlankValue(value)
    }

    fun isNotBlank(): Boolean {
        return !isBlank()
    }

    fun setBlank() {
        value = -1.0
    }

    override fun toString(): String {
        if (isBlank()) {
            return ""
        }

        return formatText(value)
    }

    fun check(grade: Double): Boolean {
        return grade in 0.0..max.toDouble()
    }
    fun check(grade: Int): Boolean {
        return grade in 0..max
    }

    companion object {
        fun isBlankValue(value:Double): Boolean{
            return value == -1.0
        }

        fun formatText(grade: Double): String {
            return if (grade % 1.0 == 0.0) {
                grade.toLong().toString() // Convertir a Long y luego a String para quitar ".0"
            } else {
                val maxDecimalPlaces = 2
                val formattedString = String.format(Locale.US, "%.${maxDecimalPlaces}f", grade)
                formattedString.trimEnd('0').removeSuffix(".")
            }
        }

        fun formatText(grade: Float): String {
            return formatText(grade.toDouble())
        }
    }
}

fun Iterable<Grade>.averageGrade(): Double {
    if (this.none()) return 0.0
    val filters = this.filter { !it.isBlank() }
    if (filters.isEmpty()) return 0.0
    val sum = filters.sumOf { it.getGrade() } / filters.count()
    return Grade(sum, this.first().getMinToPass(), this.first().getMax()).getGrade()
}
