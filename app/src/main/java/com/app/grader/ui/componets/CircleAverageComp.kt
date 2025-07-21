package com.app.grader.ui.componets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.app.grader.domain.types.Grade

@Composable
fun CircleAverage(
    average: Grade,
    accumulatePoints:Double,
    pendingPoints: Double,
    strokeWith: Dp = 7.dp,
    radius: Dp = 40.dp
){
    val density = LocalDensity.current

    val animatedAverage by animateFloatAsState(
        targetValue = average.getGrade().toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "averageAnimation"
    )

    val textAverage = when{
        average.isBlank() -> "--"
        else -> Grade.formatText(animatedAverage)
    }
    val sections = if (average.isNotBlank()) {
        listOf(
            DonutSection(amount = accumulatePoints.toFloat(), color = MaterialTheme.colorScheme.tertiary),
            DonutSection(amount = pendingPoints.toFloat(), color = MaterialTheme.colorScheme.secondary),
        )
    } else {
        listOf(
            DonutSection(amount = 0f, color = MaterialTheme.colorScheme.tertiary),
            DonutSection(amount = 0f, color = MaterialTheme.colorScheme.secondary),
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2)
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
            text= textAverage,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }


}