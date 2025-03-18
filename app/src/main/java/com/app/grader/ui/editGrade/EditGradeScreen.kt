package com.app.grader.ui.editGrade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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

@Composable
fun EditGradeScreen(
    gradeId: Int,
    courseId:Int,
    navegateBack: () -> Unit,
    viewModel: EditGradeViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.courseId.intValue = courseId
            viewModel.setDefaultPercentage()
            viewModel.getGradeFromId(gradeId)
        }
    }

    HeaderBack(
        title = "Editar Calificación",
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = viewModel.courseId.intValue.toString())
            EditScreenInputComp(
                placeHolderText = "Agregar titulo",
                value = viewModel.showTitle.value,
                onValueChange = {
                    viewModel.showTitle.value = it
                    viewModel.title.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cog_outline),
                        contentDescription = "Ajustes",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.size(32.dp).padding(end = 5.dp),
                    )
                }
            )
            EditScreenInputComp(
                placeHolderText = "Agregar descricción",
                value = viewModel.showDescription.value,
                onValueChange = {
                    viewModel.showDescription.value = it
                    viewModel.description.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cog_outline),
                        contentDescription = "Ajustes",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.size(32.dp).padding(end = 5.dp),
                    )
                },
            )
            EditScreenInputComp(
                placeHolderText = "Agregar calificación",
                value = viewModel.showGrade.value,
                onValueChange = {
                    viewModel.showGrade.value = it
                    viewModel.grade.value.setGrade(it.toIntOrNull() ?: 1)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cog_outline),
                        contentDescription = "Ajustes",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.size(32.dp).padding(end = 5.dp),
                    )
                },
            )
            EditScreenInputComp(
                placeHolderText = "Agregar porcentaje",
                value = viewModel.showPercentage.value,
                onValueChange = {
                    viewModel.showPercentage.value = it
                    val value = it.toIntOrNull()
                    if (value != null) viewModel.percentage.value.setPercentage(value)
                    else viewModel.setDefaultPercentage()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.cog_outline),
                        contentDescription = "Ajustes",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.size(32.dp).padding(end = 5.dp),
                    )
                },
            )
            Button(onClick = {
                viewModel.updateOrCreateGrade(gradeId)
                navegateBack()
            }) {
                Text(text = "Guardar")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}