package com.app.grader.debug

import androidx.compose.runtime.Composable

class DebugHelper {
    companion object {
        @Composable
        fun DebugOptionsComp() {
            return com.app.debugGrader.ui.pages.config.DebugOptionsComp()
        }
    }
}