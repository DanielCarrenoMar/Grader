package com.app.debugGrader.ui.pages.config

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.grader.R
import com.app.grader.ui.componets.card.IconCardButton

@Composable
fun DebugOptionsComp(
    viewModel: DebugConfigViewModel = hiltViewModel(),
) {
    IconCardButton(
        onClick = { viewModel.loadDebugData() },
        contentColor = MaterialTheme.colorScheme.onSurface,
        iconColor = MaterialTheme.colorScheme.primary,
        icon = R.drawable.cog_outline,
        text = "Cargar datos de prueba",
    )
}

