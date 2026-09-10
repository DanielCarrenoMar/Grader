package com.app.grader.ui.pages.initialConfig

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.grader.R
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.card.SwitchCardComp
import com.app.grader.ui.pages.config.SelectorCard
import com.app.grader.ui.pages.config.SelectorItem
import kotlinx.coroutines.launch

private const val TOTAL_PAGES = 5

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitialConfigScreen(
    onComplete: () -> Unit,
    viewModel: InitialConfigViewModel = hiltViewModel(),
) {
    val pagerState = rememberPagerState(pageCount = { TOTAL_PAGES })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding()
    ) {
        LinearProgressIndicator(
            progress = { (pagerState.currentPage + 1).toFloat() / TOTAL_PAGES },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> TypeGradePage(viewModel)
                2 -> MinToPassPage(viewModel)
                3 -> DirectPercentagePage(viewModel)
                4 -> RoundingPage(viewModel)
            }
        }

        NavigationButtons(
            currentPage = pagerState.currentPage,
            onNext = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            },
            onPrevious = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onSkip = onComplete,
            onFinish = onComplete,
        )
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.cog_outline),
            contentDescription = "Configuración",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "¡Configuremos tu experiencia!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Todas estas configuraciones se pueden modificar en cualquier momento desde la página de Ajustes.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TypeGradePage(viewModel: InitialConfigViewModel) {
    StepPageLayout(
        icon = R.drawable.rectangle_list_outline,
        title = "Tipo de calificación",
        description = "Selecciona en base a qué número se entrega la nota de las asignaturas en tu institución.",
    ) {
        SelectorCard(
            title = "Tipo de calificación",
            items = viewModel.typeGradeList.value.map { SelectorItem(it.title, it.id.toString()) },
            current = viewModel.selectedTypeGradeId.intValue.toString(),
            onSelect = { viewModel.setSelectedTypeGradeId(it.toInt()) },
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onSurface,
            icon = R.drawable.rectangle_list_outline,
        )
    }
}

@Composable
private fun MinToPassPage(viewModel: InitialConfigViewModel) {

    StepPageLayout(
        icon = R.drawable.check_outline,
        title = "¿Hay un mínimo para aprobar?",
        description = "Representa la nota mínima que se necesita acumulada para aprobar la asignatura.",
    ) {
        EditScreenInputComp(
            placeHolderText = "Mínimo para aprobar (opcional)",
            value = viewModel.minToPassInput.value,
            onValueChange = viewModel::setMinToPass,
            leadingIconId = R.drawable.check_outline,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            maxLength = 6,
            maxLines = 1,
            isError = viewModel.minToPassError.value,
        )
    }
}

@Composable
private fun DirectPercentagePage(viewModel: InitialConfigViewModel) {
    StepPageLayout(
        icon = R.drawable.layers_outline,
        title = "¿Tus calificaciones se basan en porcentajes?",
        description = "Suma directamente los porcentajes que ganas en cada tarea (ej. sacas 8% de un 10% posible) en lugar de usar la escala de notas habitual."
    ) {
        SwitchCardComp(
            checked = viewModel.isDirectPercentage.value,
            onCheckedChange = { viewModel.setDirectPercentage(it) },
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onSurface,
            icon = R.drawable.layers_outline,
            text = "Usar porcentaje acumulativo como calificación",
        )
    }
}

@Composable
private fun RoundingPage(viewModel: InitialConfigViewModel) {
    StepPageLayout(
        icon = R.drawable.round,
        title = "¿Se redondean las notas finales?",
        description = "Ajusta la nota definitiva de la materia al número entero más cercano (ej. de 8.6 pasa a 9), lo que cambia tu promedio general.",
    ) {
        SwitchCardComp(
            checked = viewModel.isRoundFinalCourseAverage.value,
            onCheckedChange = { viewModel.setRoundFinalCourseAverage(it) },
            contentColor = MaterialTheme.colorScheme.onSurface,
            iconColor = MaterialTheme.colorScheme.onSurface,
            icon = R.drawable.round,
            text = "Redondear promedio para asignaturas finalizadas",
        )
    }
}

@Composable
private fun StepPageLayout(
    icon: Int,
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Left,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun NavigationButtons(
    currentPage: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit,
) {
    val isLastPage = currentPage == TOTAL_PAGES - 1
    val isFirstPage = currentPage == 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .animateContentSize(),
    ) {
        Button(
            onClick = when {
                isLastPage -> onFinish
                else -> onNext
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            Text(
                text = when {
                    isFirstPage -> "Comenzar"
                    isLastPage -> "Finalizar"
                    else -> "Siguiente"
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (!isFirstPage) {
                OutlinedButton(onClick = onPrevious) {
                    Text("Atrás")
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            TextButton(onClick = onSkip) {
                Text("Omitir")
            }
        }
    }
}
