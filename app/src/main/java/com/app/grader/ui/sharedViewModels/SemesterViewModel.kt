package com.app.grader.ui.sharedViewModels

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.DeleteCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import kotlinx.coroutines.launch

open class SemesterViewModel(
    protected val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    protected val deleteCourseByIdUseCase: DeleteCourseByIdUseCase,
    protected val getGradesFromSemesterUseCase: GetGradesFromSemesterUseCase,
    protected val getAverageFromSemesterUseCase: GetAverageFromSemesterUseCase,
    protected val gradeFactory: GradeFactory,
): ViewModel() {
    private val _totalAverage = mutableStateOf(gradeFactory.instGrade())
    val totalAverage = _totalAverage
    private val _deleteCourseId = mutableIntStateOf(-1)

    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades

    fun selectDeleteCourse(courseId: Int){
        viewModelScope.launch {
            _deleteCourseId.intValue = courseId
        }
    }

    fun deleteSelectedCourse(onDeleteAction : () -> Unit = {}) {
        viewModelScope.launch {
            deleteCourseByIdUseCase(_deleteCourseId.intValue).collect{ result ->
                when (result){
                    is Resource.Success -> {
                        onDeleteAction()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("SemesterViewModel", "Error deleteSelectedCourse: ${result.message}")
                    }
                }
            }
        }
    }

    fun getCoursesAndCalTotalAverageFromSemester(semesterId: Int?){
        getCoursesFromSemester(semesterId)
        calTotalAverage(semesterId)
    }

    private fun calTotalAverage(semesterId: Int?){
        viewModelScope.launch {
            getAverageFromSemesterUseCase(semesterId).collect{ result ->
                when (result){
                    is Resource.Success -> {
                        _totalAverage.value = result.data!!
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("SemesterViewModel", "Error calTotalAverage: ${result.message}")
                    }
                }
            }
        }
    }

    private fun getCoursesFromSemester(semesterId: Int?) {
        viewModelScope.launch {
            getCoursesFromSemesterUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!
                        _isLoading.value = false
                    }
                    is Resource.Loading -> { _isLoading.value = true }
                    is Resource.Error -> {
                        Log.e("SemesterViewModel", "Error getAllcourse: ${result.message}")
                    }
                }
            }
        }
    }

    fun getGradeFromSemester(semesterId: Int?) {
        viewModelScope.launch {
            getGradesFromSemesterUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _grades.value = result.data!!
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("SemesterViewModel", "Error getAllGrades: ${result.message}")
                    }
                }
            }
        }
    }
}