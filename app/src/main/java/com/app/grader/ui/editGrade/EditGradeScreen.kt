package com.app.grader.ui.editGrade

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
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
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge
import com.app.grader.ui.theme.IconMedium
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditGradeScreen(
    gradeId: Int,
    courseId:Int,
    navegateBack: () -> Unit,
    viewModel: EditGradeViewModel = hiltViewModel(),
) {
    var expanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.courseId.intValue = courseId
            viewModel.loadGradeFromId(gradeId)
            viewModel.loadSubGradesFromGrade(gradeId)
            viewModel.loadCourseOptions(courseId)
            if (gradeId == -1) viewModel.actDefaultPercentage(courseId)
        }
    }

    HeaderBack(
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 30.dp), horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    if (viewModel.updateOrCreateGrade(gradeId)) navegateBack()
                    else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Campos rellenados automáticamente")
                        }
                    }
                }) {
                    Text(text = "Guardar")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        it,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                },
                modifier = Modifier.imePadding()
            )
        },
        navigateBack = navegateBack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(10.dp))
                EditScreenInputComp(
                    enabled = viewModel.showSubGrades.isEmpty(),
                    placeHolderText = "Agregar calificación",
                    value = viewModel.showGrade.value,
                    onValueChange = {
                        viewModel.setGrade(it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIconId = R.drawable.star_outline,
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
                    }
                )
            }
            itemsIndexed (viewModel.showSubGrades) { index, subgrade ->
                var itemHeight by remember { mutableStateOf(0.dp) }
                val animatedHeight by animateDpAsState(targetValue = itemHeight)
                val focusManager = LocalFocusManager.current
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    itemHeight = 65.dp
                    if (index == viewModel.showSubGrades.size - 1) {
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
                    value = subgrade,
                    onValueChange = {
                        viewModel.setSubGrade(index, it)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIconId = R.drawable.star_half_outline,
                    maxLength = 5,
                    suffix = {
                        IconButton(
                            onClick = { viewModel.removeSubGrade(index) },
                            modifier = Modifier.size(IconLarge)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.trash_outline),
                                contentDescription = "Delete",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            )
                        }
                    }
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
                            text = viewModel.showCourse.value.title,
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
                        viewModel.courses.value.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.showCourse.value = option
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
                    placeHolderText = viewModel.defaultPercentage.value.toString()
                        .removeSuffix(".0"),
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
                    maxLength = 6
                )
                Spacer(modifier = Modifier.height(30.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                EditScreenInputComp(
                    placeHolderText = "Agregar título (Opcional)",
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
                    placeHolderText = "Agregar descripcción (Opcional)",
                    value = viewModel.showDescription.value,
                    onValueChange = {
                        viewModel.showDescription.value = it
                        viewModel.description.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    leadingIconId = R.drawable.align_center,
                    maxLength = 200
                )
                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}