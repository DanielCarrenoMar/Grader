package com.app.grader.ui.editGrade

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.GetGradeFromIdUseCase
import com.app.grader.domain.usecase.GetGradesFromCourseUserCase
import com.app.grader.domain.usecase.SaveGradeUseCase
import com.app.grader.domain.usecase.UpdateGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase,
    private val getGradesFromCourseUserCase: GetGradesFromCourseUserCase,
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getAllCoursesUserCase: GetAllCoursesUserCase,
): ViewModel() {
    private val _title = mutableStateOf("Sin Titulo")
    val title = _title
    private val _description = mutableStateOf("Sin Descricción")
    val description = _description
    private val _grade = mutableStateOf(Grade(0.0))
    val grade = _grade
    private val _percentage = mutableStateOf(Percentage(100.0))
    val percentage = _percentage
    private val _defaultPercentage = mutableStateOf(Percentage(100.0))
    val defaultPercentage = _defaultPercentage

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle
    private val _showDescription = mutableStateOf("")
    val showDescription = _showDescription
    private val _showGrade = mutableStateOf("")
    val showGrade = _showGrade
    private val _showPercentage = mutableStateOf("")
    val showPercentage = _showPercentage

    private val _courseId = mutableIntStateOf(-1)
    val courseId = _courseId
    private val _showCourse = mutableStateOf(CourseModel.DEFAULT)
    val showCourse = _showCourse
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses

    fun setPercentage(percentage: String){
        _showPercentage.value = percentage
        val value = percentage.toDoubleOrNull()

        if (percentage.isBlank() || value == null || Percentage.check(value).not()) actDefaultPercentage()
        else _percentage.value.setPercentage(value)
    }
    fun setCourseId(courseId: Int){
        _courseId.intValue = courseId
        actDefaultPercentage()
    }

    fun actDefaultPercentage() {
        if (_courseId.intValue == -1) return
        viewModelScope.launch {
            getGradesFromCourseUserCase(_courseId.intValue).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grades = result.data!!
                        var totalPercentage = 0.0
                        grades.forEach { grade ->
                            totalPercentage += grade.percentage
                        }
                        _defaultPercentage.value.setPercentage(100.0 - totalPercentage)
                        _percentage.value.setPercentage(100.0 - totalPercentage)
                        _showPercentage.value = " "
                        _showPercentage.value = ""
                    }

                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }

                    is Resource.Error -> {
                        Log.e(
                            "EditGradeViewModel",
                            "Error getGradesFromCourseUserCase: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    fun getGradeFromId(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grade = result.data!!
                        _title.value = grade.title
                        _showTitle.value = grade.title
                        _description.value = grade.description
                        _showDescription.value = grade.description
                        _grade.value.setGrade(grade.grade)
                        _showGrade.value = grade.grade.toString()
                        _percentage.value.setPercentage(grade.percentage)
                        _showPercentage.value = grade.percentage.toString()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getGradeFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveGrade(gradeModel: GradeModel) {
        viewModelScope.launch {
            saveGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "saveGrade id: ${gradeModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving grade: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateGrade(gradeModel: GradeModel) {
        viewModelScope.launch {
            updateGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "updateGrade id: ${gradeModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    private fun checkInputs(): Boolean {
        return _title.value.isNotBlank() &&
                _description.value.isNotBlank() &&
                _grade.value.getGrade() != 0.0 &&
                _grade.value.getGrade().toInt() == _showGrade.value.toIntOrNull() &&
                _percentage.value.getPercentage() != 0.0 &&
                _percentage.value.getPercentage() == _showPercentage.value.toDoubleOrNull()
    }

    private fun syncInputs() {
        _showGrade.value = _grade.value.toString()
        _showPercentage.value = _percentage.value.toString()
    }

    fun updateOrCreateGrade(gradeId: Int): Boolean{
        if (_courseId.intValue == -1) return false
        if (checkInputs().not()) {
            syncInputs()
            return false
        }
        viewModelScope.launch {
            if (gradeId == -1) {
                saveGrade(
                    GradeModel(
                        courseId = _courseId.intValue,
                        title = _title.value,
                        description = _description.value,
                        grade = _grade.value.getGrade(),
                        percentage = _percentage.value.getPercentage(),
                    )
                )
            } else {
                updateGrade(
                    GradeModel(
                        courseId = _courseId.intValue,
                        title = _title.value,
                        description = _description.value,
                        grade = _grade.value.getGrade(),
                        percentage = _percentage.value.getPercentage(),
                        id = gradeId,
                    )
                )
            }
        }
        return true
    }

    fun loadCourseOptions() {
        viewModelScope.launch {
            getAllCoursesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!

                        if (courses.value.isNotEmpty()) {
                            showCourse.value = courses.value[0]
                            setCourseId(courses.value[0].id)
                        }
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

}