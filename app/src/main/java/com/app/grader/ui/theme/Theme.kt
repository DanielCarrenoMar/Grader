package com.app.grader.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = Secondary400,
    background = BackgroundLight,
    inverseSurface = BackgroundLightInvert,
    surfaceVariant = BackgroundLightVar,
    onBackground = Neutral900,
    tertiary = SuccessLight,
    error = Error500,
    surface = Shadow50,
    onSurface = Neutral600,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = Secondary200,
    background = BackgroundDark,
    inverseSurface = BackgroundDarkInvert,
    surfaceVariant = BackgroundDarkVar,
    onBackground = Shadow50,
    tertiary = SuccessDark,
    error = Error500,
    surface = SurfaceDark,
    onSurface = Neutral200,
)

val replyShapes = Shapes(

)

fun setStatusBarColor(window: Window, color: Int) {
    // Edge-to-edge (enableEdgeToEdge + targetSdk 35+): Scaffold/TopAppBar ya aplican
    // WindowInsets.statusBars. No aplicar padding manual en decorView: duplicaba el inset
    // y empujaba el header hacia abajo en modelos con status bar alta (notch/cutout grande).
    window.decorView.setOnApplyWindowInsetsListener(null)
    window.decorView.setBackgroundColor(color)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        // Solo pre-Android 15: en 15+ statusBarColor se ignora en edge-to-edge
        window.statusBarColor = color
    }
}

@Composable
fun NavigationGuideTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        setStatusBarColor(window, colorScheme.background.toArgb())
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
    }

    MaterialTheme(
        shapes = replyShapes,
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
