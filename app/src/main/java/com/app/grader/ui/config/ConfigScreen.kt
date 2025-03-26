package com.app.grader.ui.config

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu

@Composable
fun ConfigScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteAll() },
            { showDeleteConfirmation = false }
        )
    }
    HeaderMenu ("Ajustes",
        {navigateToHome()},
        {navigateToAllGrades()},
        null,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { showDeleteConfirmation = true },
            ) {
                Text("Borrar TODO")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}