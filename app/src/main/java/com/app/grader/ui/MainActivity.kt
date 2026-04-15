package com.app.grader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.app.grader.data.appConfig.AppConfigRepository
import com.app.grader.core.appConfig.TypeTheme
import com.app.grader.core.navigation.NavigationWrapper
import com.app.grader.ui.theme.NavigationGuideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appConfigRepository by lazy { AppConfigRepository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            NavigationGuideTheme (
                isDarkTheme = when (appConfigRepository.getTypeTheme()){
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