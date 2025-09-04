package com.app.grader.ui.componets.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.componets.chart.LineChartAverage

@Composable
fun InfoSemesterCard(average: Grade, grades: List<GradeModel>) {
    CardContainer { innerPading ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TitleIcon(
                    iconName = "chart mixed",
                    iconId = R.drawable.chart_mixed
                ) {
                    Text(text = "Progresión", style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val gradeSeries = grades.filter { it.grade.isNotBlank() }.map { it.grade.getGrade() }

                    if (gradeSeries.isNotEmpty()) LineChartAverage(
                        gradeSeries,
                        Modifier.fillMaxSize()
                    )
                    else Text(
                        text = "No hay calificaciones",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Box(
                modifier = Modifier.weight(0.9f),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (average.isNotBlank()) average.toString() else "--",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 32.sp,
                    )
                    Text(
                        text = "Tu Promedio",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}