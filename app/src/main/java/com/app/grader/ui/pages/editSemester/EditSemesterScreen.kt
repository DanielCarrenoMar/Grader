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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.ButtonState
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSemesterScreen(
    semesterId: Int,
    navigateBack: () -> Unit,
    viewModel: EditSemesterViewModel = hiltViewModel(),
    ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.getSemesterFromId(semesterId)
        }
    }

    HeaderBack(
        title = {
            Text(
                text = "Registro",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingItem = {
            ButtonState(
                text = if (semesterId == -1) "Crear" else "Guardar",
                isLoading = viewModel.isSaving.value,
                modifier = Modifier.widthIn(min = 88.dp, max = 120.dp),
                onClick = {
                    viewModel.updateOrCreateSemester(
                        semesterId,
                        onCreate = {navigateBack()},
                        onUpdate = {navigateBack()}
                    )
                }
            )
        },
        navigateBack = navigateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            EditScreenInputComp(
                placeHolderText = "Agregar título (opcional)",
                value = viewModel.showTitle.value,
                onValueChange = {
                    viewModel.showTitle.value = it
                    viewModel.setTitle(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIconId = R.drawable.bookmark_outline,
                maxLength = 50,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}