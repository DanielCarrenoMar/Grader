package com.app.grader.ui.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
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
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.theme.IconMedium
import com.app.grader.ui.theme.IconSmall
import kotlinx.coroutines.delay
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
        viewModel.getAllCoursesAndCalTotalAverage()
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllGrades()
        }
    }

    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteSelectedCourse() },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu(
        "Asignaturas",
        null,
        { navigateToAllGrades("a") },
        { navigateToConfig() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
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
                    { navigateToEditCourse(course.id) },
                )
                Spacer(Modifier.height(10.dp))
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
        AddMenuComp(
            listOf(
                AddMenuCompItem(
                    "Asignatura",
                    R.drawable.education_cap_outline
                ) { navigateToEditCourse(-1) },
                AddMenuCompItem(
                    "Calificación",
                    R.drawable.star_outline
                ) { navigateToEditGrade(-1, -1) },
            )
        )
    }
}

@Composable
fun InfoHomeCard(average: Double, grades: List<GradeModel>){
    CardContainer{ innerPading ->
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(150.dp)
        ){
            Column (
                modifier = Modifier.weight(1f)
            ){
                TitleIcon(
                    iconName =  "chart mixed",
                    iconId =  R.drawable.chart_mixed
                ){
                    Text(text = "Progresión", style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier.fillMaxSize().padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ){
                    if (grades.isNotEmpty()) LineChartAverage(grades.map{it.grade}, Modifier.fillMaxSize())
                    else Text(
                            text = "No hay calificaciones",
                            style = MaterialTheme.typography.titleSmall
                        )
                }
            }
            Box (
                modifier = Modifier.weight(0.9f),
            ){
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = if (average != 0.0) "${(average * 100).roundToInt() / 100.0}" else "--",
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
}