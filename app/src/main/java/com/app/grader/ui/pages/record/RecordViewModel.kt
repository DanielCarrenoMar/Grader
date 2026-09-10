package com.app.grader.ui.pages.record

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterLessThanUseCase
import com.app.grader.domain.usecase.semester.DeleteSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetAllSemestersUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetSizeFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetTotalSemesterStatisticsUseCase
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
    private val getTotalSemesterStatisticsUseCase: GetTotalSemesterStatisticsUseCase,
    private val transferSemesterToSemesterUseCase: TransferSemesterToSemesterUseCase,
) : ViewModel() {
    private val _semesters = mutableStateOf<List<SemesterModel>>(emptyList())
    val semesters = _semesters

    private val _currentSemester = mutableStateOf(SemesterModel.DEFAULT)
    val currentSemester = _currentSemester

    private val _totalAverage = mutableStateOf(GradeValue())
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

    private val _isDeleting = mutableStateOf(false)
    val isDeleting = _isDeleting

    private val _isTransferring = mutableStateOf(false)
    val isTransferring = _isTransferring

    fun transferSelfToActualSemester(semesterIdSender: Int){
        // Guard against duplicate transfer while a transfer is in flight.
        if (_isTransferring.value) return
        if (semesterIdSender == -1) return
        _isTransferring.value = true
        viewModelScope.launch {
            transferSemesterToSemesterUseCase(semesterIdSender, null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        deleteSemester(
                            semesterIdSender,
                            onDeleteAction = {
                                getCurrentSemester()
                                _isTransferring.value = false
                            },
                            onError = {
                                _isTransferring.value = false
                            },
                        )
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isTransferring.value = false
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
                ?: GradeValue()

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
            // Refresca la lista de semestres.
            getAllSemestersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _semesters.value = result.data!!
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error getAllSemestersUserCase: ${result.message}")
                    }
                }
            }
            // Calcula los totales (cursos, UC y promedio) desde el flujo del DAO.
            getTotalSemesterStatisticsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val statistics = result.data!!
                        _totalCourses.intValue = statistics.totalCourses
                        _totalWeight.intValue = statistics.totalWeight
                        _totalAverage.value = statistics.totalAverage
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordViewModel", "Error getTotalSemesterStatisticsUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun selectDeleteSemester(semesterModel: SemesterModel){
        _deleteSemester.value = semesterModel
    }

    fun deleteSemester(semesterId: Int, onDeleteAction: () -> Unit = {}, onError: () -> Unit = {}){
        // Separate flag for delete so transfer stays available.
        if (_isDeleting.value) return
        _isDeleting.value = true
        viewModelScope.launch {
            deleteSemesterByIdUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _isDeleting.value = false
                        getAllSemestersAndCalTotalAverage()
                        onDeleteAction()
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isDeleting.value = false
                        onError()
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