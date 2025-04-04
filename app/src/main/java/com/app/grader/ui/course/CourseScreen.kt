package com.app.grader.ui.course

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.ui.componets.AddMenuComp
import com.app.grader.ui.componets.AddMenuCompItem
import com.app.grader.ui.componets.CardContainer
import com.app.grader.ui.componets.CircleGrade
import com.app.grader.ui.componets.CirclePie
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.componets.IconCardButton
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge
import com.app.grader.ui.theme.Secondary600
import com.app.grader.ui.theme.Success500
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courseId: Int,
    navegateBack: () -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(true)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getGradesFromCourse(courseId)
            viewModel.getCourseFromId(courseId)
            viewModel.calPoints(courseId)
        }
    }

    if (showDeleteConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) },
            { showDeleteConfirmation = false }
        )
    }
    HeaderBack(
        text = {
            Text(
                text = viewModel.course.value.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState,
                snackbar = {
                    Snackbar(
                        it,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        },
        navigateBack = navegateBack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            InfoCourseCard(
                viewModel.course.value.average,
                viewModel.accumulatePoints.doubleValue,
                viewModel.pedingPoints.doubleValue,
                viewModel.course.value.uc
            )
            Spacer(modifier = Modifier.height(25.dp))
            CardContainer (
                modifier = Modifier.weight(1f)
            ){ innerPadding ->
                Column (modifier = Modifier.padding(innerPadding).fillMaxHeight()) {
                    TitleIcon(
                        iconName = "star",
                        iconId =  R.drawable.star
                    ) {
                        Text(text = "Calificaciones", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    LazyColumn {
                        items(viewModel.grades.value) { grade ->
                            GradeCardComp(grade) {
                                viewModel.setShowGrade(grade.id)
                                showBottomSheet = true
                            }
                        }
                    }
                }
            }
            if (showBottomSheet) {
                InfoGradeBottonCar(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    showGrade = viewModel.showGrade.value,
                    editOnClick = {
                        navigateToEditGrade(viewModel.showGrade.value.id, courseId)
                        showBottomSheet = false
                                  },
                    deleteOnClick = { showDeleteConfirmation = true; showBottomSheet = false }
                )
            }
        }
        AddMenuComp(listOf(
            AddMenuCompItem("Calificación", R.drawable.star_outline){
                if (viewModel.pedingPoints.doubleValue > 0){
                    navigateToEditGrade(-1, courseId)
                }else coroutineScope.launch {
                    snackbarHostState.showSnackbar("Los porcentajes de las calificaciones ya suman 100%")
                }
            },
        ))
    }
}

@Composable
fun InfoCourseCard(average: Double, accumulatePoints:Double, pendingPoints: Double, uc: Int){
    CardContainer{ innerPading ->
        Column (
            modifier = Modifier.padding(innerPading)
        ){
            TitleIcon(
                iconName = "chart pie",
                iconId = R.drawable.chart_pie
            ) {
                Text(text = "Promedio", style = MaterialTheme.typography.labelLarge)
            }
            Row( modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 10.dp)
            ) {
                CirclePie(average ,accumulatePoints, pendingPoints)
                Column(modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                        ) {
                            Row (verticalAlignment = Alignment.Bottom){
                                Text(
                                    text = "${(accumulatePoints * 10).roundToInt() / 10.0}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.tertiary,
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Ptos. Acumulados",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Row (verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${(pendingPoints * 10).roundToInt() / 10.0}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "Ptos. Por Evaluar",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            text = "$uc",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "UC",
                            modifier = Modifier
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoGradeBottonCar(
    onDismissRequest: ()-> Unit,
    sheetState: SheetState,
    showGrade: GradeModel,
    editOnClick: ()->Unit,
    deleteOnClick: ()->Unit
){
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn  (
            modifier = Modifier
                .padding(horizontal =  20.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 15.dp)
                ) {
                    CircleGrade(showGrade.grade, radius = 25.dp, fontSize = 18.sp)
                    Text(
                        text = showGrade.title,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.weight),
                        contentDescription = "weight",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .size(IconLarge),
                    )
                    Text(
                        text = "${if (showGrade.percentage % 1 == 0.0) showGrade.percentage.toInt() else showGrade.percentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 15.dp)
                ) {
                    Text(
                        text = showGrade.description,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(20.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                IconCardButton(
                    onClick = editOnClick,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    icon = R.drawable.pen_outline,
                    text = "Editar"
                )
                HorizontalDivider( modifier = Modifier.alpha(0.5f))
                IconCardButton(
                    onClick = deleteOnClick,
                    contentColor = Error500,
                    icon = R.drawable.trash_outline,
                    text = "Eliminar"
                )
                Spacer(Modifier.height(60.dp))
            }
        }
    }
}