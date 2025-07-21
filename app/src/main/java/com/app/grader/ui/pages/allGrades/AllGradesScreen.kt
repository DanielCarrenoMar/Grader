package com.app.grader.ui.pages.allGrades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.pages.course.InfoGradeBottonCar
import com.app.grader.ui.pages.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGradesScreen(
    navegateToHome: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: AllGradesViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllGrades()
        }
    }

    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu ("Todas las Calificaciones",
        {navegateToHome()},
        null,
        {navigateToConfig()}
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp)
        ){
            items(viewModel.grades.value){ grade ->
                GradeCardComp(
                    grade = grade,
                    onClick = {
                    viewModel.setShowGrade(grade.id)
                    showBottomSheet = true
                }
                )
            }
        }
        if (showBottomSheet) {
            InfoGradeBottonCar(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                showGrade = viewModel.showGrade.value,
                editOnClick = { navigateToEditGrade(viewModel.showGrade.value.id, viewModel.showGrade.value.courseId); showBottomSheet = false },
                deleteOnClick = { showDeleteConfirmation = true; showBottomSheet = false }
            )
        }
    }
}