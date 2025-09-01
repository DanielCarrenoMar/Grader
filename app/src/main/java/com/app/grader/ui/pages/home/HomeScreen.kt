package com.app.grader.ui.pages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.FloatingMenuComp
import com.app.grader.ui.componets.FloatingMenuCompItem
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.card.CourseCardComp
import com.app.grader.ui.componets.card.CourseCardType
import com.app.grader.ui.componets.card.InfoSemesterCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToAllGrades: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToRecord: () -> Unit,
    navigateToCourse: (Int) -> Unit,
    navigateToEditCourse: (Int, Int) -> Unit,
    navigateToEditGrade: (Int, Int, Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val courses by viewModel.courses
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
            { viewModel.deleteSelectedCourse({ viewModel.getAllCoursesAndCalTotalAverage(null) }) },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu(
        "Asignaturas",
        null,
        navigateToAllGrades,
        navigateToRecord,
        navigateToConfig,
        snackbarHostState = snackbarHostState
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
                InfoSemesterCard(viewModel.totalAverage.value, viewModel.grades.value)
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
                            { navigateToEditCourse(-1, course.id) },
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
                ) { navigateToEditCourse(-1, -1) },
                FloatingMenuCompItem(
                    "Calificación",
                    R.drawable.star_outline
                ) {
                    if (courses.isEmpty()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("No se encontro ninguna asignatura, por favor crea una antes 2")
                        }
                    } else {
                        navigateToEditGrade(-1, -1, -1)
                    }
                },
            )
        )
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
