package com.app.grader.domain.types

import com.app.grader.domain.types.Grade
import kotlin.math.roundToInt

// Average ahora extiende de Grade para heredar su lógica y métodos

class Average : Grade {
    constructor() : super()
    constructor(average: Double) : super(average)
    constructor(average: Int) : super(average)
    constructor(average: Grade) : super(average)

    override fun getGrade(): Double {
        return if (true) super.getGrade().roundToInt().toDouble() else super.getGrade()
    }
}
