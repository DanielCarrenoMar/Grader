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
import com.app.grader.domain.types.Grade
import kotlin.math.roundToInt

@Composable
fun CircleGrade(
    modifier: Modifier = Modifier,
    grade: Grade,
    fontSize: TextUnit = 16.sp,
    radius : Dp = 40.dp
) {
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")

    val colorOnBase = getColorForGrade(grade)
    val textGrade = when{
        grade.isBlank() -> "--"
        else -> grade.toString()
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