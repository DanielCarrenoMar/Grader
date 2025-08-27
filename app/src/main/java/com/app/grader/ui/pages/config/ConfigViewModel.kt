package com.app.grader.ui.pages.config

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.domain.model.Resource
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.domain.usecase.course.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.grade.DeleteAllGradesUseCase
import com.app.grader.domain.usecase.semester.DeleteAllSemestersUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesUseCase
import com.patrykandpatrick.vico.compose.common.shader.component
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel  @Inject constructor(
    private val deleteAllGradesUseCase: DeleteAllGradesUseCase,
    private val deleteAllCoursesUseCase: DeleteAllCoursesUseCase,
    private val deleteAllSubGradesUseCase: DeleteAllSubGradesUseCase,
    private val deleteAllSemestersUseCase: DeleteAllSemestersUseCase,
    private val appConfig: AppConfig
): ViewModel() {
    private val _isDarkMode = mutableStateOf(appConfig.isDarkMode())
    val isDarkMode = _isDarkMode
    private val _isRoundFinalCourseAverage = mutableStateOf(appConfig.isDarkMode())
    val isRoundFinalCourseAverage = _isRoundFinalCourseAverage

    private val _typeGrade = mutableStateOf(appConfig.getTypeGrade())
    val typeGrade = _typeGrade

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
        _isDarkMode.value = appConfig.isDarkMode()
        _isRoundFinalCourseAverage.value = appConfig.isRoundFinalCourseAverage()
        _typeGrade.value = appConfig.getTypeGrade()
    }

    fun setDarkMode(isDarkMode: Boolean) {
        _isDarkMode.value = isDarkMode
        appConfig.setDarkMode(isDarkMode)
    }
    fun setRoundFinalCourseAverage(isRoundFinalCourseAverage: Boolean) {
        _isRoundFinalCourseAverage.value = isRoundFinalCourseAverage
        appConfig.setRoundFinalCourseAverage(isRoundFinalCourseAverage)
    }
    fun setTypeGrade(typeGrade: TypeGrade) {
        _typeGrade.value = typeGrade
        appConfig.setTypeGrade(typeGrade)
    }
    fun deleteAll(){
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
            if (finished){
                Log.i("ConfigViewModel", "All data deleted successfully")
            }
        }
    }
}