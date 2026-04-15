package com.app.grader.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.core.appConfig.TypeTheme
import com.app.grader.core.navigation.NavigationWrapper
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.review.LaunchInAppReviewIfValidUseCase
import com.app.grader.ui.theme.NavigationGuideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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