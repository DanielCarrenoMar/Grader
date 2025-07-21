package com.app.grader.domain.types

import java.util.Locale

data class Grade(
    private var grade: Double
){
    init {
        require(grade in 0.0..20.0 || grade == -1.0) { "Grade must be between 0 and 20 or -1. Not $grade" }
    }
    constructor() : this(-1.0)
    constructor(grade:Grade) : this(grade.getGrade())
    fun setGrade(grade:Double){
        if (grade < 0.0) this.grade = -1.0
        else if (grade > 20.0) this.grade = 20.0
        else this.grade = grade
    }
    fun setGrade(grade:Int){
        setGrade(grade.toDouble())
    }
    fun setGrade(grade:Grade){
        setGrade(grade.getGrade())
    }
    fun getGrade(): Double {
        return grade
    }

    fun isBlank(): Boolean{
        return grade == -1.0
    }

    fun isNotBlank(): Boolean {
        return !isBlank()
    }

    fun setBlank() {
        grade = -1.0
    }

    override fun toString(): String {
        if (isBlank()) {
            return ""
        }

        return formatText(grade)
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
