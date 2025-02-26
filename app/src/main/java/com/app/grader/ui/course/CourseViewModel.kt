package com.app.grader.ui.course

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.SaveCourseUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel  @Inject constructor(
    private val getAllCoursesUserCase: GetAllCoursesUserCase,
    private val saveCourseUserCase: SaveCourseUserCase
): ViewModel() {

    private val _showCourses = mutableStateOf<List<CourseModel>>(emptyList())
    val showCourses = _showCourses

    fun getAllGrades() {
        viewModelScope.launch {
            getAllCoursesUserCase().collect { result ->
                if (result is Resource.Success) {
                    _showCourses.value = result.data!!
                    result.data.forEach { course ->
                        Log.i("CourseViewModel", "Course: ${course.title}, Description: ${course.description}, UC: ${course.uc}")
                    }
                } else if (result is Resource.Error) {
                    Log.e("CourseViewModel", "Error fetching courses: ${result.message}")
                }
            }
        }
    }
    fun saveGrade() {
        val courseModel = CourseModel("Curso", "Descripcion", 2)
        viewModelScope.launch {
            saveCourseUserCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getAllGrades()
                        Log.i("CourseViewModel", "saveGrade Cantidad: " + _showCourses.value.size)
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }
}