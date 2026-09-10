package com.app.grader.ui.pages.initialConfig

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.usecase.course.UpdateTypeGradeForAllCoursesUseCase
import com.app.grader.domain.usecase.typeGrade.ChangeAllTypeGradeUseCase
import com.app.grader.domain.usecase.typeGrade.ChangeTypeGradeUseCase
import com.app.grader.domain.usecase.typeGrade.GetAllTypeGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialConfigViewModel @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val getAllTypeGradeUseCase: GetAllTypeGradeUseCase,
    private val changeTypeGradeUseCase: ChangeTypeGradeUseCase,
    private val changeAllTypeGradeUseCase: ChangeAllTypeGradeUseCase,
    private val updateTypeGradeForAllCoursesUseCase: UpdateTypeGradeForAllCoursesUseCase,
) : ViewModel() {

    private val _typeGradeList = mutableStateOf<List<TypeGradeModel>>(emptyList())
    val typeGradeList = _typeGradeList

    private val _selectedTypeGradeId = mutableIntStateOf(appConfigRepository.getDefaultTypeGradeId())
    val selectedTypeGradeId = _selectedTypeGradeId

    private val _minToPassInput = mutableStateOf("")
    val minToPassInput = _minToPassInput
    private val _minToPassError = mutableStateOf(false)
    val minToPassError = _minToPassError
    private var minToPass: Double? = null

    private val _isDirectPercentage = mutableStateOf(false)
    val isDirectPercentage = _isDirectPercentage

    private val _isRoundFinalCourseAverage = mutableStateOf(appConfigRepository.isRoundFinalCourseAverage())
    val isRoundFinalCourseAverage = _isRoundFinalCourseAverage

    init {
        loadTypeGrades()
    }

    private fun loadTypeGrades() {
        viewModelScope.launch {
            getAllTypeGradeUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _typeGradeList.value = result.data ?: emptyList()
                        if (_typeGradeList.value.isEmpty()) return@collect
                        val selectedTypeGrade = _typeGradeList.value.firstOrNull { it.id == _selectedTypeGradeId.intValue }
                        _isDirectPercentage.value = selectedTypeGrade?.isDirectPercentage == true
                        _minToPassInput.value = selectedTypeGrade?.minToPass?.let(GradeValue::formatText) ?: ""
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("InitialConfigViewModel", "Error loadTypeGrades: ${result.message}")
                    }
                }
            }
        }
    }

    fun setSelectedTypeGradeId(typeGradeId: Int) {
        _selectedTypeGradeId.intValue = typeGradeId
        appConfigRepository.setDefaultTypeGradeId(typeGradeId)
        viewModelScope.launch {
            updateTypeGradeForAllCoursesUseCase(typeGradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("InitialConfigViewModel", "Error updateTypeGradeForAllCoursesUseCase: ${result.message}")
                    }
                }
            }
        }
        loadTypeGrades()
    }

    fun setMinToPass(value: String) {
        _minToPassInput.value = value
        val parsed = value.toDoubleOrNull()
        val selectedId = _selectedTypeGradeId.intValue
        val selectedTypeGrade = _typeGradeList.value.firstOrNull { it.id == selectedId } ?: return
        val max = selectedTypeGrade.max.toDouble()
        val isValid = value.isEmpty() || (parsed != null && parsed in 0.0..max)
        _minToPassError.value = !isValid
        if (!isValid) return
        minToPass = parsed
        viewModelScope.launch {
            changeTypeGradeUseCase(selectedId, minToPass).collect { result ->
                if (result is Resource.Error) {
                    Log.e("InitialConfigViewModel", "Error changeTypeGradeUseCase: ${result.message}")
                }
            }
        }
    }

    fun setDirectPercentage(isDirectPercentage: Boolean) {
        _isDirectPercentage.value = isDirectPercentage
        viewModelScope.launch {
            changeAllTypeGradeUseCase(isDirectPercentage).collect { result ->
                when (result) {
                    is Resource.Success -> loadTypeGrades()
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("InitialConfigViewModel", "Error changeAllTypeGradeUseCase: ${result.message}")
                        loadTypeGrades()
                    }
                }
            }
        }
    }

    fun setRoundFinalCourseAverage(isRoundFinalCourseAverage: Boolean) {
        _isRoundFinalCourseAverage.value = isRoundFinalCourseAverage
        appConfigRepository.setRoundFinalCourseAverage(isRoundFinalCourseAverage)
    }
}
