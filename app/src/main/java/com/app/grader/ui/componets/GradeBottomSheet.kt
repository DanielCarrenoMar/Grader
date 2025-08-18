package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.R
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.ui.componets.card.IconCardButton
import com.app.grader.ui.componets.chart.CircleGrade
import com.app.grader.ui.theme.Error500
import com.app.grader.ui.theme.IconLarge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    showGrade: GradeModel,
    editOnClick: () -> Unit,
    deleteOnClick: () -> Unit
) {
    val accumulatePoints =
        if (showGrade.grade.isBlank()) Grade() else Grade(showGrade.grade.getGrade() * (showGrade.percentage.getPercentage() / 100))

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 15.dp)
                ) {
                    CircleGrade(
                        grade = showGrade.grade,
                        radius = 25.dp,
                        fontSize = 18.sp
                    )
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
                        text = showGrade.percentage.toString() + "%",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    if (accumulatePoints.isNotBlank()){
                        Spacer(Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = Grade.formatText(accumulatePoints.getGrade()),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.tertiary,
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Ptos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
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
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
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