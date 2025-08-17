package com.app.grader.ui.pages.record

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecordScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    navigateToConfig: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
){

}