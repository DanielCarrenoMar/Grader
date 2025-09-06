package com.app.grader.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
            view.setBackgroundColor(color)
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        // For Android 14 and below
        window.statusBarColor = color
    }
}

@Composable
fun NavigationGuideTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    isDynamicColor: Boolean = Build.VERSION.SDK_INT >= 32, // Dynamic color is available on Android 12+ (api 32)
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
