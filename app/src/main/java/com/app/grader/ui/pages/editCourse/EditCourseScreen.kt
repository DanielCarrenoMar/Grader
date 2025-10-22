package com.app.grader.ui.pages.editCourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.app.grader.ui.theme.Shadow50
import kotlinx.coroutines.launch
import java.security.InvalidParameterException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseScreen(
    semesterId: Int,
    courseId: Int,
    navigateBack: () -> Unit,
    navigateToEditGrade: (Int, Int, Int) -> Unit,
    viewModel: EditCourseViewModel = hiltViewModel(),
    ) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    // Estado para mostrar el diálogo informativo de "Peso"
    var showPesoInfoDialog by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.getCourseFromId(courseId)
        }
    }

    if (showPesoInfoDialog) {
        AlertDialog(
            onDismissRequest = { showPesoInfoDialog = false },
            title = { Text("¿Qué significa Peso?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Representa la ponderación que tendrá en el cálculo del promedio.", style = MaterialTheme.typography.labelMedium) },
            confirmButton = {
                TextButton(
                    onClick = { showPesoInfoDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Shadow50
                    ),
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    HeaderBack(
        snackbarHostState = snackbarHostState,
        title = {
            Row (
                modifier = Modifier.fillMaxWidth().padding(end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Asignatura",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        try {
                            viewModel.updateOrCreateCourse(
                                semesterId,
                                courseId,
                                onCreate = { newCourseId ->
                                    navigateBack()
                                    if (semesterId != -1) navigateToEditGrade(semesterId, newCourseId.toInt(), -1)
                                },
                                onUpdate = {
                                    navigateBack()
                                }
                            )

                        }catch (e: InvalidParameterException){
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(e.message?: "Error desconocido")
                            }
                        }
                    }) {
                    Text(text = if (courseId == -1) "Crear" else "Guardar")
                }
                Spacer(Modifier.weight(0.3f))
            }
        },
        navigateBack = navigateBack
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
                    viewModel.setTitle(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIconId = R.drawable.bookmark_outline,
                maxLength = 50,
                maxLines = 1
            )
            EditScreenInputComp(
                placeHolderText = "Ponderación",
                value = viewModel.showUc.value,
                onValueChange = {
                    viewModel.showUc.value = it
                    viewModel.uc.intValue = it.toIntOrNull() ?: 1
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIconId = R.drawable.chart_pie,
                suffix = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Peso",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                        IconButton(onClick = { showPesoInfoDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.info_outline),
                                contentDescription = "Información sobre Peso",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                maxLength = 3,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}