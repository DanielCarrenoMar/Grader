package com.app.grader.ui.editGrade

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.usecase.GetGradeFromIdUseCase
import com.app.grader.domain.usecase.SaveGradeUseCase
import com.app.grader.domain.usecase.UpdateGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase,
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
): ViewModel() {
    private val _title = mutableStateOf("Sin Titulo")
    val title = _title
    private val _description = mutableStateOf("Sin Descricción")
    val description = _description
    private val _grade = mutableStateOf(Grade(0.0))
    val grade = _grade
    private val _percentage = mutableStateOf(Percentage(100.0))
    val percentage = _percentage

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle
    private val _showDescription = mutableStateOf("")
    val showDescription = _showDescription
    private val _showGrade = mutableStateOf("")
    val showGrade = _showGrade
    private val _showPercentage = mutableStateOf("")
    val showPercentage = _showPercentage

    fun getGradeFromId(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grade = result.data!!
                        _title.value = grade.title
                        _showTitle.value = grade.title
                        _description.value = grade.description
                        _showDescription.value = grade.description
                        _grade.value.setGrade(grade.grade)
                        _showGrade.value = grade.grade.toString()
                        _percentage.value.setPercentage(grade.percentage)
                        _showPercentage.value = grade.percentage.toString()
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getGradeFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveGrade(gradeModel: GradeModel) {
        viewModelScope.launch {
            saveGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "saveGrade id: ${gradeModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving grade: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateGrade(gradeModel: GradeModel) {
        viewModelScope.launch {
            updateGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "updateGrade id: ${gradeModel.id}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    fun updateOrCreateGrade(gradeId: Int, courseId: Int){
        viewModelScope.launch {
            if (gradeId == -1) {
                saveGrade(
                    GradeModel(
                        courseId = courseId,
                        title = title.value,
                        description = description.value,
                        grade = grade.value.getGrade(),
                        percentage = percentage.value.getPercentage(),
                    )
                )
            } else {
                updateGrade(
                    GradeModel(
                        courseId = courseId,
                        title = title.value,
                        description = description.value,
                        grade = grade.value.getGrade(),
                        percentage = percentage.value.getPercentage(),
                        id = gradeId,
                    )
                )
            }
        }
    }

}