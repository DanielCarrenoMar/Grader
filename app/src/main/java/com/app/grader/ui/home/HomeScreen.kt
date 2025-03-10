package com.app.grader.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import com.app.grader.ui.componets.HeaderMenu
import androidx.core.view.WindowInsetsCompat.Type
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.domain.model.CourseModel
import com.app.grader.ui.componets.CircleGrade
import com.app.grader.ui.componets.CourseCardComp

@Composable
fun HomeScreen(navigateToAllGrades: (String) -> Unit, navigateToConfig: () -> Unit, navigateToCourse: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val insets = WindowInsetsCompat.toWindowInsetsCompat(LocalView.current.rootWindowInsets)
    val statusBarHeight = with(LocalDensity.current) { insets.getInsets(Type.statusBars()).top.toDp() }
    val courses by remember { mutableStateOf(viewModel.courses) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllCourses()
        }
    }

    HeaderMenu ("Asignaturas",
        null,
        {navigateToAllGrades("a")},
        {navigateToConfig()}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "HOME SCREEN", fontSize = 25.sp)
            //Spacer(modifier = Modifier.weight(1f))
            //Text(text = "Texto Transmitido a AllGrades: $text", fontSize = 14.sp)
            //TextField(value = text, onValueChange = {viewModel.onTextChanged(it)})
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.saveCourse() }) {
                Text(text = "Crear Materia")
            }
            Button(onClick = { viewModel.deleteAllCourses() }) {
                Text(text = "Borrar todas las Materias")
            }
            Spacer(modifier = Modifier.weight(1f))
            CourseList(
                courses.value,
                navigateToCourse
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}

@Composable
fun CourseList(courses: List<CourseModel>, navigateToCourse: () -> Unit) {
    LazyColumn {
        items(courses) { course ->
            CourseCardComp(course, navigateToCourse)
        }
    }
}