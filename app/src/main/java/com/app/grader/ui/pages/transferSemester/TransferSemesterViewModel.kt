package com.app.grader.ui.pages.transferSemester

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.SaveSemesterUseCase
import com.app.grader.domain.usecase.semester.TransferSemesterToSemesterUseCase
import com.app.grader.domain.usecase.semester.UpdateSemesterUseCase
import com.app.grader.ui.sharedViewModels.EditSemesterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferSemesterViewModel @Inject constructor(
    getSemesterByIdUseCase: GetSemesterByIdUseCase,
    saveSemesterUseCase: SaveSemesterUseCase,
    updateSemesterUseCase: UpdateSemesterUseCase,
    private val transferSemesterToSemesterUseCase: TransferSemesterToSemesterUseCase,
    private val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    val appConfigRepository: AppConfigRepository,
) : EditSemesterViewModel(
    getSemesterByIdUseCase,
    saveSemesterUseCase,
    updateSemesterUseCase
) {
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses

    private val _isTransferring = mutableStateOf(false)
    val isTransferring = _isTransferring

    fun getCoursesFromSemester(semesterId: Int?) {
        viewModelScope.launch {
            getCoursesFromSemesterUseCase(semesterId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("TransferSemesterViewModel", "Error getCoursesFromSemester: ${result.message}")
                    }
                }
            }
        }
    }

    fun transferCoursesToNewSemester(onComplete: () -> Unit = {}) {
        // Guard against duplicate transfer while save or transfer is in flight.
        if (_isTransferring.value || isSaving.value) return
        _isTransferring.value = true
        saveSemester(
            SemesterModel(
                title = title.value
            ),
            onComplete = { newSemesterId ->
                viewModelScope.launch {
                    transferSemesterToSemesterUseCase(null, newSemesterId.toInt()).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                // Success path: keep isTransferring true (sticky) so
                                // the button stays disabled. Navigation leaves the page.
                                Log.d("TransferSemesterViewModel", "Courses transferred successfully")
                                onComplete()
                            }
                            is Resource.Loading -> {}
                            is Resource.Error -> {
                                _isTransferring.value = false
                                Log.e("TransferSemesterViewModel", "Error transferring courses: ${result.message}")
                            }
                        }
                    }
                }
            },
            onError = {
                _isTransferring.value = false
            },
        )
    }
}