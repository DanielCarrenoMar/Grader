package com.app.grader.ui.pages.editSemester

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderBack

@Composable
fun EditSemesterScreen(
    semesterId: Int,
    navegateBack: () -> Unit,
    viewModel: EditSemesterViewModel = hiltViewModel(),
    ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getCourseFromId(semesterId)
        }
    }

    HeaderBack(
        text = {
            Row (modifier = Modifier.fillMaxWidth().padding(end = 30.dp), horizontalArrangement = Arrangement.End){
                Button(onClick = {
                    viewModel.updateOrCreateCourse(semesterId)
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
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}