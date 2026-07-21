package com.app.grader.core.appConfig

enum class TypeGrade() {
    NUMERIC_7_CHI,
    NUMERIC_10_ARG,
    NUMERIC_10_ESP,
    NUMERIC_10_MEX,
    NUMERIC_20,
    NUMERIC_100,
}

fun TypeGrade.toBaseAt(): Int = when (this) {
    TypeGrade.NUMERIC_7_CHI -> 7
    TypeGrade.NUMERIC_10_ARG -> 10
    TypeGrade.NUMERIC_10_ESP -> 10
    TypeGrade.NUMERIC_10_MEX -> 10
    TypeGrade.NUMERIC_20 -> 20
    TypeGrade.NUMERIC_100 -> 100
}

fun TypeGrade.toTypeGradeId(): Int = when (this) {
    TypeGrade.NUMERIC_7_CHI -> 1
    TypeGrade.NUMERIC_10_ARG -> 2
    TypeGrade.NUMERIC_10_ESP -> 2
    TypeGrade.NUMERIC_10_MEX -> 2
    TypeGrade.NUMERIC_20 -> 3
    TypeGrade.NUMERIC_100 -> 4
}