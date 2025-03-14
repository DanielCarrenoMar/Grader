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
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.Neutral100
import com.app.grader.ui.theme.Success500
import com.app.grader.ui.theme.Warning500

fun getColorForGrade(grade: Double): Color {
    if (grade < 0 || grade > 20) throw IllegalArgumentException("Grade must be between 0 and 20")
    if (grade == 0.0) return Neutral100

    val colorHigh = Success500
    val colorMiddle = Warning500
    val colorLow = Error500

    return when {
        grade <= 10 -> {
            val adjustedFraction = (grade / 10f).toFloat()
            lerp(colorLow, colorMiddle, adjustedFraction)
        }
        else -> {
            val adjustedFraction = ((grade - 10) / 10f).toFloat()
            lerp(colorMiddle, colorHigh, adjustedFraction)
        }
    }
}

@Composable
fun CircleGrade(grade: Double, modifier: Modifier = Modifier, strokeWith: Dp = 7.dp, radius : Dp = 40.dp) {
    if (grade < 0 || grade > 20) throw IllegalArgumentException("Grade must be between 0 and 20")
    if (strokeWith < 0.dp) throw IllegalArgumentException("Stroke width must be positive")
    if (radius < 0.dp) throw IllegalArgumentException("Radius must be positive")
    if (strokeWith > radius) throw IllegalArgumentException("Stroke width must be less than radius")

    val colorBase = MaterialTheme.colorScheme.surface
    val colorOnBase = getColorForGrade(grade)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2)
            .drawBehind {
                drawCircle(
                    colorOnBase,
                    radius = radius.toPx(),
                )
            }.drawBehind {
                drawCircle(
                    colorBase,
                    radius = (radius - strokeWith).toPx(),
                )
            }
            .then(modifier)
    ) {
        Text(
            if (grade != 0.0) "$grade" else "--",
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}