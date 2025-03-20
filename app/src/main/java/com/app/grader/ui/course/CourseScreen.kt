package com.app.grader.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
    val scope = rememberCoroutineScope()
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
            Spacer(modifier = Modifier.weight(1f))
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
                    Button(onClick = { navigateToEditGrade(viewModel.showGrade.value.id, courseId)}) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCourseCard(average: Double, accumulatePoints:Double, pendingPoints: Double, description: String, uc: Int){
    Column(modifier =Modifier
        .padding(horizontal = 25.dp)
        .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
        .fillMaxWidth()

    ){
        Column (
            modifier = Modifier
                .padding(20.dp)
        ){
            Text(text = "Tu promedio", fontSize = 20.sp)
            Row( modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 10.dp)
            ) {
                CirclePie(average ,accumulatePoints, pendingPoints)
                Column(modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 0.dp)

                ) {
                    Text(text = description, style = MaterialTheme.typography.bodyLarge)
                    Text(text = "$uc UC", style = MaterialTheme.typography.bodyLarge)
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
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
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
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = Secondary600
                    )
                }
            }
        }
    }
}