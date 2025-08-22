package com.app.grader.ui.pages.allGrades

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.GetAllCoursesUserCase
import com.app.grader.domain.usecase.grade.DeleteGradeFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradeFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllGradesViewModel  @Inject constructor(
    private val getAllCoursesUseCase: GetAllCoursesUserCase,
    private val getGradesFromCourseUseCase: GetGradesFromCourseUseCase,
    private val deleteGradeFromIdUseCase: DeleteGradeFromIdUseCase,
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase
): ViewModel() {
    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading
    private val _grades = mutableStateOf<List<List<GradeModel>>>(emptyList())
    val grades = _grades
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses
    private val _showGrade = mutableStateOf(GradeModel.DEFAULT)
    val showGrade = _showGrade

    fun deleteGradeFromId(gradeId: Int){
        viewModelScope.launch {
            deleteGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getAllGradesWithCourses()
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

    fun getAllGradesWithCourses() {
        _isLoading.value = true
        viewModelScope.launch {
            getAllCoursesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val courses = result.data
                        if (courses.isNullOrEmpty()) {
                            _grades.value = emptyList()
                            _isLoading.value = false
                            return@collect
                        }
                        _courses.value = courses

                        val newGradesByCourses = MutableList<List<GradeModel>>(courses.size) { emptyList() }

                        courses.forEachIndexed { index, course ->
                            getGradesFromCourseUseCase(course.id).collect { gradeResult ->
                                when (gradeResult) {
                                    is Resource.Success -> {
                                        newGradesByCourses[index] = gradeResult.data ?: emptyList()
                                    }
                                    is Resource.Loading -> {}
                                    is Resource.Error -> {
                                        Log.e(
                                            "CourseViewModel",
                                            "Error getGradesFromCourse: ${gradeResult.message}"
                                        )
                                        newGradesByCourses[index] = emptyList()
                                    }
                                }
                            }
                        }
                        _grades.value = newGradesByCourses
                        _isLoading.value = false
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getGradesFromCourse: ${result.message}")
                    }
                }
            }
        }
    }
}