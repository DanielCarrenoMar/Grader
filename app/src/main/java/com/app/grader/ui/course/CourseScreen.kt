package com.app.grader.ui.course

import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.unit.dp
import com.app.grader.domain.model.CourseModel
import com.app.grader.ui.componets.HeaderBack

@Composable
fun CourseScreen(navegateBack: () -> Unit, navigateToGrade: () -> Unit, viewModel: CourseViewModel = hiltViewModel()) {
    val showCourses by remember { mutableStateOf(viewModel.showCourses) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllCourses()
        }
    }

    HeaderBack(
        title = "Materia",
        navigateBack = navegateBack
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Course SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { navigateToGrade() }) {
                Text(text = "Navegar a Nota (Grader)")
            }
            Button(onClick = { viewModel.saveCourse() }) {
                Text(text = "Crear Materia")
            }
            Button(onClick = { viewModel.deleteAllCourses() }) {
                Text(text = "Borrar todas las Materias")
            }
            Spacer(modifier = Modifier.weight(1f))
            CourseList(courses = showCourses.value, navigateToGrade = navigateToGrade)
        }
    }
}

@Composable
fun CourseList(courses: List<CourseModel>, navigateToGrade: () -> Unit) {
    LazyColumn {
        items(courses) { course ->
            CourseItem(course = course, navigateToGrade = navigateToGrade)
        }
    }
}

@Composable
fun CourseItem(course: CourseModel, navigateToGrade: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = course.title, fontSize = 20.sp)
            Text(text = course.description, fontSize = 14.sp)
            Text(text = "UC: ${course.uc}", fontSize = 14.sp)
            Button(onClick = { navigateToGrade() }) {
                Text(text = "Navegar a Nota (Grader)")
            }
        }
    }
}