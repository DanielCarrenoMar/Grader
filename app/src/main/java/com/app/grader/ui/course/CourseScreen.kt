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
import com.app.grader.ui.componets.CourseCardComp
import com.app.grader.ui.componets.HeaderBack

@Composable
fun CourseScreen(navegateBack: () -> Unit, navigateToGrade: () -> Unit, viewModel: CourseViewModel = hiltViewModel()) {
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
        }
    }
}

@Composable
fun CourseList(courses: List<CourseModel>, navigateToGrade: () -> Unit) {
    LazyColumn {
        items(courses) { course ->
            CourseCardComp(course, navigateToGrade)
        }
    }
}