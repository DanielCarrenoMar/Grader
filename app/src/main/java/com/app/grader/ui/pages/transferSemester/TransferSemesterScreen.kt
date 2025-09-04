package com.app.grader.ui.pages.transferSemester

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.app.grader.R
import com.app.grader.domain.types.cardTypeFromCourse
import com.app.grader.ui.componets.EditScreenInputComp
import com.app.grader.ui.componets.HeaderBack
import com.app.grader.ui.componets.card.CourseCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferSemesterScreen(
    navigateBack: () -> Unit,
    viewModel: TransferSemesterViewModel = hiltViewModel(),
    ) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.getCoursesFromSemester(null)
        }
    }

    HeaderBack(
        title = {
            Row (
                modifier = Modifier.fillMaxWidth().padding(end = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Button(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        viewModel.transferCoursesToNewSemester()
                        navigateBack()
                }) {
                    Text(text = "Guardar")
                }
                Spacer(Modifier.weight(0.3f))
            }
        },
        navigateBack = navigateBack
    ) { innerPadding ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(10.dp))
                EditScreenInputComp(
                    placeHolderText = "Sin título",
                    value = viewModel.showTitle.value,
                    onValueChange = {
                        viewModel.showTitle.value = it
                        viewModel.title.value = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    leadingIconId = R.drawable.bookmark_outline,
                    maxLength = 50
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "El registro actual contiene:"
                )
                Spacer(Modifier.height(10.dp))
            }

            items(viewModel.courses.value){ course ->
                val courseCardType = cardTypeFromCourse(course)
                CourseCard(
                    course,
                    {},
                    {},
                    {},
                    type = courseCardType
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}