package com.app.grader.ui.editCourse

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.EditScreenInputComp
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
        text = {
            Row (modifier = Modifier.fillMaxWidth().padding(end = 30.dp), horizontalArrangement = Arrangement.End){
                Button(onClick = {
                    viewModel.updateOrCreateCourse(courseId)
                    navegateBack()
                }) {
                    Text(text = "Guardar")
                }
            }
        },
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            EditScreenInputComp(
                placeHolderText = "Sin título",
                value = viewModel.showTitle.value,
                onValueChange = {
                    viewModel.showTitle.value = it
                    viewModel.title.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIconId = R.drawable.bookmark_outline,
                maxLength = 50
            )
            EditScreenInputComp(
                placeHolderText = "Unidades de Credito",
                value = viewModel.showUc.value,
                onValueChange = {
                    viewModel.showUc.value = it
                    viewModel.uc.intValue = it.toIntOrNull() ?: 1
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIconId = R.drawable.chart_pie,
                suffix = {
                    Text(
                        text = "UC",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                },
                maxLength = 3
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}