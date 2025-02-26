package com.app.grader.ui.course

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
                }
            }
        }
    }
}