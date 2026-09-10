package com.app.grader.domain.types

@Deprecated("This enum is deprecated, use TypeGradeId instead. Deprecated since 2.1.0")
enum class TypeGrade() {
    NUMERIC_7_CHI,
    NUMERIC_10_ARG,
    NUMERIC_10_ESP,
    NUMERIC_10_MEX,
    NUMERIC_20,
    NUMERIC_100,
}

fun TypeGrade.toTypeGradeId(): Int = when (this) {
    TypeGrade.NUMERIC_7_CHI -> 1
    TypeGrade.NUMERIC_10_ARG -> 2
    TypeGrade.NUMERIC_10_ESP -> 2
    TypeGrade.NUMERIC_10_MEX -> 2
    TypeGrade.NUMERIC_20 -> 3
    TypeGrade.NUMERIC_100 -> 4
}