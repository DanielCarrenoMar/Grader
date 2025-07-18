package com.app.grader.ui.componets

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.futured.donut.compose.DonutProgress
import app.futured.donut.compose.data.DonutConfig
import app.futured.donut.compose.data.DonutModel
import app.futured.donut.compose.data.DonutSection
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

@Composable
fun CircleCourse(
    modifier: Modifier = Modifier,
    grade: Grade,
    strokeWith: Dp = 5.dp,
    radius : Dp = 40.dp
) {
    if (strokeWith < 0.dp) throw IllegalArgumentException("Stroke width must be positive")
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")
    if (strokeWith > radius) throw IllegalArgumentException("Stroke width must be less than radius")

    val density = LocalDensity.current
    val colorOnBase = getColorForGrade(grade)
    val textGrade = when{
        grade.isBlank() -> "--"
        else -> grade.toString()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        DonutProgress(
            modifier = Modifier
                .size(radius * 2),
            model = DonutModel(
                cap = 20f,
                masterProgress = 1f,
                gapWidthDegrees = 0f,
                gapAngleDegrees = 270f,
                strokeWidth = with(density) { strokeWith.toPx() },
                backgroundLineColor = MaterialTheme.colorScheme.onSurface,
                sections = listOf(
                    DonutSection(amount = grade.getGrade().toFloat(), color = colorOnBase),
                )
            ),
            config = DonutConfig(
                gapWidthAnimationSpec = spring(stiffness = Spring.StiffnessLow),
                gapAngleAnimationSpec = spring(stiffness = Spring.StiffnessLow),
                capAnimationSpec = spring(),
                strokeWidthAnimationSpec = spring(),
                sectionColorAnimationSpec = spring(),
                sectionAmountAnimationSpec = spring(),
                masterProgressAnimationSpec = spring(),
                backgroundLineColorAnimationSpec = spring(),
            )
        )
        Text(
            text = textGrade,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}