package com.app.grader.ui.componets.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.app.grader.core.lib.getColorForGrade
import com.app.grader.domain.types.GradeValue

@Composable
fun CircleGrade(
    modifier: Modifier = Modifier,
    gradeValue: GradeValue,
    fontSize: TextUnit = 16.sp,
    radius : Dp = 40.dp,
    isPercentage: Boolean = false,
) {
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")

    val colorOnBase = getColorForGrade(gradeValue)
    val textGrade = when{
        gradeValue.isBlank() -> "--"
        else -> gradeValue.toString()
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
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                textGrade,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.surface,
                fontSize = fontSize
            )
            if (isPercentage) {
                Text(
                    text = "%",
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = fontSize * 0.65f
                )
            }
        }
    }
}