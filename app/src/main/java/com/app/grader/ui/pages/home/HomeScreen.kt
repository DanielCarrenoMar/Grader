package com.app.grader.ui.pages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.ui.componets.FloatingMenuComp
import com.app.grader.ui.componets.FloatingMenuCompItem
import com.app.grader.ui.componets.card.CardContainer
import com.app.grader.ui.componets.card.CourseCardComp
import com.app.grader.ui.componets.card.CourseCardType
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.chart.LineChartAverage
import com.app.grader.ui.componets.TitleIcon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAllGrades: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToRecord: () -> Unit,
    navigateToCourse: (Int) -> Unit,
    navigateToEditCourse: (Int) -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val courses by viewModel.courses
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllCoursesAndCalTotalAverage(null)
            viewModel.getGradeFromSemester(null)
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(courses) {
        if (courses.isNotEmpty()) {
            listState.animateScrollToItem(index = 0)
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteSelectedCourse({viewModel.getAllCoursesAndCalTotalAverage(null)}) },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu(
        "Asignaturas",
        null,
        navigateToAllGrades,
        navigateToRecord,
        navigateToConfig
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState,
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                InfoHomeCard(viewModel.totalAverage.doubleValue, viewModel.grades.value)
                Spacer(modifier = Modifier.height(25.dp))
            }
            if (viewModel.isLoading.value) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            } else {
                val sortedCourses = courses.sortedWith(compareBy { course ->

                    val typeForSort = viewModel.cardTypeFromCourse(course)

                    when (typeForSort) {
                        CourseCardType.Normal -> 1
                        CourseCardType.Pass -> 1
                        CourseCardType.Finish -> 2
                        CourseCardType.Fail -> 3
                    }
                })

                if (sortedCourses.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            EmptyCoursesImg()
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "No hay asignaturas",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    items(sortedCourses) { course ->
                        val courseCardType = viewModel.cardTypeFromCourse(course)
                        CourseCardComp(
                            course,
                            { navigateToCourse(course.id) },
                            {
                                viewModel.selectDeleteCourse(course.id)
                                showDeleteConfirmation = true
                            },
                            { navigateToEditCourse(course.id) },
                            type = courseCardType
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
        FloatingMenuComp(
            listOf(
                FloatingMenuCompItem(
                    "Asignatura",
                    R.drawable.education_cap_outline
                ) { navigateToEditCourse(-1) },
                FloatingMenuCompItem(
                    "Calificación",
                    R.drawable.star_outline
                ) { navigateToEditGrade(-1, -1) },
            )
        )
    }
}

@Composable
fun InfoHomeCard(average: Double, grades: List<GradeModel>) {
    CardContainer { innerPading ->
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TitleIcon(
                    iconName = "chart mixed",
                    iconId = R.drawable.chart_mixed
                ) {
                    Text(text = "Progresión", style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (grades.isNotEmpty()) LineChartAverage(
                        grades.map { it.grade },
                        Modifier.fillMaxSize()
                    )
                    else Text(
                        text = "No hay calificaciones",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Box(
                modifier = Modifier.weight(0.9f),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

@Composable
fun EmptyCoursesImg() {
    val image = painterResource(id = R.drawable.chick)
    Image(
        painter = image,
        contentDescription = "Empty Courses",
    )
}
