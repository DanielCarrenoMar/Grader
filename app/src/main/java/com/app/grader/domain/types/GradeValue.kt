package com.app.grader.domain.types

import java.util.Locale
import kotlin.math.roundToInt

data class GradeValue(
    private var value: Double?,
    private var minToPass: Double,
    private var max: Int,
    private var isDirectPercentage: Boolean
) {
    init {
        require(value == null || value!! in 0.0..max.toDouble()) { "Grade must be between 0 and $max or null. Not $value" }
        require(minToPass >= 0) { "Min must be greater than 0. Not $minToPass" }
        require(max >= 0) { "Max must be greater than 0. Not $max" }
    }

    constructor(min: Double, max: Int) : this(null, min, max, false)
    constructor(gradeValue: GradeValue) : this(gradeValue.getGrade(), gradeValue.getMinToPass(), gradeValue.getMax(), gradeValue.isDirectPercentage())
    constructor(grade: Int, min: Double, max: Int) : this(grade.toDouble(), min, max, false)
    constructor(grade: Double?, min: Double, max: Int) : this(grade, min, max, false)

    fun setValue(value: Double) {
        if (value < 0.0 || value > max) throw IllegalArgumentException("Grade value must be between 0 and $max. Not $value")
        this.value = value
    }

    fun setValue(value: Double?) {
        this.value = value;
    }

    fun setValue(value: Int) {
        setValue(value.toDouble())
    }

    fun setValue(value: GradeValue) {
        this.value = value.getGrade()
    }

    fun getGrade(): Double? {
        return value
    }

    fun getMinToPass(): Double {
        return minToPass
    }

    fun getMax(): Int {
        return max
    }

    fun isDirectPercentage(): Boolean {
        return isDirectPercentage
    }

    fun getRounded(): GradeValue {
        return GradeValue(value?.roundToInt()?.toDouble(), minToPass, max, isDirectPercentage)
    }

    fun getRoundedGrade(): Double? {
        return value?.roundToInt()?.toDouble()
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
        return grade in 0.0..max.toDouble()
    }

    fun check(grade: Int): Boolean {
        return grade in 0..max
    }

    companion object {
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
    val sum = filters.sumOf { it.getGrade()!! } / filters.count()
    return GradeValue(sum, this.first().getMinToPass(), this.first().getMax(), this.first().isDirectPercentage()).getGrade()
}
