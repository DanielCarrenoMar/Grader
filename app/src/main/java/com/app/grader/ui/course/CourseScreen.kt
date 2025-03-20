package com.app.grader.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.AddMenuComp
import com.app.grader.ui.componets.AddMenuCompItem
import com.app.grader.ui.componets.CardContainer
import com.app.grader.ui.componets.CircleGrade
import com.app.grader.ui.componets.CirclePie
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.theme.Secondary600
import com.app.grader.ui.theme.Success500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courseId: Int,
    navegateBack: () -> Unit,
    navigateToGrade: (Int) -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getGradesFromCourse(courseId)
            viewModel.getCourseFromId(courseId)
            viewModel.calPoints(courseId)
        }
    }

    HeaderBack(
        title = viewModel.course.value.title,
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoCourseCard(
                viewModel.course.value.average,
                viewModel.accumulatePoints.doubleValue,
                viewModel.pedingPoints.doubleValue,
                viewModel.course.value.description,
                viewModel.course.value.uc
            )
            Spacer(modifier = Modifier.height(25.dp))
            LazyColumn {
                items(viewModel.grades.value){ grade ->
                    GradeCardComp(grade) {
                        viewModel.setShowGrade(grade.id)
                        showBottomSheet = true
                    }
                }
            }
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    CircleGrade(viewModel.showGrade.value.grade, radius = 40.dp)
                    Text(text = viewModel.showGrade.value.title)
                    Text(text = viewModel.showGrade.value.description)
                    Text(text = "${viewModel.showGrade.value.percentage}%")
                    HorizontalDivider()
                    Button(onClick = { navigateToEditGrade(viewModel.showGrade.value.id, courseId) }) {
                        Text("Edit")
                    }
                    HorizontalDivider()
                    Button(onClick = { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) }) {
                        Text("Eliminar")
                    }
                }
            }
        }
        AddMenuComp(listOf(
            AddMenuCompItem("Calificación", R.drawable.star_outline){ navigateToEditGrade(-1, courseId) },
        ))
    }
}

@Composable
fun InfoCourseCard(average: Double, accumulatePoints:Double, pendingPoints: Double, description: String, uc: Int){
    CardContainer{ innerPading ->
        Column (
            modifier = Modifier.padding(innerPading)
        ){
            Text(text = "Tu promedio", fontSize = 20.sp)
            Row( modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 10.dp)
            ) {
                CirclePie(average ,accumulatePoints, pendingPoints)
                Column(modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 0.dp)

                ) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.height(60.dp)
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            text = "$uc",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UC",
                            modifier = Modifier
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Puntos acumulados",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Success500
                    )
                    Text(
                        text = "${Math.round(accumulatePoints * 10) / 10.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Success500
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Puntos por evaluar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Secondary600
                    )
                    Text(
                        text = "${Math.round(pendingPoints * 10) / 10.0}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Secondary600
                    )
                }
            }
        }
    }
}