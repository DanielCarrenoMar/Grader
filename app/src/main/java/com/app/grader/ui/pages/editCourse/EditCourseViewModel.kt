package com.app.grader.ui.pages.editCourse

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.infrastructure.appConfig.AppConfigRepository
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.course.SaveCourseUseCase
import com.app.grader.domain.usecase.course.UpdateCourseUseCase
import com.app.grader.domain.usecase.typeGrade.GetAllTypeGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class EditCourseViewModel @Inject constructor(
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getAllTypeGradeUseCase: GetAllTypeGradeUseCase,
    private val appConfigRepository: AppConfigRepository,
    private val saveCourseUseCase: SaveCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
): ViewModel() {
    private val _typeGradeList = mutableStateOf<List<TypeGradeModel>>(emptyList())
    val typeGradeList = _typeGradeList

    private val _title = mutableStateOf("Sin Título")
    val title = _title
    private val _uc = mutableIntStateOf(1)
    val uc = _uc
    private val _selectedTypeGradeId = mutableIntStateOf(appConfigRepository.getDefaultTypeGradeId())
    val selectedTypeGradeId = _selectedTypeGradeId

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle
    private val _showUc = mutableStateOf("")
    val showUc = _showUc

    init {
        loadTypeGrades()
    }

    private fun loadTypeGrades() {
        viewModelScope.launch {
            getAllTypeGradeUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _typeGradeList.value = result.data ?: emptyList()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error getAllTypeGradeUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun getCourseFromId(courseId: Int) {
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseByIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val course = result.data!!
                        _title.value = course.title
                        _showTitle.value = course.title
                        _uc.intValue = course.uc
                        _showUc.value = course.uc.toString()
                        _selectedTypeGradeId.intValue = course.typeGradeId
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

    private fun saveCourse(courseModel:CourseModel, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            saveCourseUseCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        onComplete(result.data!!)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateCourse(courseModel:CourseModel, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            updateCourseUseCase(courseModel = courseModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        onComplete()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    fun setTitle(newTitle: String) {
        if (newTitle.isBlank()) {
            _title.value = "Sin Título"
            return
        }
        _title.value = newTitle
    }

    fun validCourse(){
        if (uc.intValue < 0) throw InvalidParameterException("El peso debe ser mayor que 0")
    }

    fun setSelectedTypeGradeId(typeGradeId: Int) {
        _selectedTypeGradeId.intValue = typeGradeId
    }

    fun updateOrCreateCourse(semesterId: Int, courseId: Int, onCreate: (Long) -> Unit = {}, onUpdate: () -> Unit = {}) {
        validCourse()
        val semesterIdOrNull = if (semesterId != -1) semesterId else null
        viewModelScope.launch {
            if (courseId == -1) {
                saveCourse(
                    CourseModel(
                        title = title.value,
                        uc = uc.intValue,
                        semesterId = semesterIdOrNull,
                        typeGradeId = selectedTypeGradeId.intValue,
                    ),
                    onComplete = onCreate
                )
            } else {
                updateCourse(
                    CourseModel(
                        title = title.value,
                        uc = uc.intValue,
                        typeGradeId = selectedTypeGradeId.intValue,
                        id = courseId
                    ),
                    onComplete = onUpdate
                )
            }
        }
    }
}