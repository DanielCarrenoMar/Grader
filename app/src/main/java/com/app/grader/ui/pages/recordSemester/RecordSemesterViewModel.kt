package com.app.grader.ui.pages.recordSemester

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.usecase.course.DeleteCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterUseCase
import com.app.grader.domain.usecase.semester.DeleteSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.GetSizeFromSemesterUseCase
import com.app.grader.domain.usecase.semester.TransferSemesterToSemesterUseCase
import com.app.grader.ui.sharedViewModels.SemesterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordSemesterViewModel @Inject constructor(
    getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    deleteCourseByIdUseCase: DeleteCourseByIdUseCase,
    getGradesFromSemesterUseCase: GetGradesFromSemesterUseCase,
    getAverageFromSemesterUseCase: GetAverageFromSemesterUseCase,
    gradeFactory: GradeFactory,
    private val deleteSemesterByIdUseCase: DeleteSemesterByIdUseCase,
    private val getSemesterByIdUseCase: GetSemesterByIdUseCase,
    private val transferSemesterToSemesterUseCase: TransferSemesterToSemesterUseCase,
    private val getSizeOfSemestersUseCase: GetSizeFromSemesterUseCase
) : SemesterViewModel(
    getCoursesFromSemesterUseCase,
    deleteCourseByIdUseCase,
    getGradesFromSemesterUseCase,
    getAverageFromSemesterUseCase,
    gradeFactory
) {
    private val _semester = mutableStateOf(SemesterModel.DEFAULT)
    val semester = _semester

    fun getSemester(semesterId: Int) {
        viewModelScope.launch {
            getSemesterByIdUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _semester.value = result.data!!
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e(
                            "RecordSemesterViewModel",
                            "Error getSemesterTitle: ${result.message}"
                        )
                    }
                }
            }
        }
    }
    fun deleteSelf(navigateTo: () -> Unit) {
        if (_semester.value.id == -1) return
        viewModelScope.launch {
            deleteSemesterByIdUseCase(_semester.value.id).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        navigateTo()
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordSemesterViewModel", "Error deleteSelf: ${result.message}")
                    }
                }
            }
        }
    }

    fun transferSelfToActualSemester(navigateTo: () -> Unit){
        if (_semester.value.id == -1) return
        viewModelScope.launch {
            transferSemesterToSemesterUseCase(_semester.value.id, null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        deleteSelf(navigateTo)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordSemesterViewModel", "Error transferSelfToActualSemester: ${result.message}")
                    }
                }
            }
        }
    }

    fun onGetSizeOfActualSemester(onComplete: (Int) -> Unit){
        viewModelScope.launch {
            getSizeOfSemestersUseCase(null).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        onComplete(result.data!!)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("RecordSemesterViewModel", "Error getSizeOfActualSemester: ${result.message}")
                    }
                }
            }
        }
    }
}