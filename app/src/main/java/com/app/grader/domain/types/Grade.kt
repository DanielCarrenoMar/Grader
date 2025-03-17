package com.app.grader.domain.types

data class Grade(
    private var grade: Double
) {
    init {
        require(grade in 0.0..20.0) { "Grade must be between 0 and 20" }
    }
    fun setGrade(grade:Double){
        if (grade < 0.0) this.grade = 0.0
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
}
