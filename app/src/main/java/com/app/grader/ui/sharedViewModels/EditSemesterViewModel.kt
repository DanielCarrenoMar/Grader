package com.app.grader.ui.sharedViewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.SaveSemesterUseCase
import com.app.grader.domain.usecase.semester.UpdateSemesterUseCase
import kotlinx.coroutines.launch
import java.security.InvalidParameterException

open class EditSemesterViewModel(
    protected val getSemesterByIdUseCase: GetSemesterByIdUseCase,
    protected val saveSemesterUseCase: SaveSemesterUseCase,
    protected val updateSemesterUseCase: UpdateSemesterUseCase
): ViewModel() {
    private val _title = mutableStateOf("Sin Título")
    val title = _title
    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle

    private val _isSaving = mutableStateOf(false)
    val isSaving = _isSaving

    fun setTitle(newTitle: String) {
        if (newTitle.isBlank()) {
            _title.value = "Sin Título"
            return
        }
        _title.value = newTitle
    }

    fun getSemesterFromId(semesteId: Int) {
        if (semesteId == -1) return
        viewModelScope.launch {
            getSemesterByIdUseCase(semesteId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val course = result.data!!
                        _title.value = course.title
                        _showTitle.value = course.title
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("TransferSemesterViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    protected fun saveSemester(
        semesterModel: SemesterModel,
        onComplete: (Long) -> Unit = {},
        onError: () -> Unit = {},
    ) {
        viewModelScope.launch {
            saveSemesterUseCase(semesterModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Success path: keep isSaving true (sticky) so the button
                        // stays disabled. Navigation always leaves the page.
                        onComplete(result.data!!)
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isSaving.value = false
                        onError()
                        Log.e("TransferSemesterViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    protected fun updateSemester(
        semesterModel: SemesterModel,
        onComplete: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        viewModelScope.launch {
            updateSemesterUseCase(semesterModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Success path: keep isSaving true (sticky) so the button
                        // stays disabled. Navigation always leaves the page.
                        onComplete()
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        _isSaving.value = false
                        onError()
                        Log.e("TransferSemesterViewModel", "Error saving semester: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateOrCreateSemester(semesterId: Int, onCreate: (Long) -> Unit = {}, onUpdate: () -> Unit = {}) {
        // Guard against duplicate save while outer or inner save is in flight.
        if (_isSaving.value) return
        _isSaving.value = true
        viewModelScope.launch {
            try {
                if (semesterId == -1) {
                    saveSemester(
                        SemesterModel(
                            title = title.value,
                        ),
                        onComplete = onCreate,
                        onError = {},
                    )
                } else {
                    updateSemester(
                        SemesterModel(
                            title = title.value,
                            id = semesterId
                        ),
                        onComplete = onUpdate,
                        onError = {},
                    )
                }
            } catch (e: Exception) {
                _isSaving.value = false
                throw e
            }
        }
    }
}