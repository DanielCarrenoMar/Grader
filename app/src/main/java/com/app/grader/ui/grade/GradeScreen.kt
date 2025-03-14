package com.app.grader.ui.grade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.app.grader.domain.model.GradeModel
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderBack

@Composable
fun GradeScreen(navegateBack: () -> Unit) {

    HeaderBack(
        title = "Nota",
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Grade SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun gradeList(grades: List<GradeModel>, navigateToGrade: () -> Unit) {

    LazyColumn {
        items(grades) { grade ->
            GradeCardComp(grade, navigateToGrade)
        }
    }
}
