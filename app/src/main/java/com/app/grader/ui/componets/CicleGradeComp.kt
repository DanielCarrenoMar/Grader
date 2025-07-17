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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun CircleGrade(
    modifier: Modifier = Modifier,
    grade: Double,
    fontSize: TextUnit = 16.sp,
    radius : Dp = 40.dp
) {
    if (grade < 0 || grade > 20) throw IllegalArgumentException("Grade must be between 0 and 20")
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")

    val colorOnBase = getColorForGrade(grade)
    val textGrade = when{
        grade == 0.0 -> "--"
        grade % 1 == 0.0 -> grade.toInt().toString()
        else -> ((grade * 10).roundToInt() / 10.0).toString()
    }

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
            .then(modifier)
    ) {
        Text(
            textGrade,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.surface,
            fontSize = fontSize
        )
    }
}