package com.app.grader.ui.editGrade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.app.grader.ui.componets.HeaderBack

@Composable
fun EditGradeScreen(navegateBack: () -> Unit) {

    HeaderBack(
        title = "Asignatura",
        navigateBack = navegateBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "EditGrade SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}