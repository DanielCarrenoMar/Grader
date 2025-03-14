package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.domain.model.GradeModel


@Composable
fun GradeCardComp(grade: GradeModel, navigateToGrade: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
    ) {
        Button(onClick = { navigateToGrade() }) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = grade.title, fontSize = 20.sp)
                Text(text = "porcentaje: ${grade.percentage}", fontSize = 14.sp)
            }
            CircleGrade(grade.grade)
        }
    }
}