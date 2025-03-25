package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.domain.model.CourseModel
import com.app.grader.ui.theme.IconSmall

@Composable
fun CourseCardComp(
    course: CourseModel,
    navigateToCourse: () -> Unit,
    deleteCourse: ()-> Unit,
    editCourse: ()-> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    CardContainer(
        onClick = navigateToCourse
    ) { innerPading ->
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPading),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Column (verticalArrangement = Arrangement.Top){
                TitleIcon("education cap", R.drawable.education_cap, backgroundColor = MaterialTheme.colorScheme.primary){
                    Text(
                        text = if (course.title.length > 18) course.title.take(18) + "..." else course.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
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
            Row (verticalAlignment = Alignment.Top){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 4.dp),
                ){
                    CircleCourse(course.average, strokeWith =  5.dp, radius =  33.dp)
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
                        DropdownMenuItem(onClick = editCourse,
                            text = {
                                Text("Editar", style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            },

                        )
                        DropdownMenuItem(onClick = deleteCourse,
                        text = {
                            Text("Borrar", style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                        }
                        )
                    }
                }
            }
        }

    }
}