package com.app.grader.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.ui.componets.AddMenuComp
import com.app.grader.ui.componets.AddMenuCompItem
import com.app.grader.ui.componets.CardContainer
import com.app.grader.ui.componets.CourseCardComp
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.LineChartAverage
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    navigateToAllGrades: (String) -> Unit,
    navigateToConfig: () -> Unit,
    navigateToCourse: (Int) -> Unit,
    navigateToEditCourse: (Int) -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val courses by remember { mutableStateOf(viewModel.courses) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllGrades()
            viewModel.getAllCoursesAndCalTotalAverage()
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
                InfoHomeCard(viewModel.totalAverage.doubleValue, viewModel.grades.value)
                Spacer(modifier = Modifier.height(25.dp))
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
            item{
                Spacer(Modifier.height(80.dp))
            }
        }
        AddMenuComp(listOf(
            AddMenuCompItem("Asignatura", R.drawable.education_cap_outline){ navigateToEditCourse(-1) },
            AddMenuCompItem("Calificación", R.drawable.star_outline){ navigateToEditGrade(-1, -1) },
        ))
    }
}

@Composable
fun InfoHomeCard(average: Double, grades: List<GradeModel>){
    val gradeValues = if (grades.isNotEmpty()) grades.map { it.grade } else listOf(1.0,4.0)

    CardContainer{ innerPading ->
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPading)
                .fillMaxSize()
        ){
            Column{
                Text(text = "Grafico")
                LineChartAverage(gradeValues)
            }
            Column (
                modifier = Modifier.padding(end = 40.dp)
            ){
                /*Row {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(28.dp)
                            .background(color = MaterialTheme.colorScheme.secondary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cog_outline),
                            contentDescription = "educationCap",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(text = "titulo")
                }*/

                Text(
                    text = "${(average * 100).roundToInt() / 100.0 }",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 32.sp,
                )
                Text(
                    text = "Tu Promedio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}