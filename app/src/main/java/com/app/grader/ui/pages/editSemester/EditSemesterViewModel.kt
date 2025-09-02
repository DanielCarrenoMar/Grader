package com.app.grader.ui.pages.editSemester

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.SaveSemesterUseCase
import com.app.grader.domain.usecase.semester.UpdateSemesterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditSemesterViewModel @Inject constructor(
    private val getSemesterByIdUseCase: GetSemesterByIdUseCase,
    private val saveSemesterUseCase: SaveSemesterUseCase,
    private val updateSemesterUseCase: UpdateSemesterUseCase
): ViewModel() {
    private val _title = mutableStateOf("Sin Título")
    val title = _title

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle

    fun getCourseFromId(semesteId: Int) {
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
                        Log.e("EditCourseViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveCourse(semesterModel: SemesterModel) {
        viewModelScope.launch {
            saveSemesterUseCase(semesterModel).collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateSemester(semesterModel: SemesterModel) {
        viewModelScope.launch {
            updateSemesterUseCase(semesterModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                    }
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Log.e("EditCourseViewModel", "Error saving semester: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateOrCreateCourse(semesterId: Int){
        viewModelScope.launch {
            if (semesterId == -1) {
                saveCourse(
                    SemesterModel(
                        title = title.value,
                    )
                )
            } else {
                updateSemester(
                    SemesterModel(
                        title = title.value,
                        id = semesterId
                    )
                )
            }
        }
    }
}