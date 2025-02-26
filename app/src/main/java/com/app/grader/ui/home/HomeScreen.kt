package com.app.grader.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(viewModel: HomeViewModel ,navegateToAllGrades: (String) -> Unit, navigateToCourse: () -> Unit) {
    //val text: String by viewModel.text.observeAsState(initial = "")

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "HOME SCREEN", fontSize = 25.sp)
        Spacer(modifier = Modifier.weight(1f))
        //Text(text = "Texto Transmitido a AllGrades: $text", fontSize = 14.sp)
        //TextField(value = text, onValueChange = {viewModel.onTextChanged(it)})
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navigateToCourse() }) {
            Text(text = "Navegar a Materia (Course)")
        }
        Button(onClick = { navegateToAllGrades("a") }) {
            Text(text = "Navegar a todas las Notas (AllGrades)")
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}