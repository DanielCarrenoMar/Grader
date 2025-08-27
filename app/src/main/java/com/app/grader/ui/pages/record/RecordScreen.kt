package com.app.grader.ui.pages.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.FloatingMenuComp
import com.app.grader.ui.componets.FloatingMenuCompItem
import com.app.grader.ui.componets.HeaderMenu
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.componets.card.CardContainer
import com.app.grader.ui.componets.card.RecordSemesterCard
import kotlin.math.roundToInt

@Composable
fun RecordScreen(
    navigateToHome: () -> Unit,
    navigateToAllGrades: () -> Unit,
    navigateToConfig: () -> Unit,
    viewModel: RecordViewModel = hiltViewModel(),
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getAllSemestersAndCalTotalAverage()
        }
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationComp(
            { },
            { showDeleteConfirmation = false }
        )
    }

    HeaderMenu(
        "Hist. Académico",
        navigateToHome,
        navigateToAllGrades,
        null,
        navigateToConfig
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = innerPadding.calculateTopPadding() + 10.dp,
                bottom = innerPadding.calculateBottomPadding() + 80.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(
                span = { GridItemSpan(2) }
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                InforecordCard(viewModel.totalAverage.doubleValue)
                Spacer(modifier = Modifier.height(25.dp))
            }
            items(viewModel.semesters.value) { semester ->
                RecordSemesterCard(
                    average = semester.average,
                    title = semester.title,
                    coursesNum = semester.size,
                    onClick = { }
                )
            }
        }

        FloatingMenuComp(
            listOf(
                FloatingMenuCompItem(
                    "Guardar actual",
                    R.drawable.education_cap_outline
                ) { },
                FloatingMenuCompItem(
                    "Crear nuevo",
                    R.drawable.star_outline
                ) { },
            )
        )
    }
}

@Composable
fun InforecordCard(average: Double) {
    CardContainer { innerPading ->
        Column(
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(200.dp),
        ) {
            TitleIcon(
                iconName = "chart mixed",
                iconId = R.drawable.chart_mixed
            ) {
                Text(text = "Promedio General", style = MaterialTheme.typography.labelLarge)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (average != 0.0) "${(average * 100).roundToInt() / 100.0}" else "--",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 52.sp,
                )
            }
        }
    }
}