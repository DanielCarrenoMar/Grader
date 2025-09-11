package com.app.grader.ui.pages.config

import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.core.appConfig.TypeTheme
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.HeaderMenu
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
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
            viewModel.updateConfiguration()
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { viewModel.deleteAll() },
            { showDeleteConfirmation = false },
            "Esta opción borrara TODOS los datos de la app.",
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
            SelectorCard(
                title = "Tipo de calificación",
                items = listOf(
                    SelectorItem("0-7", TypeGrade.NUMERIC_7_CHI.name),
                    SelectorItem("0-10 (Argentina)", TypeGrade.NUMERIC_10_ARG.name),
                    SelectorItem("0-10 (España)", TypeGrade.NUMERIC_10_ESP.name),
                    SelectorItem("0-10 (México)", TypeGrade.NUMERIC_10_MEX.name),
                    SelectorItem("0-20", TypeGrade.NUMERIC_20.name),
                    SelectorItem("0-100", TypeGrade.NUMERIC_100.name),
                ),
                current = viewModel.typeGrade.value.name,
                onSelect = { viewModel.setTypeGrade(TypeGrade.valueOf(it)) },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = R.drawable.rectangle_list_outline,
            )
            SelectorCard(
                title = "Tema",
                items = listOf(
                    SelectorItem("Usar mi tema del sistema", TypeTheme.SYSTEM_DEFAULT.name),
                    SelectorItem("Tema Claro", TypeTheme.LIGHT.name),
                    SelectorItem("Tema Oscuro", TypeTheme.DARK.name),
                ),
                current = viewModel.typeTheme.value.name,
                onSelect = {
                    viewModel.setTypeTheme(TypeTheme.valueOf(it))
                    viewModel.restartApp(context)
                },
                contentColor = MaterialTheme.colorScheme.onSurface,
                iconColor = MaterialTheme.colorScheme.primary,
                icon = when(viewModel.typeTheme.value){
                    TypeTheme.DARK -> R.drawable.moon_outline
                    TypeTheme.LIGHT -> R.drawable.sun_outline
                    TypeTheme.SYSTEM_DEFAULT -> if (isSystemInDarkTheme()) R.drawable.sun_outline else R.drawable.moon_outline
                },
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
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
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
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
