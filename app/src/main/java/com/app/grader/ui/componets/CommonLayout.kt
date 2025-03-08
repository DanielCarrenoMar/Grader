package com.app.grader.ui.componets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonLayout(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(start = 16.dp,end = 16.dp)
    ) {
        content()
    }
}