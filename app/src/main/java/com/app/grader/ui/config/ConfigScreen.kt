package com.app.grader.ui.config

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.IconCardButton
import com.app.grader.R
import androidx.core.net.toUri
import com.app.grader.ui.theme.Error500

@Composable
fun ConfigScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName


    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteAll() },
            { showDeleteConfirmation = false },
            "Esta opción borrar todos los datos de la app: asignaturas y calificaciones.",
        )
    }
    HeaderMenu(
        "Ajustes",
        { navigateToHome() },
        { navigateToAllGrades() },
        null,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(10.dp))
            IconCardButton(
                onClick = {
                    val url = "https://github.com/DanielCarrenoMar/Grader/releases"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = R.drawable.info_outline,
                text = "Buscar si hay actualización",
            )
            IconCardButton(
                onClick = {
                    val url = "https://github.com/DanielCarrenoMar/Grader/issues/new"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = R.drawable.exclamation_outline,
                text = "Reportar bug o sugerencia",
            )
            IconCardButton(
                onClick = { showDeleteConfirmation = true },
                contentColor = Error500,
                icon = R.drawable.trash_outline,
                text = "Eliminar todos los datos",
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Grader $versionName",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "Creado por @DanielCarrenoMar en colaboración con @Kobalt09, @Queik5450, @Bloodbay8 y @davijuan69",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }

}