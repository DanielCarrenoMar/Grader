package com.app.grader.domain.types

import java.util.Locale
import kotlin.math.roundToInt

data class Grade(
    private var value: Double,
    private var min:Double,
    private var max:Int,
){
    init {
        require(value in 0.0..20.0 || value == -1.0) { "Grade must be between 0 and 20 or -1. Not $value" }
        require(min > 0) { "Min must be greater than 0. Not $min" }
        require(max > 0) { "Max must be greater than 0. Not $max" }
    }
    constructor(min:Double, max:Int) : this(-1.0, min, max)
    constructor(grade:Grade) : this(grade.getGrade(), grade.getMin(), grade.getMax())
    constructor(grade:Int, min:Double, max:Int) : this(grade.toDouble(), min, max)
    fun setGrade(grade:Double){
        if (grade < 0.0) this.value = -1.0
        else if (grade > 20.0) this.value = 20.0
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
    fun getMin(): Double {
        return min
    }
    fun getMax(): Int {
        return max
    }
    fun getRounded(): Grade {
        return Grade(value.roundToInt().toDouble(), min, max)
    }
    fun getRoundedGrade(): Double {
        return value.roundToInt().toDouble()
    }

    fun isFail(): Boolean {
        return value < min
    }

    fun getGradeRating(): Float {
        return (value / max).toFloat()
    }

    fun isBlank(): Boolean{
        return value == -1.0
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

    companion object {
        fun check(grade: Double): Boolean {
            return grade in 0.0..20.0
        }
        fun check(grade: Int): Boolean {
            return grade in 0..20
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
    return Grade(sum, this.first().getMin(), this.first().getMax()).getGrade()
}
