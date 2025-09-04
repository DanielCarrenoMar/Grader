package com.app.grader.ui.pages.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.FloatingMenuComp
import com.app.grader.ui.componets.FloatingMenuCompItem
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.componets.card.CardContainer
import com.app.grader.ui.componets.card.CurrentRecordSemesterCard
import com.app.grader.ui.componets.card.RecordSemesterCard
import com.app.grader.ui.componets.chart.LineChartAverage
import kotlinx.coroutines.launch
import java.security.InvalidParameterException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToEditSemester: (Int) -> Unit,
    navigateToRecordSemester: (Int) -> Unit,
    navigateToTransferSemester: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllSemestersAndCalTotalAverage()
            viewModel.getCurrentSemester()
            viewModel.getAllGrades()
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteSelectSemester({ viewModel.getAllSemestersAndCalTotalAverage() }) },
            { showDeleteConfirmation = false }
        )
    }

    HeaderMenu(
        "Registro Académico",
        navigateToHome,
        navigateToAllGrades,
        null,
        navigateToConfig,
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRecordCard(viewModel.totalAverage.value, viewModel.grades.value)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }

            if (viewModel.isLoading.value) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            } else {
                item {
                    CurrentRecordSemesterCard(
                        viewModel.currentSemester.value,
                        { navigateToHome() },
                        {
                            try {
                                viewModel.validActualSemesterToTransfer()
                                navigateToTransferSemester()
                            } catch (e: InvalidParameterException) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(e.message ?: "Error desconocido")
                                }
                            }
                        }
                    )
                }
                items(viewModel.semesters.value) { semester ->
                    RecordSemesterCard(
                        semester,
                        { navigateToRecordSemester(semester.id) },
                        {
                            viewModel.selectDeleteSemester(semester.id)
                            showDeleteConfirmation = true
                        },
                        { navigateToEditSemester(semester.id) },
                    )
                }
            }
        }

        FloatingMenuComp(
            listOf(
                FloatingMenuCompItem(
                    "Crear nuevo",
                    R.drawable.star_outline
                ) { navigateToEditSemester(-1) },
                FloatingMenuCompItem(
                    "Guardar actual",
                    R.drawable.education_cap_outline
                ) {
                    try {
                        viewModel.validActualSemesterToTransfer()
                        navigateToTransferSemester()
                    } catch (e: InvalidParameterException) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(e.message ?: "Error desconocido")
                        }
                    }
                },
            )
        )
    }
}

@Composable
fun InfoRecordCard(average: Grade, grades: List<GradeModel>) {
    CardContainer { innerPading ->
        Column(
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(200.dp),
        ) {
            TitleIcon(
                iconName = "chart mixed",
                iconId = R.drawable.chart_mixed
            ) {
                Text(text = "Promedio Final", style = MaterialTheme.typography.labelLarge)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (average.isNotBlank()) average.toString() else "--",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 48.sp,
                )
                if (grades.isNotEmpty()) {
                    LineChartAverage(
                        gradeSeries = grades.map { it.grade.getGrade() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .alpha(0.7f)
                    )
                }
            }
        }
    }
}