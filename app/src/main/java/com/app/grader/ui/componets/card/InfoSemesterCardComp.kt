package com.app.grader.ui.componets.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.types.GradeValue
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.componets.chart.LineChartAverage

@Composable
fun InfoSemesterCard(
    average: GradeValue,
    grades: List<GradeModel>,
    coursesLength: Int,
    totalWeight: Int
) {
    CardContainer { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(1f)
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
                        val gradeSeries =
                            grades.filter { it.gradeValue.isNotBlank() }.map { it.gradeValue.getGrade() ?: 0.0 }
                                .reversed()

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$coursesLength",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (coursesLength == 1) " asignatura" else " asignaturas",
                    modifier = Modifier
                        .padding(start = 2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "$totalWeight",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Peso",
                    modifier = Modifier
                        .padding(start = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}