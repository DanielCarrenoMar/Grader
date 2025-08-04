package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.domain.model.CourseModel

sealed class Mode {
    object Normal : Mode()
    object Fail : Mode()
    object Pass : Mode()
}

@Composable
fun CourseCardComp(
    course: CourseModel,
    navigateToCourse: () -> Unit,
    deleteCourse: ()-> Unit,
    editCourse: ()-> Unit,
    mode: Mode = Mode.Normal
) {
    var expanded by remember { mutableStateOf(false) }
    val screenWidth = LocalWindowInfo.current.containerSize.width.dp
    val iconResId = when (mode) {
        is Mode.Normal -> R.drawable.education_cap
        is Mode.Pass -> R.drawable.ic_pass_icon
        is Mode.Fail -> R.drawable.ic_fail_icon
    }

    CardContainer(
        onClick = navigateToCourse
    ) { innerPading ->
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPading),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Column (
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Top
            ){
                TitleIcon(
                    iconName = "education cap",
                    iconId =  iconResId,
                    backgroundColor = MaterialTheme.colorScheme.primary
                ){
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 27.dp, start = 40.dp)
                ){
                    Text(
                        text = "${course.uc}",
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
            Row (
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.End
            ){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 4.dp),
                ){
                    CircleCourse(
                        grade = course.average,
                        radius =  33.dp)
                }
                Box(contentAlignment = Alignment.TopEnd){
                    IconButton(
                        onClick = { expanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dots_vertical_outline),
                            contentDescription = "edit",
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = screenWidth - 200.dp, y = 0.dp),
                        modifier = Modifier.width(200.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        DropdownMenuItem(
                            onClick = { editCourse();expanded = false },
                            text = {
                                Text(
                                    "Editar",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },

                            )
                        DropdownMenuItem(onClick = {deleteCourse();expanded = false},
                        text = {
                            Text("Eliminar", style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                        }
                        )
                    }
                }
            }
        }

    }
}