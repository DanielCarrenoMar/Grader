package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.grader.ui.theme.Neutral600
import com.app.grader.ui.theme.Secondary600
import com.app.grader.ui.theme.Success500


@Composable
fun CirclePie(average:Double, accumulatePoints:Double, pendingPoints: Double, strokeWeight: Dp = 7.dp, radius: Dp = 40.dp){
    //debe recibir por parametro los valores de nota acumulada, los puntos perdidos y los restantes. En ese orden float
    val data = listOf(
        accumulatePoints.toFloat(),
        20 - accumulatePoints.toFloat() - pendingPoints.toFloat()
        ,pendingPoints.toFloat()
    )
    val colors = listOf(
        Success500,
        Neutral600,
        Secondary600
    )

    val colorBase = MaterialTheme.colorScheme.surface
    val textAverage = when{
        average == 0.0 -> "--"
        average % 1 == 0.0 -> average.toInt().toString()
        else -> (Math.round(average * 10) / 10.0 ).toString()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2)
            .drawBehind {

                val total = data.sum()
                var startAngle = -90f

                data.forEachIndexed { index, value ->
                    val sweepAngle = (value / total) * 360f
                    drawArc(
                        color = colors[index],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                    startAngle += sweepAngle
                }
            }
            .drawBehind {
                drawCircle(
                    colorBase,
                    radius = (radius - strokeWeight).toPx(),
                )
            }
    ) {
        Text(
            text= textAverage,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }


}