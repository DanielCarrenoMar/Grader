package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

fun getColorForGrade(grade: Double): Color {
    if (grade < 0 || grade > 20) throw IllegalArgumentException("Grade must be between 0 and 20")
    if (grade == 0.0) return Neutral100

    return  interpolateColors(
        listOf(
            Color(0xFFF28705),
            Color(0xFFF2B705),
            Color(0xFF8BC441),
            Color(0xFF4CA649),
        ),
        (grade / 20).toFloat()
    )
}

@Composable
fun CircleCourse(
    modifier: Modifier = Modifier,
    grade: Double,
    strokeWith: Dp = 7.dp,
    radius : Dp = 40.dp
) {
    if (grade < 0 || grade > 20) throw IllegalArgumentException("Grade must be between 0 and 20")
    if (strokeWith < 0.dp) throw IllegalArgumentException("Stroke width must be positive")
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")
    if (strokeWith > radius) throw IllegalArgumentException("Stroke width must be less than radius")

    val colorBase = MaterialTheme.colorScheme.surface
    val colorOnBase = getColorForGrade(grade)
    val textGrade = when{
        grade == 0.0 -> "--"
        grade % 1 == 0.0 -> grade.toInt().toString()
        else -> (Math.round(grade * 10) / 10.0 ).toString()
    }

    val data = listOf(
        grade.toFloat(),
        20 - grade.toFloat(),
    )
    val colors = listOf(
        colorOnBase,
        MaterialTheme.colorScheme.onSurface,
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2)
            .drawBehind {
                drawCircle(
                    colorOnBase,
                    radius = radius.toPx(),
                )
            }
            .drawBehind {
                drawCircle(
                    colorBase,
                    radius = (radius - strokeWith).toPx(),
                )
            }
            .then(modifier)
    ) {
        Text(
            text = textGrade,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}