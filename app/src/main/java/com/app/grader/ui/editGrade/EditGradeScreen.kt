package com.app.grader.ui.editGrade

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.theme.IconLarge

@Composable
fun EditGradeScreen(
    gradeId: Int,
    courseId:Int,
    navegateBack: () -> Unit,
    viewModel: EditGradeViewModel = hiltViewModel(),
) {
    var expanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.courseId.intValue = courseId
            viewModel.actDefaultPercentage()
            viewModel.getGradeFromId(gradeId)
            viewModel.getAllCourses()
        }
    }

    HeaderBack(
        text = {
            Row (modifier = Modifier.fillMaxWidth().padding(end = 30.dp), horizontalArrangement = Arrangement.End){
                Button(onClick = {
                    if (viewModel.updateOrCreateGrade(gradeId)) navegateBack()
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
                placeHolderText = "Agregar calificación",
                value = viewModel.showGrade.value,
                onValueChange = {
                    viewModel.showGrade.value = it
                    viewModel.grade.value.setGrade(it.toIntOrNull() ?: 1)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIconId = R.drawable.star_outline,
            )
            Card (
                onClick = { expanded = true },
                colors = CardColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ){
                    Image(
                        painter = painterResource(id = R.drawable.education_cap_outline),
                        contentDescription = "Course",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .size(IconLarge),
                    )
                    Text(
                        text = "Seleccionar curso ${viewModel.courseId.intValue}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 20.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    viewModel.courses.value.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                viewModel.setCurseId(option.id)
                                expanded = false
                            },
                            text = {
                                Text(option.title, style = MaterialTheme.typography.bodyMedium)
                            }
                        )
                    }
                }
            }
            HorizontalDivider( modifier = Modifier.alpha(0.5f))
            EditScreenInputComp(
                placeHolderText = viewModel.defaultPercentage.value.toString(),
                value = viewModel.showPercentage.value,
                onValueChange = { viewModel.setPercentage(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIconId = R.drawable.weight_outline,
                suffix = {
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                },
            )
            Spacer(modifier = Modifier.height(30.dp))
            HorizontalDivider( modifier = Modifier.alpha(0.5f))
            EditScreenInputComp(
                placeHolderText = "Sin titulo",
                value = viewModel.showTitle.value,
                onValueChange = {
                    viewModel.showTitle.value = it
                    viewModel.title.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIconId = R.drawable.bookmark_outline
            )
            EditScreenInputComp(
                placeHolderText = "Sin descricción",
                value = viewModel.showDescription.value,
                onValueChange = {
                    viewModel.showDescription.value = it
                    viewModel.description.value = it
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                leadingIconId = R.drawable.align_center,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}