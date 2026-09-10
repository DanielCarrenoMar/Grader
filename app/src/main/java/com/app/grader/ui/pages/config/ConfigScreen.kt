package com.app.grader.ui.pages.config

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.types.ThemeType
import com.app.grader.debug.DebugHelper
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.InfoAlertDialogComp
import com.app.grader.ui.componets.card.IconCardButton
import com.app.grader.ui.componets.card.SwitchCardComp
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    navigateToRecord: () -> Unit,
    viewModel: ConfigViewModel = hiltViewModel(),
) {
    val showDeleteConfirmation = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val isDebugBuild = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    var showMinToPassInfoDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.updateConfiguration()
        }
    }

    if (showDeleteConfirmation.value) {
        DeleteConfirmationComp(
            {
                viewModel.deleteAll(
                    onComplete = { showDeleteConfirmation.value = false }
                )
            },
            { showDeleteConfirmation.value = false },
            "Esta opción borrara TODOS los datos de la app.",
            enabled = !viewModel.isDeletingAll.value,
        )
    }

    if (showMinToPassInfoDialog) {
        InfoAlertDialogComp(
            title = "Mínimo para aprobar",
            message = "Representa la nota mínima que se necesita acumulada para aprobar la asignatura.",
            onDismiss = { showMinToPassInfoDialog = false }
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(10.dp))
            SelectorCard(
                title = "Tema",
                items = listOf(
                    SelectorItem("Usar mi tema del sistema", ThemeType.SYSTEM_DEFAULT.name),
                    SelectorItem("Tema Claro", ThemeType.LIGHT.name),
                    SelectorItem("Tema Oscuro", ThemeType.DARK.name),
                ),
                current = viewModel.typeTheme.value.name,
                onSelect = {
                    viewModel.setTypeTheme(ThemeType.valueOf(it))
                    viewModel.restartApp(context)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.onSurface,
                icon = when(viewModel.typeTheme.value){
                    ThemeType.DARK -> R.drawable.moon_outline
                    ThemeType.LIGHT -> R.drawable.sun_outline
                    ThemeType.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) R.drawable.moon_outline else R.drawable.sun_outline
                },
            )
            SelectorCard(
                title = "Tipo de calificación",
                items = viewModel.typeGradeList.value.map { SelectorItem(it.title, it.id.toString()) },
                current = viewModel.selectedTypeGradeId.intValue.toString(),
                onSelect = { viewModel.setSelectedTypeGradeId(it.toInt()) },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.onSurface,
                icon = R.drawable.rectangle_list_outline,
            )
            EditScreenInputComp(
                placeHolderText = "Mínimo para aprobar (opcional)",
                value = viewModel.minToPassInput.value,
                onValueChange = viewModel::setMinToPass,
                leadingIconId = R.drawable.check_outline,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                maxLength = 6,
                maxLines = 1,
                isError = viewModel.minToPassError.value,
                suffix = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showMinToPassInfoDialog = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.info_outline),
                                contentDescription = "Información sobre Mínimo para aprobar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            SwitchCardComp(
                checked = viewModel.isRoundFinalCourseAverage.value,
                onCheckedChange = {
                    viewModel.setRoundFinalCourseAverage(it)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.onSurface,
                icon = R.drawable.round,
                text = "Redondear promedio para asignaturas finalizadas",
            )
            SwitchCardComp(
                checked = viewModel.isDirectPercentage.value,
                onCheckedChange = {
                    viewModel.setDirectPercentage(it)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.onSurface,
                icon = R.drawable.layers_outline,
                text = "Usar porcentaje acumulativo como calificacion",
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            IconCardButton(
                onClick = {
                    val url = "https://play.google.com/store/apps/details?id=com.app.grader&pcampaignid=web_share"
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = R.drawable.heart_outline,
                text = "¿Te gusto la app? ¡Calificanos!",
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
                onClick = { showDeleteConfirmation.value = true },
                contentColor = Error500,
                icon = R.drawable.trash_outline,
                text = "Eliminar todos los datos de la app",
            )
            if (isDebugBuild) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                DebugHelper.DebugOptionsComp()
                IconCardButton(
                    onClick = {
                        viewModel.resetLaunchCount()
                        Toast.makeText(context, "Launch count reiniciado a 0", Toast.LENGTH_SHORT).show()
                    },
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    iconColor = MaterialTheme.colorScheme.primary,
                    icon = R.drawable.cog_outline,
                    text = "Reiniciar launch count (dev)",
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
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

data class SelectorItem(val title: String, val value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorCard(
    title: String,
    items: List<SelectorItem>,
    current: String,
    onSelect: (String) -> Unit,
    contentColor: Color,
    iconColor: Color = contentColor,
    icon: Int,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        colors = CardColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Error500,
            disabledContentColor = Error500
        )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 15.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Tipo de nota",
                colorFilter = ColorFilter.tint(iconColor),
                modifier = Modifier.size(IconLarge)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                TextField(
                    readOnly = true,
                    value = items.find { it.value == current }?.title ?: "",
                    onValueChange = {},
                    label = { Text(title) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.title) },
                            onClick = {
                                onSelect(item.value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
