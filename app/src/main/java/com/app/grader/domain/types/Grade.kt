package com.app.grader.domain.types

import java.util.Locale
import kotlin.math.roundToInt

data class Grade(
    private var value: Double
){
    init {
        require(value in 0.0..20.0 || value == -1.0) { "Grade must be between 0 and 20 or -1. Not $value" }
    }
    constructor() : this(-1.0)
    constructor(grade:Grade) : this(grade.getGrade())
    constructor(grade:Int) : this(grade.toDouble())
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
    fun getRounded(): Grade {
        return Grade(value.roundToInt().toDouble())
    }
    fun getRoundedGrade(): Double {
        return value.roundToInt().toDouble()
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

fun Iterable<Grade>.averageGrade(): Grade {
    if (this.none()) return Grade()
    val filters = this.filter { !it.isBlank() }
    if (filters.isEmpty()) return Grade()
    val sum = filters.sumOf { it.getGrade() } / filters.count()
    return Grade(sum)
}
