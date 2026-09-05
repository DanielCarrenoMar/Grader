package com.app.grader.domain.types

import java.util.Locale
import kotlin.math.roundToInt

data class GradeValue(
    private var value: Double?,
    private var minToPass: Double,
    private var max: Double
) {
    init {
        require(value == null || value!! in 0.0..max) { "La calificación debe estar entre 0 y $max o estar vacía. No $value" }
        require(minToPass >= 0) { "El mínimo para pasar debe ser mayor que 0. No $minToPass" }
        require(max >= 0) { "El máximo debe ser mayor que 0. No $max" }
    }
    constructor(value: Double?, minToPass: Double?, max: Double) : this(value,
        minToPass ?: (max / 2), max)
    constructor(value: Double?, minToPass: Double?, max: Int) : this(value, minToPass, max.toDouble())
    constructor() : this(null, 0.0, 0.0)

    constructor(minToPass: Double, max: Double) : this(null, minToPass, max)
    constructor(minToPass: Double, max: Int) : this(null, minToPass, max.toDouble())
    constructor(gradeValue: GradeValue) : this(gradeValue.getValue(), gradeValue.getMinToPass(), gradeValue.getMax())

    fun setValue(value: Double) {
        if (value !in 0.0..max) throw IllegalArgumentException("La calificación debe estar entre 0 y $max. No $value")
        this.value = value
    }

    fun setValue(value: Double?) {
        this.value = value;
    }

    fun setValue(value: Int) {
        setValue(value.toDouble())
    }

    fun setValue(value: GradeValue) {
        this.value = value.getValue()
    }

    fun getValue(): Double? {
        return value
    }

    fun getMinToPass(): Double {
        return minToPass
    }

    fun getMax(): Double {
        return max
    }

    fun getRounded(): GradeValue {
        return GradeValue(value?.roundToInt()?.toDouble(), minToPass, max)
    }

    fun getGradePercentage(): Double? {
        return if (isBlank()) null else (value!! / max) * 100.0
    }

    fun isFail(): Boolean {
        if (isBlank()) return  false
        return value!! < minToPass
    }

    fun isFailValue(grade: Double): Boolean {
        return grade < minToPass
    }

    fun getGradeRating(): Float {
        return ((value ?: 0.0) / max).toFloat()
    }

    fun isBlank(): Boolean {
        return value == null
    }

    fun isNotBlank(): Boolean {
        return !isBlank()
    }

    fun setBlank() {
        value = null
    }

    override fun toString(): String {
        if (isBlank()) {
            return ""
        }
        return formatText(value!!)
    }

    fun check(grade: Double): Boolean {
        return grade in 0.0..max
    }

    companion object {
        fun createFromGradePercentage(gradePercentage: Double?, minToPass: Double?, max: Double): GradeValue {
            if (gradePercentage == null) return GradeValue(null, minToPass, max)
            val gradeValue = (gradePercentage / 100.0) * max
            return GradeValue(gradeValue, minToPass, max)
        }

        fun createFromGradePercentage(gradePercentage: Double?, minToPass: Double?, max: Int): GradeValue {
            return createFromGradePercentage(gradePercentage, minToPass, max.toDouble())
        }
        fun formatText(grade: Double): String {
            return if (grade % 1.0 == 0.0) {
                grade.toLong().toString()
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

fun Iterable<GradeValue>.averageGrade(): Double? {
    if (this.none()) return null
    val filters = this.filter { !it.isBlank() }
    if (filters.isEmpty()) return null
    val sum = filters.sumOf { it.getValue()!! } / filters.count()
    return GradeValue(sum, this.first().getMinToPass(), this.first().getMax()).getValue()
}
