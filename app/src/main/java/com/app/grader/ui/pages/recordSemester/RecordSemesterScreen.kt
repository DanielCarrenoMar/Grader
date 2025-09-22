package com.app.grader.ui.pages.recordSemester

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.types.CourseCardType
import com.app.grader.domain.types.cardTypeFromCourse
import com.app.grader.ui.componets.FloatingMenuComp
import com.app.grader.ui.componets.FloatingMenuCompItem
import com.app.grader.ui.componets.card.CourseCard
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.componets.MenuAction
import com.app.grader.ui.componets.card.InfoSemesterCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSemesterScreen(
    semesterId: Int,
    navigateBack: () -> Unit,
    navigateToEditSemester: (Int) -> Unit,
    navigateToCourse: (Int) -> Unit,
    navigateToEditCourse: (Int, Int) -> Unit,
    navigateToEditGrade: (Int, Int, Int) -> Unit,
    viewModel: RecordSemesterViewModel = hiltViewModel()
) {
    val courses by viewModel.courses
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showDeleteSelfConfirmation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.getCoursesAndCalTotalAverageFromSemester(semesterId)
            viewModel.getGradeFromSemester(semesterId)
            viewModel.getSemester(semesterId)
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
            { viewModel.deleteSelectedCourse({ viewModel.getCoursesAndCalTotalAverageFromSemester(semesterId) }) },
            { showDeleteConfirmation = false },
            "¿Realmente desea eliminar ${viewModel.deleteCourse.value.title}?",
        )
    }
    if (showDeleteSelfConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteSelf(navigateBack) },
            { showDeleteSelfConfirmation = false },
            "¿Realmente desea eliminar ${viewModel.semester.value.title}?",
        )
    }
    HeaderBack(
        title = {
            Text(
                text = viewModel.semester.value.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                overflow = TextOverflow.Ellipsis,
            )
        },
        snackbarHostState = snackbarHostState,
        navigateBack = navigateBack,
        actions = listOf(
            MenuAction("Transferir al registro actual") {
                if (viewModel.semester.value.size <= 0) coroutineScope.launch {
                    snackbarHostState.showSnackbar("No puedes transferir un registro vacío")
                    return@launch
                }
                viewModel.onGetSizeOfActualSemester{ size ->
                    if (size > 0) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Debes vaciar el registro actual primero")
                        }
                        return@onGetSizeOfActualSemester
                    }
                    viewModel.transferSelfToActualSemester(navigateBack)
                }
            },
            MenuAction("Editar") { navigateToEditSemester(semesterId) },
            MenuAction("Eliminar") { showDeleteSelfConfirmation = true },
        ),
        topAppBarColors = TopAppBarColors(
            MaterialTheme.colorScheme.inverseSurface,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.primary,
        )
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseSurface)
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

                    val typeForSort = cardTypeFromCourse(course)

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
                            Image(
                                painter = painterResource(id = R.drawable.mountain_bg),
                                modifier = Modifier.clip(MaterialTheme.shapes.large),
                                contentDescription = "Empty Courses",
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Aún no hay asignaturas",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                } else {
                    items(sortedCourses) { course ->
                        val courseCardType = cardTypeFromCourse(course)
                        CourseCard(
                            course,
                            onClick =  { navigateToCourse(course.id) },
                            onEdit =  { navigateToEditCourse(-1, course.id) },
                            onDelete = {
                                viewModel.selectDeleteCourse(course)
                                showDeleteConfirmation = true
                            },
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
                ) { navigateToEditCourse(semesterId, -1) },
                FloatingMenuCompItem(
                    "Calificación",
                    R.drawable.star_outline
                ) {
                    if (courses.isEmpty()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Por favor agrega una asignatura primero")
                        }
                    } else {
                        navigateToEditGrade(semesterId, -1, -1)
                    }
                },
            )
        )
    }
}