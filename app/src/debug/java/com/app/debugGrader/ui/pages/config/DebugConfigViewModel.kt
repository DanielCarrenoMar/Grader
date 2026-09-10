package com.app.debugGrader.ui.pages.config

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.infrastructure.database.AppDatabase
import com.app.debugGrader.data.database.DebugDatabaseSeeder
import com.app.grader.infrastructure.playStore.PlayStoreDistributionRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DebugConfigViewModel @Inject constructor(
    private val appDatabase: AppDatabase,
    private val playStoreDistributionRepositoryImp: PlayStoreDistributionRepositoryImp
) : ViewModel() {

    fun loadDebugData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    DebugDatabaseSeeder.seed(appDatabase, true)
                }
                Log.i("DebugConfigViewModel", "Debug data seeded successfully")
            } catch (e: Exception) {
                Log.e("DebugConfigViewModel", "Error loading debug data", e)
            }
        }
    }

    fun launchReviewFlow(activity: android.app.Activity, onComplete: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            playStoreDistributionRepositoryImp.requestReview(activity)
                .onSuccess { onComplete() }
                .onFailure { onError() }
        }
    }
}