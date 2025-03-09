package com.app.grader.ui.grade

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.home.HomeViewModel

@Composable
fun GradeScreen(navegateBack: () -> Unit) {

    HeaderBack(
        title = "Nota",
        navigateBack = navegateBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Grade SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}