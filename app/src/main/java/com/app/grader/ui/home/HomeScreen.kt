package com.app.grader.ui.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.ui.componets.CourseCardComp
import com.app.grader.ui.componets.HeaderMenu
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.domain.model.CourseModel
import com.app.grader.ui.componets.AddComp
import com.app.grader.ui.componets.AddCompItem
import com.app.grader.ui.componets.DeleteConfirmationComp

@Composable
fun HomeScreen(
    navigateToAllGrades: (String) -> Unit,
    navigateToConfig: () -> Unit,
    navigateToCourse: (Int) -> Unit,
    navigateToEditCourse: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val courses by remember { mutableStateOf(viewModel.courses) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllCourses()
        }
    }

    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteSelectedCourse() },
            { showDeleteConfirmation = false }
        )
    }

    HeaderMenu ("Asignaturas",
        null,
        {navigateToAllGrades("a")},
        {navigateToConfig()}
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "HOME SCREEN", fontSize = 25.sp)
                //Text(text = "Texto Transmitido a AllGrades: $text", fontSize = 14.sp)
                //TextField(value = text, onValueChange = {viewModel.onTextChanged(it)})
                Button(onClick = { viewModel.deleteAllCourses() }) {
                    Text(text = "Borrar todas las Materias")
                }
            }
            items(courses.value) { course ->
                CourseCardComp(
                    course,
                    { navigateToCourse(course.id) },
                    {
                        viewModel.selectDeleteCourse(course.id)
                        showDeleteConfirmation = true
                    },
                    {navigateToEditCourse(course.id)},
                )
                Spacer(Modifier.height(10.dp))
            }
        }
        AddComp(listOf(
            AddCompItem("Asignatura", R.drawable.education_cap_outline){ navigateToEditCourse(-1) },
            AddCompItem("Calificación", R.drawable.star_outline){ navigateToEditCourse(-1) },
        ))
    }

}