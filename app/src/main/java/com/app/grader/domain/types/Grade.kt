package com.app.grader.domain.types

import kotlin.div

data class Grade(
    private var grade: Double
){
    init {
        require(grade in 1.0..20.0) { "Grade must be between 1 and 20" }
    }
    fun setGrade(grade:Double){
        if (grade < 1.0) this.grade = 1.0
        else if (grade > 20.0) this.grade = 20.0
        else this.grade = grade
    }
    fun setGrade(grade:Int){
        setGrade(grade.toDouble())
    }
    fun getGrade(): Double {
        return grade
    }

    override fun toString(): String {
        return grade.toString()
    }

    companion object {
        fun check(grade: Double): Boolean {
            return grade in 1.0..20.0
        }
        fun check(grade: Int): Boolean {
            return grade in 1..20
        }
    }
}

fun Iterable<Grade>.averageGrade(): Double {
    if (this.none()) return 0.0
    return this.sumOf { it.getGrade() } / this.count()
}
