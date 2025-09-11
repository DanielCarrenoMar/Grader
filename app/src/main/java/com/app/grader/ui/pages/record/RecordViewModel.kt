package com.app.grader.ui.pages.record

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterLessThanUseCase
import com.app.grader.domain.usecase.semester.DeleteSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetAllSemestersUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetSizeFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetWeightFromSemesterUseCase
import com.app.grader.domain.usecase.semester.TransferSemesterToSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.security.InvalidParameterException
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val getAllSemestersUseCase: GetAllSemestersUseCase,
    private val deleteSemesterByIdUseCase: DeleteSemesterByIdUseCase,
    private val getGradesFromSemesterLessThanUseCase: GetGradesFromSemesterLessThanUseCase,
    private val getAverageFromSemesterUseCase: GetAverageFromSemesterUseCase,
    private val getSizeFromSemesterUseCase: GetSizeFromSemesterUseCase,
    private val getWeightFromSemesterUSeCase: GetWeightFromSemesterUseCase,
    private val transferSemesterToSemesterUseCase: TransferSemesterToSemesterUseCase,
    private val gradeFactory: GradeFactory,
) : ViewModel() {
    private val _semesters = mutableStateOf<List<SemesterModel>>(emptyList())
    val semesters = _semesters

    private val _currentSemester = mutableStateOf(SemesterModel.DEFAULT)
    val currentSemester = _currentSemester

    private val _totalAverage = mutableStateOf(gradeFactory.instGrade())
    val totalAverage = _totalAverage

    private val _totalWeight = mutableIntStateOf(0)
    val totalWeight = _totalWeight

    private val _totalCourses = mutableIntStateOf(0)
    val totalCourses = _totalCourses

    private val _grades = mutableStateOf<List<GradeModel>>(emptyList())
    val grades = _grades

    private val _deleteSemester = mutableStateOf(SemesterModel.DEFAULT)
    val deleteSemester = _deleteSemester

    private val _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    fun transferSelfToActualSemester(semesterIdSender: Int){
        if (semesterIdSender == -1) return
        viewModelScope.launch {
            transferSemesterToSemesterUseCase(semesterIdSender, null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        deleteSemester(semesterIdSender){
                            getCurrentSemester()
                        }
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error transferSelfToActualSemester: ${result.message}")
                    }
                }
            }
        }
    }

    fun validActualSemesterToTransfer() {
        if (_currentSemester.value.size == 0) throw InvalidParameterException("No hay cursos para transferir")
    }
    fun getAllGradesLessActualSemester(){
        viewModelScope.launch {
            getGradesFromSemesterLessThanUseCase(null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _grades.value = result.data!!
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error getAllGradesLessActualSemester: ${result.message}")
                    }
                }
            }
        }
    }

    fun getCurrentSemester() {
        _isLoading.value = true
        viewModelScope.launch {
            val average = getAverageFromSemesterUseCase(null)
                .firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: gradeFactory.instGrade()

            val size = getSizeFromSemesterUseCase(null)
                .firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: 0

            val weight = getWeightFromSemesterUSeCase(null)
                .firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: 0

            _currentSemester.value = SemesterModel(
                title = "Registro Actual",
                average = average,
                size = size,
                weight = weight
            )
            _isLoading.value = false
        }
    }

    fun getAllSemestersAndCalTotalAverage() {
        viewModelScope.launch {
            getAllSemestersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _semesters.value = result.data!!
                        if (_semesters.value.isEmpty()) {
                            _totalAverage.value = gradeFactory.instGrade()
                            return@collect
                        }

                        var sumAverage = 0.0
                        var sumSemesterWeight = 0
                        var sumCoursesLength = 0
                        _semesters.value.forEach { semester ->
                            if (semester.average.isBlank()) return@forEach
                            sumAverage += semester.average.getGrade() * semester.weight
                            sumSemesterWeight += semester.weight
                            sumCoursesLength += semester.size
                        }
                        if (sumSemesterWeight == 0) {
                            _totalAverage.value = gradeFactory.instGrade()
                            return@collect
                        }

                        _totalCourses.intValue = sumCoursesLength
                        _totalWeight.intValue = sumSemesterWeight
                        _totalAverage.value = gradeFactory.instGrade(sumAverage / sumSemesterWeight)
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error getAllSemestersUserCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun selectDeleteSemester(semesterModel: SemesterModel){
        _deleteSemester.value = semesterModel
    }

    fun deleteSemester(semesterId: Int, onDeleteAction: () -> Unit = {}){
        viewModelScope.launch {
            deleteSemesterByIdUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        getAllSemestersAndCalTotalAverage()
                        onDeleteAction()
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error deleteSemesterByIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun deleteSelectSemester(onDeleteAction: () -> Unit = {}) {
        if (_deleteSemester.value.id == -1) return
        deleteSemester(_deleteSemester.value.id, onDeleteAction)
    }
}