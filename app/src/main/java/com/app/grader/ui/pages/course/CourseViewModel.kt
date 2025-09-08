package com.app.grader.ui.pages.course

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.usecase.course.DeleteCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetAverageFromCourseUseCase
import com.app.grader.domain.usecase.grade.DeleteGradeByIdUseCase
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.grade.GetGradeByIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromCourseUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel  @Inject constructor(
    private val getGradesFromCourseUseCase: GetGradesFromCourseUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getGradeByIdUseCase: GetGradeByIdUseCase,
    private val deleteGradeByIdUseCase: DeleteGradeByIdUseCase,
    private val getAverageFromCourseUseCase: GetAverageFromCourseUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val deleteCourseByIdUseCase: DeleteCourseByIdUseCase,
    private val gradeFactory: GradeFactory
): ViewModel() {
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades
    private val _accumulatePoints = mutableStateOf(Grade(0.0,0.0,0))
    val accumulatePoints = _accumulatePoints
    private val _pedingPoints = mutableStateOf(Grade(0.0,0.0,0))
    val pedingPoints = _pedingPoints
    private val _totalPercentaje = mutableStateOf(Percentage(0.0))
    val totalPercentaje = _totalPercentaje

    private val _showGrade = mutableStateOf(GradeModel.DEFAULT)
    val showGrade = _showGrade
    private val _course = mutableStateOf(
        CourseModel.DEFAULT
    )
    val course = _course

    private val _isEditingGrade = mutableStateOf(false)
    val isEditingGrade = _isEditingGrade

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    fun deleteSelf(navigateTo: () -> Unit) {
        if (_course.value.id == -1) return
        viewModelScope.launch {
            deleteCourseByIdUseCase(_course.value.id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        navigateTo()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error deleteSelf: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateGrade(grade: GradeModel) {
        viewModelScope.launch {
            updateGradeUseCase(grade).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        calPoints(_course.value.id)
                        calAverageFromCourseId(_course.value.id)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error updateGrade: ${result.message}")
                    }
                }
            }
        }
    }

    fun setShowGrade(gradeId: Int){
        viewModelScope.launch {
            getGradeByIdUseCase(gradeId).collect { result ->
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

    fun calPoints(courseId: Int){
        viewModelScope.launch {
            getGradesFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grades = result.data!!
                        var accumulatePointsTemp = 0.0
                        var totalPercentage = 0.0
                        grades.forEach { grade ->
                            totalPercentage += grade.percentage.getPercentage()
                            if (grade.grade.isNotBlank()) {
                                accumulatePointsTemp += (grade.percentage.getPercentage() / 100) * grade.grade.getGrade()
                            }
                        }
                        _totalPercentaje.value = Percentage(totalPercentage)
                        _accumulatePoints.value = gradeFactory.instGrade(accumulatePointsTemp)
                        _pedingPoints.value = gradeFactory.instGradeFromPercentage(100 - totalPercentage)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun getCourseFromId(courseId: Int) {
        viewModelScope.launch {
            getCourseByIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _course.value = result.data!!
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun getGradesFromCourse(courseId: Int) {
        viewModelScope.launch {
            getGradesFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _grades.value = result.data!!
                        _isLoading.value = false
                    }
                    is Resource.Loading -> { _isLoading.value = true }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getGradesFromCourse: ${result.message}")
                    }
                }
            }
        }
    }

    fun calAverageFromCourseId(courseId: Int) {
        viewModelScope.launch {
            getAverageFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val average = result.data!!
                        _course.value = _course.value.copy(
                            average = average
                        )
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getAverageFromCourseId: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteGradeFromId(gradeId: Int){
        viewModelScope.launch {
            deleteGradeByIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getGradesFromCourse(_course.value.id)
                        calPoints(_course.value.id)
                        calAverageFromCourseId(_course.value.id)
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
}