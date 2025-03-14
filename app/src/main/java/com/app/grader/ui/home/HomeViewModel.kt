package com.app.grader.ui.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.SaveCourseUserCase
import com.app.grader.domain.usecase.DeleteAllCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val getAllCoursesUserCase: GetAllCoursesUserCase,
    private val saveCourseUserCase: SaveCourseUserCase,
    private val deleteAllCoursesUserCase: DeleteAllCoursesUseCase
): ViewModel() {

    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses

    fun getAllCourses() {
        viewModelScope.launch {
            getAllCoursesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!
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
    fun saveCourse() {
        val courseModel = CourseModel(
            title ="Curso",
            description =  "Descripcion",
            uc =  2
        )
        viewModelScope.launch {
            saveCourseUserCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getAllCourses()
                        Log.i("HomeViewModel", "saveGrade Cantidad: " + _courses.value.size)
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteAllCourses() {
        viewModelScope.launch {
            deleteAllCoursesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("HomeViewModel", "deleteAllCourses Cantidad: " + result.data)
                        _courses.value = emptyList()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error deleting all courses: ${result.message}")
                    }
                }
            }
        }
    }
}