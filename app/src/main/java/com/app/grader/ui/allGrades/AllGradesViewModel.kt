package com.app.grader.ui.allGrades

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.grade.DeleteGradeFromIdUseCase
import com.app.grader.domain.usecase.grade.GetAllGradesUserCase
import com.app.grader.domain.usecase.grade.GetGradeFromIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllGradesViewModel  @Inject constructor(
    private val getAllGradesUserCase: GetAllGradesUserCase,
    private val deleteGradeFromIdUseCase: DeleteGradeFromIdUseCase,
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase
): ViewModel() {
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades
    private val _showGrade = mutableStateOf(GradeModel.DEFAULT)
    val showGrade = _showGrade

    fun deleteGradeFromId(gradeId: Int){
        viewModelScope.launch {
            deleteGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getAllGrades()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error deleteGradeFromId: ${result.message}")
                    }
                }
            }
        }
    }

    fun setShowGrade(gradeId: Int){
        viewModelScope.launch {
            getGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) _showGrade.value = result.data
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun getAllGrades() {
        viewModelScope.launch {
            getAllGradesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _grades.value = result.data!!
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getGradesFromCourse: ${result.message}")
                    }
                }
            }
        }
    }
}