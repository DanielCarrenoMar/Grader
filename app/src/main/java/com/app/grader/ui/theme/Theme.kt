package com.app.grader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = Primary400,
    secondary = Secondary600,
    background = BackgroundLight,
    surfaceVariant = BackgroundLightVar,
    onBackground = Neutral900,
    tertiary = Success500,
    error = Error500,
    surface = Shadow50,
    onSurface = Neutral600,
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary200,
    secondary = Secondary200,
    background = BackgroundDark,
    surfaceVariant = BackgroundDarkVar,
    onBackground = Shadow50,
    tertiary = Success400,
    error = Error500,
    surface = SurfaceDark,
    onSurface = Neutral200,
)

val replyShapes = Shapes(

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
        shapes = replyShapes,
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}