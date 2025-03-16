package com.app.grader.ui.course

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.SaveCourseUserCase
import com.app.grader.domain.usecase.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.GetGradesFromCourseUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel  @Inject constructor(
    private val getGradesFromCourseUserCase: GetGradesFromCourseUserCase,
    private val getCourseFromIdUseCase: GetCourseFromIdUseCase
): ViewModel() {
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades

    private val _course = mutableStateOf<CourseModel>(
        CourseModel(
            title = "NULL",
            description = "Null",
            uc = 0,
            average = 0.0
        )
    )
    val course = _course

    fun getCourseFromId(courseId: Int) {
        viewModelScope.launch {
            getCourseFromIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) _course.value = result.data!!
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

    fun getGradesFromCourse(courseId: Int) {
        viewModelScope.launch {
            getGradesFromCourseUserCase(courseId).collect { result ->
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