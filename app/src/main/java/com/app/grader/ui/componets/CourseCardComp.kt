package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.domain.model.CourseModel

@Composable
fun CourseCardComp(course: CourseModel, navigateToGrade: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardColors(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurface,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error,
        )
    ) {
        Button(onClick = { navigateToGrade() }) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = course.title, fontSize = 20.sp)
                Text(text = "UC: ${course.uc} ${course.average}", fontSize = 14.sp)
            }
            CircleGrade(course.average)
        }
    }
}