package com.app.grader.ui.pages.course

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.usecase.course.GetAverageFromCourseIdUseCase
import com.app.grader.domain.usecase.grade.DeleteGradeFromIdUseCase
import com.app.grader.domain.usecase.course.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradeFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel  @Inject constructor(
    private val getGradesFromCourseUseCase: GetGradesFromCourseUseCase,
    private val getCourseFromIdUseCase: GetCourseFromIdUseCase,
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase,
    private val deleteGradeFromIdUseCase: DeleteGradeFromIdUseCase,
    private val getAverageFromCourseIdUseCase: GetAverageFromCourseIdUseCase
): ViewModel() {
    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades
    private val _accumulatePoints = mutableStateOf(Grade(0.0))
    val accumulatePoints = _accumulatePoints
    private val _pedingPoints = mutableStateOf(Grade(0.0))
    val pedingPoints = _pedingPoints
    private val _totalPercentaje = mutableStateOf(Percentage(0.0))
    val totalPercentaje = _totalPercentaje

    private val _showGrade = mutableStateOf(GradeModel.DEFAULT)
    val showGrade = _showGrade
    private val _course = mutableStateOf(
        CourseModel.DEFAULT
    )
    val course = _course

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

    fun calPoints(courseId: Int){
        viewModelScope.launch {
            getGradesFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grades = result.data!!
                        var accumulatePointsTemp = 0.0
                        var totalPercentage = 0.0
                        grades.forEach { grade ->
                            if (grade.grade.isNotBlank()) {
                                accumulatePointsTemp += (grade.percentage.getPercentage() / 100) * grade.grade.getGrade()
                                totalPercentage += grade.percentage.getPercentage()
                            }
                        }
                        _totalPercentaje.value = Percentage(totalPercentage)
                        _accumulatePoints.value = Grade(accumulatePointsTemp)
                        _pedingPoints.value = Grade((100 - totalPercentage) / 100 * 20)
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

    fun getCourseFromId(courseId: Int) {
        viewModelScope.launch {
            getCourseFromIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) _course.value = result.data
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
            getGradesFromCourseUseCase(courseId).collect { result ->
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

    fun calAverageFromCourseId(courseId: Int) {
        viewModelScope.launch {
            getAverageFromCourseIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val average = result.data!!
                        _course.value = _course.value.copy(
                            average = average
                        )
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("CourseViewModel", "Error getAverageFromCourseId: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteGradeFromId(gradeId: Int){
        viewModelScope.launch {
            deleteGradeFromIdUseCase(gradeId).collect { result ->
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