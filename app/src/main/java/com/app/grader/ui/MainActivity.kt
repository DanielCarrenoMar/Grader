package com.app.grader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.types.ThemeType
import com.app.grader.core.navigation.NavigationWrapper
import com.app.grader.ui.theme.NavigationGuideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appConfigRepository: AppConfigRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            NavigationGuideTheme (
                isDarkTheme = when (appConfigRepository.getTypeTheme()){
                    ThemeType.DARK -> true
                    ThemeType.LIGHT -> false
                    ThemeType.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                },
            ) {
                NavigationWrapper()
            }
        }
    }
}