package com.app.grader.ui.allGrades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun AllGradesScreen(text:String,navegateToHome: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "AllGrades SCREEN", fontSize = 25.sp)
        Spacer(modifier = Modifier.weight(1f))
        //Text(text = "Texto Transmitido desde HOME: $text", fontSize = 14.sp)
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navegateToHome() }) {
            Text(text = "Navegar a la HOME")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}