package com.app.grader.ui.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.grader.R
import com.app.grader.domain.model.CourseModel

@Composable
fun CourseCardComp(
    course: CourseModel,
    navigateToCourse: () -> Unit,
    deleteCourse: ()-> Unit,
    editCourse: ()-> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    CardContainer(
        onClick = navigateToCourse
    ) { innerPading ->
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPading),
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Row (
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(28.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.education_cap),
                        contentDescription = "educationCap",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column{
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 26.dp)
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
            }
            Row (verticalAlignment = Alignment.Top){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(vertical = 4.dp),
                ){
                    CircleGrade(course.average, strokeWith =  5.dp, radius =  32.dp)
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
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = editCourse,
                            text = {
                                Text("Editar")
                            })
                        DropdownMenuItem(onClick = deleteCourse,
                        text = {
                            Text("Borrar")
                        })
                    }
                }
            }
        }

    }
}