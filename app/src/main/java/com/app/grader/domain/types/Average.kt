package com.app.grader.domain.types

import kotlin.math.roundToInt

class Average : Grade {
    constructor() : super()
    constructor(average: Double) : super(average)
    constructor(average: Int) : super(average)
    constructor(average: Grade) : super(average)

    fun getAverage(): Double {
        return if (true) super.getGrade().roundToInt().toDouble() else super.getGrade()
    }
    fun setAverage(average: Int) {
        super.setGrade(average.toDouble())
    }
    fun setAverage(average: Double) {
        super.setGrade(average)
    }
    fun setAverage(average: Grade) {
        super.setGrade(average.getGrade())
    }
}
