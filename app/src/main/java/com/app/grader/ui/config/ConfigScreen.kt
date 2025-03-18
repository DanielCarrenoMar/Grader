package com.app.grader.ui.config

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.grader.ui.componets.HeaderMenu

@Composable
fun ConfigScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: (String) -> Unit,
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    HeaderMenu ("Ajustes",
        {navigateToHome()},
        {navigateToAllGrades("a")},
        null,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Config SCREEN", fontSize = 25.sp)
            Button(
                onClick = { viewModel.deleteAll() },
            ) {
                Text("Borrar TODO")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}