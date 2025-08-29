package com.app.grader.ui.pages.recordSemester

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.course.DeleteCourseFromIdUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterUseCase
import com.app.grader.domain.usecase.semester.DeleteSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.ui.componets.card.CourseCardType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordSemesterViewModel  @Inject constructor(
    private val deleteSemesterByIdUseCase: DeleteSemesterByIdUseCase,
    private val getSemesterByIdUseCase: GetSemesterByIdUseCase,
    private val getCoursesFromSemesterIdUseCase: GetCoursesFromSemesterIdUseCase,
    private val deleteCourseFromIdUseCase: DeleteCourseFromIdUseCase,
    private val getGradesFromSemesterUseCase: GetGradesFromSemesterUseCase,
    private val getAverageFromSemesterUseCase: GetAverageFromSemesterUseCase,
): ViewModel() {
    private val _semester = mutableStateOf(SemesterModel.DEFAULT)
    val semester = _semester
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades
    private val _deleteCourseId = mutableIntStateOf(-1)
    private val _totalAverage = mutableDoubleStateOf(0.0)
    val totalAverage = _totalAverage

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    fun cardTypeFromCourse(course: CourseModel): CourseCardType {
        val accumulatedPoints = course.average.getGrade() * (course.totalPercentage.getPercentage() / 100.0)
        val pendingPoints = (100.0 - course.totalPercentage.getPercentage()) / 100.0 * 20.0

        return if (course.average.isFailValue(pendingPoints + accumulatedPoints)) {
            CourseCardType.Fail
        } else if (pendingPoints == 0.0) {
            CourseCardType.Finish
        } else if (!course.average.isFailValue(accumulatedPoints)) {
            CourseCardType.Pass
        } else {
            CourseCardType.Normal
        }
    }

    fun getSemester(semesterId: Int){
        viewModelScope.launch {
            getSemesterByIdUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _semester.value = result.data!!
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordSemesterViewModel", "Error getSemesterTitle: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteSelf(navigateTo: () -> Unit) {
        if (_semester.value.id == -1) return
        viewModelScope.launch {
            deleteSemesterByIdUseCase(_semester.value.id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        navigateTo()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordSemesterViewModel", "Error deleteSelf: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteSelectedCourse(onDeleteAction : () -> Unit = {}) {
        viewModelScope.launch {
            deleteCourseFromIdUseCase(_deleteCourseId.intValue).collect{ result ->
                when (result){
                    is Resource.Success -> {
                        onDeleteAction()
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

    fun getAllCoursesAndCalTotalAverage(semesterId: Int?){
        getAllCourses(semesterId)
        calTotalAverage(semesterId)
    }
// Se calcula el promedio individualmente para tomar en cuenta si semesterId es null
    private fun calTotalAverage(semesterId: Int?){
        viewModelScope.launch {
            getAverageFromSemesterUseCase(semesterId).collect{ result ->
                when (result){
                    is Resource.Success -> {
                        _totalAverage.doubleValue = result.data!!.getGrade()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error calTotalAverage: ${result.message}")
                    }
                }
            }
        }
    }

    private fun getAllCourses(semesterId: Int?) {
        viewModelScope.launch {
            getCoursesFromSemesterIdUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!
                        _isLoading.value = false
                    }
                    is Resource.Loading -> { _isLoading.value = true }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error getAllcourse: ${result.message}")
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