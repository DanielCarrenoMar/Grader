package com.app.grader.ui.config

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import com.app.grader.ui.componets.HeaderMenu

@Composable
fun ConfigScreen( navigateToHome: () -> Unit, navigateToAllGrades: (String) -> Unit) {
    val insets = WindowInsetsCompat.toWindowInsetsCompat(LocalView.current.rootWindowInsets)
    val statusBarHeight = with(LocalDensity.current) { insets.getInsets(Type.statusBars()).top.toDp() }

    HeaderMenu ("Ajustes",
        {navigateToHome()},
        {navigateToAllGrades("a")},
        null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusBarHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Config SCREEN", fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}