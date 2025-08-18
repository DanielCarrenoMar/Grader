package com.app.grader.ui.pages.allGrades

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.GradeBottomSheet
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.card.CardContainer
import com.app.grader.ui.componets.card.GradeCardComp
import com.app.grader.ui.theme.IconMedium
import com.app.grader.ui.theme.IconSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGradesScreen(
    navigateToHome: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToRecord: () -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: AllGradesViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllGradesWithCourses()
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu(
        "Todas las Calificaciones",
        navigateToHome,
        null,
        navigateToRecord ,
        navigateToConfig
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp)
        ) {
            if (viewModel.isLoading.value) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            } else {
                itemsIndexed(viewModel.courses.value) { index, course ->
                    CardContainer(
                        onClick = {}
                    ){
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                course.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(10.dp))
                            Image(
                                painter = painterResource(id = R.drawable.arrow_right),
                                contentDescription = "arrow",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.size(IconMedium)
                            )
                        }
                    }


                    val gradesForCurrentCourse = viewModel.grades.value[index]
                    if (gradesForCurrentCourse.isNotEmpty()) {
                        gradesForCurrentCourse.forEach { grade ->
                            GradeCardComp(
                                grade = grade,
                                onClick = {
                                    viewModel.setShowGrade(grade.id)
                                    showBottomSheet = true
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    } else {
                        Text(
                            text = "No hay calificaciones para esta asignatura.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        GradeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            showGrade = viewModel.showGrade.value,
            editOnClick = {
                navigateToEditGrade(
                    viewModel.showGrade.value.id,
                    viewModel.showGrade.value.courseId
                ); showBottomSheet = false
            },
            deleteOnClick = { showDeleteConfirmation = true; showBottomSheet = false }
        )
    }
}