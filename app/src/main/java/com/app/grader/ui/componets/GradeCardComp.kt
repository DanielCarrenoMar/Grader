package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.domain.model.GradeModel
import java.nio.file.WatchEvent
import kotlin.rem
import kotlin.text.toInt
import kotlin.toString


@Composable
fun GradeCardComp(grade: GradeModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp)
        ){
            CircleGrade(grade.grade, radius = 20.dp)
            Column(
                modifier = Modifier.height(40.dp).padding(start = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = grade.title, style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "${if (grade.percentage % 1 == 0.0) grade.percentage.toInt() else grade.percentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
    }
}