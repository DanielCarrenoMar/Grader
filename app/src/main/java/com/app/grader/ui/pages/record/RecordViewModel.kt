package com.app.grader.ui.pages.record

import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.semester.DeleteSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetAllSemestersUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getAllSemestersUseCase: GetAllSemestersUseCase,
    private val deleteSemesterByIdUseCase: DeleteSemesterByIdUseCase
) : ViewModel() {
    private val _semesters = mutableStateOf<List<SemesterModel>>(emptyList())
    val semesters = _semesters

    private val _totalAverage = mutableDoubleStateOf(0.0)
    val totalAverage = _totalAverage

    private val _deleteSemesterId = mutableIntStateOf(-1)

    fun getAllSemestersAndCalTotalAverage() {
        viewModelScope.launch {
            getAllSemestersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _semesters.value = result.data!!
                        if (_semesters.value.isEmpty()) {
                            _totalAverage.doubleValue = 0.0
                            return@collect
                        }

                        var sumAverage = 0.0
                        _semesters.value.forEach { semester ->
                            sumAverage += semester.average.getGrade()
                        }
                        _totalAverage.doubleValue = sumAverage / _semesters.value.size
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error getAllSemestersUserCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun selectDeleteSemester(semesterId: Int){
        viewModelScope.launch {
            _deleteSemesterId.intValue = semesterId
        }
    }

    fun deleteSelectSemester(onDeleteAction: () -> Unit = {}) {
        if (_deleteSemesterId.intValue == -1) return
        viewModelScope.launch {
            deleteSemesterByIdUseCase(_deleteSemesterId.intValue).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        onDeleteAction()
                        getAllSemestersAndCalTotalAverage()
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error deleteSemesterByIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }
}