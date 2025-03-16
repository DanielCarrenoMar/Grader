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
import androidx.compose.ui.unit.dp
import com.app.grader.ui.theme.Neutral600
import com.app.grader.ui.theme.Secondary600
import com.app.grader.ui.theme.Success500


@Composable
fun CirclePie(){
    //debe recibir por parametro los valores de nota acumulada, los puntos perdidos y los restantes. En ese orden float
    val data = listOf(40f, 20f,40f)
    val colors = listOf(
        Success500,
        Neutral600,
        Secondary600
    )

    val colorBase = MaterialTheme.colorScheme.surface

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp * 2)
            .drawBehind {

                val total = data.sum() //
                var startAngle = -90f //

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
                    radius = 70f,
                )
            }
    ) {
        Text( text="20" ,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }


}