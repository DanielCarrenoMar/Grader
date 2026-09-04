package com.app.grader.ui.pages.editGrade

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.theme.IconLarge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGradeScreen(
    semesterId: Int,
    courseId:Int,
    gradeId: Int,
    navigateBack: () -> Unit,
    viewModel: EditGradeViewModel = hiltViewModel(),
) {
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalActivity.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.setCourseId(courseId)
        viewModel.loadGradeFromId(gradeId)
        viewModel.loadSubGradesFromGrade(gradeId)
        viewModel.loadCourseOptionsFromSemester(semesterId, courseId)
        if (gradeId == -1) viewModel.actDefaultPercentage(courseId)
    }

    HeaderBack(
        title = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Calificación",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        coroutineScope.launch {
                            val error = viewModel.submitGrade(gradeId, activity)
                            if (error == null) navigateBack()
                            else snackbarHostState.showSnackbar(error)
                        }
                    }) {
                    Text(text = if (gradeId == -1) "Crear" else "Guardar")
                }
                Spacer(Modifier.weight(0.3f))
            }
        },
        snackbarHostState = snackbarHostState,
        navigateBack = navigateBack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(10.dp))
                EditScreenInputComp(
                    enabled = uiState.subGrades.isEmpty(),
                    placeHolderText = "Agregar calificación 0-${viewModel.defaultTypeGrade.value?.max ?: 20}",
                    value = uiState.gradeValue,
                    onValueChange = {
                        viewModel.setGrade(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
leadingIconId = if (uiState.subGrades.isEmpty()) R.drawable.star_outline else R.drawable.star_half_stroke_outline,
                     isError = uiState.fieldErrors.containsKey("grade"),
                     maxLength = 5,
                    suffix = {
                        IconButton(
                            onClick = { viewModel.addSubGrade() },
                            modifier = Modifier.size(IconLarge)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.plus_outline),
                                contentDescription = "Grade",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                                modifier = Modifier
                            )
                        }
                    },
                    maxLines = 1
                )
            }
            itemsIndexed (uiState.subGrades) { index, subgrade ->
                var itemHeight by remember { mutableStateOf(0.dp) }
                val animatedHeight by animateDpAsState(targetValue = itemHeight)
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    itemHeight = 65.dp
                    if (index == uiState.subGrades.size - 1) {
                        focusRequester.requestFocus()
                    }
                }

                EditScreenInputComp(
                    modifier = Modifier
                        .animateContentSize()
                        .height(animatedHeight)
                        .padding(start = 5.dp)
                        .focusRequester(focusRequester),
                    placeHolderText = "Agregar calificación",
                    value = uiState.subGradeTexts.getOrNull(index).orEmpty(),
                    onValueChange = {
                        if (index in viewModel.uiState.value.subGrades.indices) viewModel.setSubGrade(index, it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
leadingIconId = R.drawable.star_half_outline,
                     isError = uiState.fieldErrors.containsKey("subgrade:$index"),
                     maxLength = 5,
                    suffix = {
                        IconButton(
                            onClick = {
                                if (index in viewModel.uiState.value.subGrades.indices) viewModel.removeSubGrade(index)
                            },
                            modifier = Modifier.size(IconLarge)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.trash_outline),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            )
                        }
                    },
                    maxLines = 1
                )
            }

            item {
                Card(
                    onClick = { expanded = true },
                    colors = CardColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.education_cap_outline),
                            contentDescription = "Course",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier
                                .size(IconLarge),
                        )
                        Text(
                            text = uiState.course.title,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 20.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        uiState.courses.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.setCourse(option)
                                    viewModel.setCourseId(option.id)
                                    expanded = false
                                },
                                text = {
                                    Text(option.title, style = MaterialTheme.typography.bodyLarge)
                                }
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                EditScreenInputComp(
                    placeHolderText = viewModel.defaultPercentage.toString()
                        .removeSuffix(".0"),
                    value = uiState.percentage,
                    onValueChange = { viewModel.setPercentage(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                     leadingIconId = R.drawable.weight_outline,
                     isError = uiState.fieldErrors.containsKey("percentage"),
                     suffix = {
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    },
                    maxLength = 6,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                EditScreenInputComp(
                    placeHolderText = "Agregar título (Opcional)",
                    value = uiState.title,
                    onValueChange = {
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
                    placeHolderText = "Agregar descripcción (Opcional)",
                    value = uiState.description,
                    onValueChange = {
                        viewModel.setDescription(it)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    leadingIconId = R.drawable.align_start,
                )
            }
        }
    }
}