package com.app.grader.ui.allGrades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.home.HomeViewModel

@Composable
fun AllGradesScreen(
    text:String,navegateToHome: () -> Unit,
    navigateToConfig: () -> Unit,
    viewModel: AllGradesViewModel = hiltViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllGrades()
        }
    }

    HeaderMenu ("Todas las notas",
        {navegateToHome()},
        null,
        {navigateToConfig()}
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ){
            items(viewModel.grades.value){ grade ->
                GradeCardComp(grade) { } // TODO("Llevar a editar nota")
            }
        }
    }
}