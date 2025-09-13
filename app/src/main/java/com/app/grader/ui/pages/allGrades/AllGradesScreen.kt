package com.app.grader.ui.pages.allGrades

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.GradeBottomSheet
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.card.CardContainer
import com.app.grader.ui.componets.card.GradeCardComp
import com.app.grader.ui.theme.IconMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllGradesScreen(
    navigateToHome: () -> Unit,
    navigateToConfig: () -> Unit,
    navigateToRecord: () -> Unit,
    navigateToEditGrade: (Int, Int, Int) -> Unit,
    navigateToCourse: (Int) -> Unit,
    viewModel: AllGradesViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isGradesEmpty  by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.getAllGradesWithCourses()
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) },
            { showDeleteConfirmation = false },
            "¿Realmente desea eliminar ${viewModel.showGrade.value.title}?",
        )
    }
    HeaderMenu(
        "Calificaciones",
        navigateToHome,
        null,
        navigateToRecord ,
        navigateToConfig
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp)
        ) {
            if (viewModel.isLoading.value) {
                item {
                    Spacer(Modifier.height(10.dp))
                    Row (
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            } else {

                itemsIndexed(viewModel.courses.value) { index, course ->
                    if (viewModel.grades.value[index].isEmpty()) return@itemsIndexed
                    isGradesEmpty = false
                    CardContainer(
                        onClick = {navigateToCourse(course.id)},
                    ){
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.alpha(0.7f).padding(10.dp, 10.dp, 0.dp, 10.dp)
                        ) {
                            Text(
                                text = course.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(9f)
                            )
                            Spacer(Modifier.weight(1f))
                            Image(
                                painter = painterResource(id = R.drawable.arrow_right),
                                contentDescription = "arrow",
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                                modifier = Modifier.size(IconMedium)
                            )
                        }
                    }

                    val gradesForCurrentCourse = viewModel.grades.value[index]
                    gradesForCurrentCourse.forEach { grade ->
                        GradeCardComp(
                            grade = grade,
                            onClick = {
                                viewModel.setShowGrade(grade.id)
                                showBottomSheet = true
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (isGradesEmpty) {
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
                                contentDescription = "Empty Grades",
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "Aún no hay califaciones",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
    if (showBottomSheet) {
        GradeBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState,
            showGrade = viewModel.showGrade.value,
            editOnClick = {
                navigateToEditGrade(
                    -1,
                    viewModel.showGrade.value.courseId,
                    viewModel.showGrade.value.id
                ); showBottomSheet = false
            },
            deleteOnClick = { showDeleteConfirmation = true; showBottomSheet = false }
        )
    }
}