package com.app.grader.ui.editCourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.home.HomeViewModel

@Composable
fun EditCourseScreen(
    courseId: Int,
    navegateBack: () -> Unit,
    viewModel: EditCourseViewModel = hiltViewModel(),
    ) {

    HeaderBack(
        title = "Editar Asignatura",
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "EditCourse SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
            TextField(value = viewModel.title.value, onValueChange = { viewModel.title.value = it })
            TextField(value = viewModel.description.value, onValueChange = { viewModel.description.value = it })
            TextField(value = viewModel.uc.intValue.toString(), onValueChange = { viewModel.uc.intValue = it.toIntOrNull() ?: 0 })
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                viewModel.saveOrCreateCourse(courseId)
                navegateBack()
            }) {
                Text(text = "Guardar")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}