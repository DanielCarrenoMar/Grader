package com.app.grader.core.lib

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.app.grader.domain.types.Grade
import com.app.grader.ui.theme.Neutral100

fun interpolateColors(colors: List<Color>, fraction: Float): Color {
    require(colors.isNotEmpty()) { "Color list must not be empty" }
    require(fraction in 0f..1f) { "Fraction must be between 0 and 1" }

    if (colors.size == 1) return colors.first()

    val position = (fraction * (colors.size - 1)).coerceIn(0f, (colors.size - 1).toFloat())
    val index = position.toInt()
    val localFraction = position - index

    return if (index == colors.size - 1) {
        colors.last()
    } else {
        lerp(colors[index], colors[index + 1], localFraction)
    }
}

fun getColorForGrade(grade: Grade): Color {
    if (grade.isBlank()) return Neutral100

    return  interpolateColors(
        listOf(
            Color(0xFFF28705),
            Color(0xFFF2B705),
            Color(0xFF8BC441),
            Color(0xFF4CA649),
        ),
        (grade.getGrade() / 20).toFloat()
    )
}