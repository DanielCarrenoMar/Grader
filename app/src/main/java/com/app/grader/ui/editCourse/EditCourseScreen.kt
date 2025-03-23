package com.app.grader.ui.editCourse

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.home.HomeViewModel

@Composable
fun EditCourseScreen(
    courseId: Int,
    navegateBack: () -> Unit,
    viewModel: EditCourseViewModel = hiltViewModel(),
    ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getCourseFromId(courseId)
        }
    }

    HeaderBack(
        text = { },
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "EditCourse SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(0.2f))
            Text(text = "Titulo", style = MaterialTheme.typography.labelMedium)
            TextField(
                value = viewModel.showTitle.value,
                onValueChange = {
                    viewModel.showTitle.value = it
                    viewModel.title.value = it
                }
            )
            Text(text = "UC", style = MaterialTheme.typography.labelMedium)
            TextField(
                value = viewModel.showUc.value,
                onValueChange = {
                    viewModel.showUc.value = it
                    viewModel.uc.intValue = it.toIntOrNull() ?: 1
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.onFocusChanged { focusState ->
                   if (!focusState.isFocused) viewModel.showUc.value = viewModel.uc.intValue.toString()
                }
            )
            Button(onClick = {
                viewModel.updateOrCreateCourse(courseId)
                navegateBack()
            }) {
                Text(text = "Guardar")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}