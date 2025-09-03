package com.app.grader.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.core.appConfig.TypeTheme
import com.app.grader.core.navigation.NavigationWrapper
import com.app.grader.ui.theme.NavigationGuideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appConfig by lazy { AppConfig(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            NavigationGuideTheme (
                isDarkTheme = when (appConfig.getTypeTheme()){
                    TypeTheme.DARK -> true
                    TypeTheme.LIGHT -> false
                    TypeTheme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                },
            ) {
                NavigationWrapper()
            }
        }
    }
}