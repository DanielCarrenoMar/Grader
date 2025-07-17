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
import kotlin.math.roundToInt

@Composable
fun CirclePie(
    average:Double,
    accumulatePoints:Double,
    pendingPoints: Double,
    strokeWith: Dp = 7.dp,
    radius: Dp = 40.dp
){
    val density = LocalDensity.current
    val textAverage = when{
        average == 0.0 -> "--"
        average % 1 == 0.0 -> average.toInt().toString()
        else -> ((average * 10).roundToInt() / 10.0 ).toString()
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
                sections = listOf(
                    DonutSection(amount = accumulatePoints.toFloat(), color = MaterialTheme.colorScheme.tertiary),
                    DonutSection(amount = pendingPoints.toFloat(), color = MaterialTheme.colorScheme.secondary),
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
            text= textAverage,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }


}