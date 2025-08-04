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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.futured.donut.compose.DonutProgress
import app.futured.donut.compose.data.DonutConfig
import app.futured.donut.compose.data.DonutModel
import app.futured.donut.compose.data.DonutSection
import com.app.grader.core.navigation.lib.getColorForGrade
import com.app.grader.domain.types.Grade

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
    val sections = if (grade.isNotBlank()) {
        listOf(
            DonutSection(
                amount = grade.getGrade().toFloat(),
                color = colorOnBase
            )
        )
    } else {
        listOf(
            DonutSection(
                amount = 20f,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        )
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
                sections = sections
            ),
            config = DonutConfig(
                gapWidthAnimationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                gapAngleAnimationSpec = spring(stiffness = Spring.StiffnessVeryLow),
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