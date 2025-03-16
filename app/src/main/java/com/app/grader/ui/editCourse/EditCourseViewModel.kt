package com.app.grader.ui.editCourse

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.SaveCourseUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCourseViewModel   @Inject constructor(
    private val getCourseFromIdUseCase: GetCourseFromIdUseCase,
    private val saveCourseUserCase: SaveCourseUserCase,
): ViewModel() {
    private val _course = mutableStateOf<CourseModel>(
        CourseModel(
            title = "Sin Titulo",
            description = "Sin Descricción",
            uc = 1,
            average = 0.0
        )
    )
    val course = _course
    private val _title = mutableStateOf<String>("")
    val title = _title
    private val _description = mutableStateOf<String>("")
    val description = _description
    private val _uc = mutableIntStateOf(0)
    val uc = _uc

    fun getCourseFromId(courseId: Int) {
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseFromIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _course.value = result.data!!
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

    private fun saveCourse(courseModel:CourseModel) {
        viewModelScope.launch {
            saveCourseUserCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditCourseViewModel", "saveGrade state: $result")
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

    fun saveOrCreateCourse(courseId: Int){
        viewModelScope.launch {
            if (courseId == -1) {
                saveCourse(
                    CourseModel(
                        title = title.value,
                        description = description.value,
                        uc = uc.intValue,
                        average = 0.0
                    )
                )
            } else {
                TODO("Update course")
            }
        }
    }
}