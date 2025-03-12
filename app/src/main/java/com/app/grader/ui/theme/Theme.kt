package com.app.grader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Secondary600,
    secondary = Secondary600,
    background = Background,
    onBackground = Neutral900,
    error = Error500,
    surface = Shadow50,
    onSurface = Neutral600,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary400,
    secondary = Secondary600,
    background = Background,
    onBackground = Neutral900,
    error = Error500,
    surface = Shadow50,
    onSurface = Neutral600,
)

@Composable
fun NavigationGuideTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = false, // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when {
        dynamicColor && isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }
        dynamicColor && !isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}