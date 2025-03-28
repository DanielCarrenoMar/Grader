package com.app.grader.ui.editCourse

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.course.SaveCourseUserCase
import com.app.grader.domain.usecase.course.UpdateCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCourseViewModel @Inject constructor(
    private val getCourseFromIdUseCase: GetCourseFromIdUseCase,
    private val saveCourseUserCase: SaveCourseUserCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
): ViewModel() {
    private val _title = mutableStateOf("Sin Titulo")
    val title = _title
    private val _uc = mutableIntStateOf(1)
    val uc = _uc

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle
    private val _showUc = mutableStateOf("")
    val showUc = _showUc

    fun getCourseFromId(courseId: Int) {
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseFromIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val course = result.data!!
                        _title.value = course.title
                        _showTitle.value = course.title
                        _uc.intValue = course.uc
                        _showUc.value = course.uc.toString()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveCourse(courseModel:CourseModel) {
        viewModelScope.launch {
            saveCourseUserCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditCourseViewModel", "saveGrade id: ${courseModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateCourse(courseModel:CourseModel) {
        viewModelScope.launch {
            updateCourseUseCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditCourseViewModel", "updateCourse id: ${courseModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateOrCreateCourse(courseId: Int){
        viewModelScope.launch {
            if (courseId == -1) {
                saveCourse(
                    CourseModel(
                        title = title.value,
                        uc = uc.intValue,
                        average = 0.0
                    )
                )
            } else {
                updateCourse(
                    CourseModel(
                        title = title.value,
                        uc = uc.intValue,
                        average = 0.0,
                        id = courseId
                    )
                )
            }
        }
    }
}