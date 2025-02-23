package com.app.grader.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.grader.core.navigation.NavigationWrapper
import com.app.grader.ui.theme.NavigationGuideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            NavigationGuideTheme {
                NavigationWrapper()
            }
        }
    }
}