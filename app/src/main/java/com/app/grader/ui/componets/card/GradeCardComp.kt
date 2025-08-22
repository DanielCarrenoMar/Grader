package com.app.grader.ui.componets.card

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.grader.core.lib.getColorForGrade
import com.app.grader.domain.model.GradeModel
import com.app.grader.ui.componets.chart.CircleGrade

@Composable
fun GradeCardComp(
    grade: GradeModel,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    isEditing: Boolean = false,
    onInputValueChange: (String) -> Unit = {},
) {
    val maxGradeLength = grade.grade.getMax().toString().length

    var gradeTextFieldValue by remember {
        mutableStateOf(TextFieldValue(grade.grade.toString()))
    }
    val focusRequester = remember { FocusRequester() }
    var requestFocus by remember { mutableStateOf(false) }

    LaunchedEffect(requestFocus, Unit) {
        if (requestFocus) {
            val currentText = TextFieldValue(grade.grade.toString()).text
            gradeTextFieldValue = TextFieldValue(grade.grade.toString()).copy(
                selection = TextRange(currentText.length)
            )
            focusRequester.requestFocus()
            requestFocus = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    if (onLongClick != null){
                        onLongClick()
                        requestFocus = true
                    }
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(5.dp)
        ){
                if (isEditing) {
                    OutlinedTextField(
                        modifier = Modifier
                            .size(50.dp)
                            .focusRequester(focusRequester),
                        value = gradeTextFieldValue,
                        onValueChange = {
                            if (it.text.length <= maxGradeLength) {
                                gradeTextFieldValue = it
                                onInputValueChange(it.text)
                            }
                                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        shape = MaterialTheme.shapes.extraLarge,
                        textStyle = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = getColorForGrade(grade.grade)
                        )
                    )
                } else{
                    CircleGrade(
                        grade = grade.grade,
                        radius = 25.dp
                    )
                }
            Column(
                modifier = Modifier.padding(start = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = grade.title,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = grade.percentage.toString() + "%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
    }
}