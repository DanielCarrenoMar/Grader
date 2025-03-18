package com.app.grader.ui.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.ui.componets.AddComp
import com.app.grader.ui.componets.AddCompItem
import com.app.grader.ui.componets.CirclePie
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.theme.Secondary600
import com.app.grader.ui.theme.Success500
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun CourseScreen(
    courseId: Int,
    navegateBack: () -> Unit,
    navigateToGrade: (Int) -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val grades by remember { mutableStateOf(viewModel.grades) }
    val course by remember { mutableStateOf(viewModel.course) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getGradesFromCourse(courseId)
            viewModel.getCourseFromId(courseId)
        }
    }

    HeaderBack(
        title = course.value.title,
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally) {
            //Spacer(modifier = Modifier.weight(1f))
            //Text(text = "Course SCREEN", fontSize = 25.sp)
            Column(modifier =Modifier
                .padding(horizontal = 25.dp)
                .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.medium)
                .fillMaxWidth()

            ){
                Column (
                    modifier = Modifier
                        .padding(20.dp)
                ){
                    //course.value.description
                    Text(text = "Tu promedio", fontSize = 20.sp)
                    Row( modifier = Modifier
                        .padding(horizontal = 0.dp, vertical = 10.dp)

                    ) {
                        CirclePie()
                        Column(modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 0.dp)

                        ) {
                            Text(text = course.value.description, fontSize = 15.sp)
                            Text(text = "${course.value.uc} UC", fontSize = 15.sp)
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
                            Text(text = "Puntos acumulados", fontSize = 13.sp, color = Success500)
                            Text(text = "6", fontSize = 15.sp, color = Success500)
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Puntos por evaluar", fontSize = 13.sp, color = Secondary600)
                            Text(text = "8", fontSize = 15.sp, color = Secondary600)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            LazyColumn {
                items(grades.value) { grade ->
                    GradeCardComp(grade, {navigateToGrade(grade.id)})
                }
            }
        }
    }
    AddComp(listOf(
        AddCompItem("Calificación", R.drawable.star_outline){ navigateToEditGrade(-1, courseId) },
    ))
}