package com.app.grader.ui.pages.course

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import com.app.grader.domain.types.Grade
import com.app.grader.ui.componets.AddMenuComp
import com.app.grader.ui.componets.AddMenuCompItem
import com.app.grader.ui.componets.CardContainer
import com.app.grader.ui.componets.CircleGrade
import com.app.grader.ui.componets.CircleAverage
import com.app.grader.ui.componets.DeleteConfirmationComp
import com.app.grader.ui.componets.GradeCardComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.componets.IconCardButton
import com.app.grader.ui.componets.InfoGradeBottomCar
import com.app.grader.ui.componets.MenuAction
import com.app.grader.ui.componets.TitleIcon
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    courseId: Int,
    navigateBack: () -> Unit,
    navigateToEditCourse: (Int) -> Unit,
    navigateToEditGrade: (Int, Int) -> Unit,
    viewModel: CourseViewModel = hiltViewModel(),
) {
    val sheetState = rememberModalBottomSheetState(true)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteGradeConfirmation by remember { mutableStateOf(false) }
    var showDeleteSelfConfirmation by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getGradesFromCourse(courseId)
            viewModel.getCourseFromId(courseId)
            viewModel.calPoints(courseId)
        }
    }

    if (showDeleteGradeConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteGradeFromId(viewModel.showGrade.value.id) },
            { showDeleteGradeConfirmation = false }
        )
    }
    if (showDeleteSelfConfirmation){
        DeleteConfirmationComp(
            { viewModel.deleteSelf(navigateBack) },
            { showDeleteSelfConfirmation = false }
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
        navigateBack = navigateBack,
        actions = listOf(
            MenuAction("Editar"){navigateToEditCourse(courseId)},
            MenuAction("Eliminar"){showDeleteSelfConfirmation = true},
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .let { baseModifier ->
                    if (viewModel.isEditingGrade.value) {
                        baseModifier.clickable(
                            onClick = { viewModel.isEditingGrade.value = false },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    } else baseModifier
                },
        ) {
            InfoCourseCard(
                viewModel.course.value.average,
                viewModel.accumulatePoints.value,
                viewModel.pedingPoints.value,
                viewModel.course.value.uc
            )
            Spacer(modifier = Modifier.height(25.dp))
            CardContainer (
                modifier = Modifier.weight(1f)
            ){ innerPadding ->
                Column (modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxHeight()) {
                    TitleIcon(
                        iconName = "star",
                        iconId =  R.drawable.star
                    ) {
                        Text(text = "Calificaciones", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(6.dp))
                        if (viewModel.totalPercentaje.value.getPercentage() != 0.0) Text(
                            modifier = Modifier.alpha(if (viewModel.totalPercentaje.value.getPercentage() >= 100) 0.4f else 1f),
                            text = viewModel.totalPercentaje.value.toString() + "%",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (viewModel.totalPercentaje.value.getPercentage() >= 100) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    LazyColumn {
                        items(viewModel.grades.value) { grade ->
                            GradeCardComp(
                                grade = grade,
                                onClick = {
                                    viewModel.setShowGrade(grade.id)
                                    showBottomSheet = true
                                },
                                onLongClick = {
                                    viewModel.setShowGrade(grade.id)
                                    viewModel.isEditingGrade.value = true
                                },
                                isEditing = viewModel.isEditingGrade.value,
                                onInputValueChange = { newValue ->
                                    if (newValue.isBlank()) {
                                        grade.grade.setBlank()
                                        viewModel.updateGrade(grade)
                                        return@GradeCardComp
                                    }
                                    val numberValue = newValue.toDoubleOrNull()
                                    if (numberValue == null) return@GradeCardComp
                                    if (!Grade.check(numberValue)) return@GradeCardComp
                                    grade.grade.setGrade(numberValue)
                                    viewModel.updateGrade(grade)
                                },
                            )
                        }
                    }
                }
            }
            if (showBottomSheet) {
                InfoGradeBottomCar(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    showGrade = viewModel.showGrade.value,
                    editOnClick = {
                        navigateToEditGrade(viewModel.showGrade.value.id, courseId)
                        showBottomSheet = false
                                  },
                    deleteOnClick = { showDeleteGradeConfirmation = true; showBottomSheet = false }
                )
            }
        }
        AddMenuComp(listOf(
            AddMenuCompItem("Calificación", R.drawable.star_outline){
                if (viewModel.pedingPoints.value.getGrade() > 0){
                    navigateToEditGrade(-1, courseId)
                }else coroutineScope.launch {
                    snackbarHostState.showSnackbar("Los porcentajes de las calificaciones ya suman 100%")
                }
            },
        ))
    }
}

@Composable
fun InfoCourseCard(average: Grade, accumulatePoints:Grade, pendingPoints: Grade, uc: Int){
    val animatedAccumulatePoints by animateFloatAsState(
        targetValue = accumulatePoints.getGrade().toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "accumulatePointsAnimation"
    )

    val animatedPendingPoints by animateFloatAsState(
        targetValue = pendingPoints.getGrade().toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "pendingPointsAnimation"
    )

    CardContainer{ innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
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
                CircleAverage(average ,accumulatePoints.getGrade(), pendingPoints.getGrade())
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
                                    text =  Grade.formatText(animatedAccumulatePoints),
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
                                    text = Grade.formatText(animatedPendingPoints),
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