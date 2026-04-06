package com.app.debugGrader.ui.pages.config

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.grader.R
import com.app.grader.ui.componets.card.IconCardButton

@Composable
fun DebugOptionsComp(
    viewModel: DebugConfigViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    Column {
        IconCardButton(
            onClick = { viewModel.loadDebugData() },
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.primary,
            icon = R.drawable.cog_outline,
            text = "Cargar datos de prueba",
        )

        IconCardButton(
            onClick = {
                (context as? Activity)?.let { activity ->
                    viewModel.launchReviewFlow(
                        activity = activity,
                        onComplete = {
                            Toast.makeText(context, "Review Flow completado", Toast.LENGTH_SHORT).show()
                        },
                        onError = {
                            Toast.makeText(context, "Review Flow error", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.primary,
            icon = R.drawable.cog_outline,
            text = "Probar In-App Review",
        )
    }
}
