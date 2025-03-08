package com.app.grader.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import com.app.grader.ui.componets.lateralMenu.HeaderMenu
import androidx.core.view.WindowInsetsCompat.Type
import com.app.grader.ui.componets.CommonLayout
import com.app.grader.ui.componets.lateralMenu.ItemLateralMenu

@Composable
fun HomeScreen(viewModel: HomeViewModel ,navigateToAllGrades: (String) -> Unit, navigateToCourse: () -> Unit) {
    val insets = WindowInsetsCompat.toWindowInsetsCompat(LocalView.current.rootWindowInsets)
    val statusBarHeight = with(LocalDensity.current) { insets.getInsets(Type.statusBars()).top.toDp() }

    HeaderMenu ("Asignaturas") {
        CommonLayout {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = statusBarHeight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.weight(1f))
                Text(text = "HOME SCREEN", fontSize = 25.sp)
                Spacer(modifier = Modifier.weight(1f))
                //Text(text = "Texto Transmitido a AllGrades: $text", fontSize = 14.sp)
                //TextField(value = text, onValueChange = {viewModel.onTextChanged(it)})
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { navigateToCourse() }) {
                    Text(text = "Navegar a Materia (Course)")
                }
                Button(onClick = { navigateToAllGrades("a") }) {
                    Text(text = "Navegar a todas las Notas (AllGrades)")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

}