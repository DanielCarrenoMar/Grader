package com.app.grader.ui.home

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.DeleteCourseFromIdUseCase
import com.app.grader.domain.usecase.course.GetAllCoursesUserCase
import com.app.grader.domain.usecase.grade.GetAllGradesUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val getAllCoursesUserCase: GetAllCoursesUserCase,
    private val deleteCourseFromIdUseCase: DeleteCourseFromIdUseCase,
    private val getAllGradesUseCase: GetAllGradesUserCase
): ViewModel() {
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades
    private val _deleteCourseId = mutableIntStateOf(-1)
    val deleteCourseId = _deleteCourseId
    private val _totalAverage = mutableDoubleStateOf(0.0)
    val totalAverage = _totalAverage

    fun deleteSelectedCourse(){
        viewModelScope.launch {
            deleteCourseFromIdUseCase(_deleteCourseId.intValue).collect{ result ->
                when (result){
                    is Resource.Success -> {
                        getAllCoursesAndCalTotalAverage()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error deleteSelectedCourse: ${result.message}")
                    }
                }
            }
        }
    }

    fun selectDeleteCourse(courseId: Int){
        viewModelScope.launch {
            _deleteCourseId.intValue = courseId
        }
    }

    fun getAllCoursesAndCalTotalAverage() {
        viewModelScope.launch {
            getAllCoursesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!
                        if (_courses.value.isNotEmpty()){
                            var totalGrades = 0.0
                            var totalUC = 0

                            _courses.value.forEach { course ->
                                if (course.average != 0.0) {
                                    totalGrades += course.average * course.uc
                                    totalUC += course.uc
                                }
                            }
                            if (totalUC != 0) _totalAverage.doubleValue = totalGrades / totalUC
                            else _totalAverage.doubleValue = 0.0
                        }else _totalAverage.doubleValue = 0.0
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error getAllcourse: ${result.message}")
                    }
                }
            }
        }
    }

    fun getAllGrades() {
        viewModelScope.launch {
            getAllGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _grades.value = result.data!!
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error getAllGrades: ${result.message}")
                    }
                }
            }
        }
    }
}