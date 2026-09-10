package com.app.grader.ui.pages.config

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.app.grader.domain.types.GradeValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.types.ThemeType
import com.app.grader.domain.usecase.course.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.course.UpdateTypeGradeForAllCoursesUseCase
import com.app.grader.domain.usecase.grade.DeleteAllGradesUseCase
import com.app.grader.domain.usecase.semester.DeleteAllSemestersUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesUseCase
import com.app.grader.domain.usecase.typeGrade.ChangeAllTypeGradeUseCase
import com.app.grader.domain.usecase.typeGrade.ChangeTypeGradeUseCase
import com.app.grader.domain.usecase.typeGrade.GetAllTypeGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel  @Inject constructor(
    private val deleteAllGradesUseCase: DeleteAllGradesUseCase,
    private val deleteAllCoursesUseCase: DeleteAllCoursesUseCase,
    private val deleteAllSubGradesUseCase: DeleteAllSubGradesUseCase,
    private val deleteAllSemestersUseCase: DeleteAllSemestersUseCase,
    private val getAllTypeGradeUseCase: GetAllTypeGradeUseCase,
    private val changeTypeGradeUseCase: ChangeTypeGradeUseCase,
    private val changeAllTypeGradeUseCase: ChangeAllTypeGradeUseCase,
    private val appConfigRepository: AppConfigRepository,
    private val updateTypeGradeForAllCoursesUseCase: UpdateTypeGradeForAllCoursesUseCase
): ViewModel() {
    private val _typeTheme = mutableStateOf(appConfigRepository.getTypeTheme())
    val typeTheme = _typeTheme
    private val _isRoundFinalCourseAverage = mutableStateOf(appConfigRepository.isRoundFinalCourseAverage())
    val isRoundFinalCourseAverage = _isRoundFinalCourseAverage

    private val _typeGradeList = mutableStateOf<List<TypeGradeModel>>(emptyList())
    val typeGradeList = _typeGradeList

    private val _isDirectPercentage = mutableStateOf(false)
    val isDirectPercentage = _isDirectPercentage

    private val _minToPassInput = mutableStateOf("")
    val minToPassInput = _minToPassInput
    private val _minToPassError = mutableStateOf(false)
    val minToPassError = _minToPassError
    private var minToPass: Double? = null

    private val _selectedTypeGradeId = mutableIntStateOf(appConfigRepository.getDefaultTypeGradeId())
    val selectedTypeGradeId = _selectedTypeGradeId

    private val _isDeletingAll = mutableStateOf(false)
    val isDeletingAll = _isDeletingAll

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
                        Log.e("ConfigViewModel", "Error loadTypeGrades: ${result.message}")
                    }
                }
            }
        }
    }

    fun restartApp(context: Context) {
        viewModelScope.launch {
            delay(1000L)
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent?.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }
    fun updateConfiguration() {
        _typeTheme.value = appConfigRepository.getTypeTheme()
        _isRoundFinalCourseAverage.value = appConfigRepository.isRoundFinalCourseAverage()
        _selectedTypeGradeId.intValue = appConfigRepository.getDefaultTypeGradeId()
    }

    fun setTypeTheme(themeType: ThemeType) {
        _typeTheme.value = themeType
        appConfigRepository.setTypeTheme(themeType)
    }
    fun setRoundFinalCourseAverage(isRoundFinalCourseAverage: Boolean) {
        _isRoundFinalCourseAverage.value = isRoundFinalCourseAverage
        appConfigRepository.setRoundFinalCourseAverage(isRoundFinalCourseAverage)
    }
    fun resetLaunchCount() {
        appConfigRepository.setLaunchCount(0)
    }

    fun setDirectPercentage(isDirectPercentage: Boolean) {
        _isDirectPercentage.value = isDirectPercentage
        viewModelScope.launch {
            changeAllTypeGradeUseCase(isDirectPercentage).collect { result ->
                when (result) {
                    is Resource.Success -> loadTypeGrades()
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("ConfigViewModel", "Error changeTypeGradeUseCase: ${result.message}")
                        loadTypeGrades()
                    }
                }
            }
        }
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
                    Log.e("ConfigViewModel", "Error changeTypeGradeUseCase: ${result.message}")
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
                        Log.e("ConfigViewModel", "Error updateTypeGradeForAllCoursesUseCase: ${result.message}")
                    }
                }
            }
        }
        loadTypeGrades()
    }
    fun deleteAll(onComplete: () -> Unit = {}){
        // Guard against duplicate delete-all while a delete is in flight.
        if (_isDeletingAll.value) return
        _isDeletingAll.value = true
        viewModelScope.launch {
            var finished = true
            deleteAllSemestersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllSemestersUseCase: ${result.message}")
                    }
                }
            }
            deleteAllCoursesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllCoursesUseCase: ${result.message}")
                    }
                }
            }
            deleteAllGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllGradesUseCase: ${result.message}")
                    }
                }
            }
            deleteAllSubGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllSubGradesUseCase: ${result.message}")
                    }
                }
            }
            _isDeletingAll.value = false
            if (finished){
                Log.i("ConfigViewModel", "All data deleted successfully")
                onComplete()
            }
        }
    }
}
