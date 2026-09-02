package com.app.grader.ui.pages.config

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.infrastructure.appConfig.AppConfigRepository
import com.app.grader.domain.model.Resource
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.domain.types.ThemeType
import com.app.grader.domain.usecase.course.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.grade.DeleteAllGradesUseCase
import com.app.grader.domain.usecase.semester.DeleteAllSemestersUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesUseCase
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
    private val appConfigRepository: AppConfigRepository
): ViewModel() {
    private val _typeTheme = mutableStateOf(appConfigRepository.getTypeTheme())
    val typeTheme = _typeTheme
    private val _isRoundFinalCourseAverage = mutableStateOf(appConfigRepository.isRoundFinalCourseAverage())
    val isRoundFinalCourseAverage = _isRoundFinalCourseAverage

    private val _typeGrade = mutableStateOf(appConfigRepository.getTypeGrade())
    val typeGrade = _typeGrade

    private val _launchCount = mutableIntStateOf(appConfigRepository.getLaunchCount())
    val launchCount = _launchCount

    private val _reviewAskedCount = mutableIntStateOf(appConfigRepository.getReviewAskedCount())
    val reviewAskedCount = _reviewAskedCount

    private val _lastReviewAskedTimeDays = mutableLongStateOf((System.currentTimeMillis() - appConfigRepository.getLastReviewAskedTime()) / (1000 * 60 * 60 * 24))
    val lastReviewAskedTimeDays = _lastReviewAskedTimeDays

    private val _reviewCompleted = mutableStateOf(appConfigRepository.isReviewCompleted())
    val reviewCompleted = _reviewCompleted

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
        _typeGrade.value = appConfigRepository.getTypeGrade()
        _launchCount.intValue = appConfigRepository.getLaunchCount()
        _reviewAskedCount.intValue = appConfigRepository.getReviewAskedCount()
        _lastReviewAskedTimeDays.longValue = (System.currentTimeMillis() - appConfigRepository.getLastReviewAskedTime()) / (1000 * 60 * 60 * 24)
        _reviewCompleted.value = appConfigRepository.isReviewCompleted()
    }

    fun setTypeTheme(themeType: ThemeType) {
        _typeTheme.value = themeType
        appConfigRepository.setTypeTheme(themeType)
    }
    fun setRoundFinalCourseAverage(isRoundFinalCourseAverage: Boolean) {
        _isRoundFinalCourseAverage.value = isRoundFinalCourseAverage
        appConfigRepository.setRoundFinalCourseAverage(isRoundFinalCourseAverage)
    }
    fun setTypeGrade(typeGrade: TypeGrade) {
        _typeGrade.value = typeGrade
        appConfigRepository.setTypeGrade(typeGrade)
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