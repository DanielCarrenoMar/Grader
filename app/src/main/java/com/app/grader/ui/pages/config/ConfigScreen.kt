package com.app.grader.ui.pages.config

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.card.IconCardButton
import com.app.grader.R
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.ui.componets.card.SwitchCardComp
import com.app.grader.ui.theme.Error500
import com.app.grader.domain.types.TypeGrade

@Composable
fun ConfigScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    navigateToRecord: () -> Unit,
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.updateConfiguration()
        }
    }

    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteAll() },
            { showDeleteConfirmation = false },
            "Esta opción borrar todos los datos de la app: asignaturas y calificaciones.",
        )
    }
    HeaderMenu(
        "Ajustes",
        navigateToHome,
        navigateToAllGrades,
        navigateToRecord,
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
            SwitchCardComp(
                checked = viewModel.isRoundFinalCourseAverage.value,
                onCheckedChange = {
                    viewModel.setRoundFinalCourseAverage(it)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = R.drawable.round,
                text = "Redondear promedio para asignaturas finalizadas",
            )
            SwitchCardComp(
                checked = viewModel.isDarkMode.value,
                onCheckedChange = {
                    viewModel.setDarkMode(it)
                    viewModel.restartApp(context)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = if (viewModel.isDarkMode.value) R.drawable.moon_outline else R.drawable.sun_outline,
                text = "Modo Oscuro",
            )

            // Selector de tipo de nota
            GradeTypeSelector(
                current = viewModel.typeGrade.value,
                onSelect = { viewModel.setTypeGrade(it) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
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
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GradeTypeSelector(current: TypeGrade, onSelect: (TypeGrade) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(TypeGrade.NUMERIC_20, TypeGrade.NUMERIC_10, TypeGrade.NUMERIC_100)
    val label = when(current){
        TypeGrade.NUMERIC_20 -> "Notas base 20"
        TypeGrade.NUMERIC_10 -> "Notas base 10"
        TypeGrade.NUMERIC_100 -> "Notas base 100"
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(top = 12.dp)
    ) {
        TextField(
            readOnly = true,
            value = label,
            onValueChange = {},
            label = { Text("Tipo de nota") },
            modifier = Modifier.menuAnchor(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val optionLabel = when(option){
                    TypeGrade.NUMERIC_20 -> "Notas base 20"
                    TypeGrade.NUMERIC_10 -> "Notas base 10"
                    TypeGrade.NUMERIC_100 -> "Notas base 100"
                }
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
