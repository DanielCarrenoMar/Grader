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
import com.app.grader.domain.usecase.review.LaunchInAppReviewUseCase
import com.app.grader.ui.theme.NavigationGuideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val appConfig by lazy { AppConfig(this) }

    @Inject lateinit var launchInAppReviewUseCase: LaunchInAppReviewUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            LaunchedEffect(Unit) {
                launchInAppReviewUseCase(this@MainActivity).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            if (resource.data == true) {
                                Log.d("MainActivity", "Prompting in-app review")
                            } else {
                                Log.d("MainActivity", "Not prompting in-app review")
                            }
                        }
                        is Resource.Error -> {
                            Log.e("MainActivity", "Error checking in-app review: ${resource.message}")
                        }
                    }
                }
            }

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