package com.app.grader.ui.config

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.grade.DeleteAllGradesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel  @Inject constructor(
    private val deleteAllGradesUseCase: DeleteAllGradesUseCase,
    private val deleteAllCoursesUseCase: DeleteAllCoursesUseCase
): ViewModel() {
    fun deleteAll(){
        viewModelScope.launch {
            deleteAllGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ConfigViewModel", "deleteAllGradesUseCase: Success ${result.data}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("ConfigViewModel", "Error deleteAllGradesUseCase: ${result.message}")
                    }
                }
            }
            deleteAllCoursesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("ConfigViewModel", "deleteAllCoursesUseCase: Success ${result.data}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("ConfigViewModel", "Error deleteAllCoursesUseCase: ${result.message}")
                    }
                }
            }
        }
    }
}