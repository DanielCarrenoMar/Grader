package com.app.grader.ui.pages.editGrade

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.types.averageGrade
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradeByIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromCourseUseCase
import com.app.grader.domain.usecase.grade.SaveGradeUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeUseCase
import com.app.grader.domain.usecase.review.LaunchInAppReviewIfValidUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.GetSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.SaveSubGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toDoubleOrNull

data class EditGradeUiState(
    val title: String = "Sin Título",
    val description: String = "Sin descripción",
    val showGrade: String = "",
    val showPercentage: String = "",
    val courseId: Int = -1,
    val showCourse: CourseModel = CourseModel.DEFAULT,
    val courses: List<CourseModel> = emptyList(),
    val showSubGrades: List<String> = emptyList(),
)

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeByIdUseCase: GetGradeByIdUseCase,
    private val getGradesFromCourseUseCase: GetGradesFromCourseUseCase,
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getSubGradesFromGradeUseCase: GetSubGradesFromGradeUseCase,
    private val saveSubGradeUseCase: SaveSubGradeUseCase,
    private val deleteAllSubGradesFromGradeUseCase: DeleteAllSubGradesFromGradeUseCase,
    private val launchInAppReviewIfValidUseCase: LaunchInAppReviewIfValidUseCase,
    private val gradeFactory: GradeFactory,
): ViewModel() {

    // --- Unified UI State ---
    private val _uiState = MutableStateFlow(EditGradeUiState())
    val uiState: StateFlow<EditGradeUiState> = _uiState.asStateFlow()

    // --- Domain objects for calculations (private, mutable) ---
    private val gradesCache = mutableListOf<GradeModel>()
    private val _grade = gradeFactory.instGrade()
    private val _percentage = Percentage(100.0)
    private val _defaultPercentage = Percentage(100.0)
    private val _savedPercentage = Percentage(0.0)
    private val _subGrades = mutableListOf<Grade>()

    // Public read-only accessors for domain objects read by the UI
    val grade: Grade get() = _grade
    val defaultPercentage: Percentage get() = _defaultPercentage

    // --- Setters ---

    fun setGrade(grade: String) {
        val value = grade.toDoubleOrNull()
        if (grade.isNotBlank() && value != null && _grade.check(value)) _grade.setValue(value)
        else _grade.setBlank()
        _uiState.update { it.copy(showGrade = grade) }
    }

    fun setPercentage(percentage: String) {
        val value = percentage.toDoubleOrNull()
        if (!percentage.isBlank() && value != null && Percentage.check(value)) {
            _percentage.setPercentage(value)
            _uiState.update { it.copy(showPercentage = percentage) }
        } else {
            actDefaultPercentage()
        }
    }

    fun setCourseId(courseId: Int) {
        if (_uiState.value.courseId == courseId) return
        _uiState.update { it.copy(courseId = courseId) }
        actDefaultPercentage()
    }

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun setShowCourse(course: CourseModel) {
        _uiState.update { it.copy(showCourse = course) }
    }

    // --- Percentage calculation ---

    fun actDefaultPercentage(courseId: Int = _uiState.value.courseId) {
        if (courseId == -1) return
        if (gradesCache.isNotEmpty()) {
            calDefaultPercentage(gradesCache)
        }

        viewModelScope.launch {
            getGradesFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grades = result.data!!
                        gradesCache.clear()
                        gradesCache.addAll(grades)
                        calDefaultPercentage(grades)
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e(
                            "EditGradeViewModel",
                            "Error getGradesFromCourseUserCase: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    private fun calDefaultPercentage(grades: List<GradeModel>) {
        var totalPercentage = 0.0
        grades.forEach { grade ->
            totalPercentage += grade.percentage.getPercentage()
        }
        _defaultPercentage.setPercentage(100.0 - totalPercentage + _savedPercentage.getPercentage())
        _uiState.update { it.copy(showPercentage = "") }
    }

    fun resetCacheGrade() {
        gradesCache.clear()
    }

    // --- SubGrades ---

    fun calGradeFromSubGrades() {
        if (_subGrades.isEmpty()) return
        _grade.setValue(_subGrades.averageGrade())
        _uiState.update { it.copy(showGrade = _grade.toString()) }
    }

    fun setSubGrade(index: Int, subGrade: String) {
        val updated = _uiState.value.showSubGrades.toMutableList()
        updated[index] = subGrade
        _uiState.update { it.copy(showSubGrades = updated) }

        val value = subGrade.toDoubleOrNull()
        if (subGrade.isNotBlank() && value != null && _grade.check(value)) _subGrades[index].setValue(value)
        else _subGrades[index].setBlank()
        calGradeFromSubGrades()
    }

    fun addSubGrade() {
        if (_subGrades.isEmpty()) {
            _subGrades.add(Grade(_grade))
            _uiState.update { it.copy(showSubGrades = it.showSubGrades + _grade.toString()) }
        } else {
            _subGrades.add(gradeFactory.instGrade())
            _uiState.update { it.copy(showSubGrades = it.showSubGrades + "") }
        }
        calGradeFromSubGrades()
    }

    fun removeSubGrade(index: Int) {
        _subGrades.removeAt(index)
        val updated = _uiState.value.showSubGrades.toMutableList()
        updated.removeAt(index)
        _uiState.update { it.copy(showSubGrades = updated) }
        calGradeFromSubGrades()
    }

    fun loadSubGradesFromGrade(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val subGrades: List<SubGradeModel> = result.data!!
                        val updated = _uiState.value.showSubGrades.toMutableList()
                        subGrades.forEach {
                            _subGrades.add(Grade(it.grade))
                            updated.add(it.grade.toString())
                        }
                        _uiState.update { it.copy(showSubGrades = updated) }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getSubGradesFromGradeUserCase: ${result.message}")
                    }
                }
            }
        }
    }

    // --- Save/Update ---

    private fun saveSubGrades(gradeId: Int) {
        if (gradeId == -1) return
        _subGrades.forEachIndexed { index, grade ->
            saveSubGrade(SubGradeModel(
                title = "SubGrade $index",
                grade = grade,
                gradeId = gradeId
            ))
        }
    }

    private fun saveSubGrade(subGrade: SubGradeModel) {
        viewModelScope.launch {
            saveSubGradeUseCase(subGrade).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "saveSubGrade id: ${result.data}")
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving subGrade: ${result.message}")
                    }
                }
            }
        }
    }

    private fun deleteSubGradesFromGrade(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            deleteAllSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "deleteSubGradesFromGrade id: $gradeId amount: ${result.data}")
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error deleting subGrades: ${result.message}")
                    }
                }
            }
        }
    }

    fun loadGradeFromId(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getGradeByIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grade = result.data!!
                        _grade.setValue(grade.grade)
                        _percentage.setPercentage(grade.percentage)
                        _savedPercentage.setPercentage(grade.percentage)
                        _uiState.update {
                            it.copy(
                                title = grade.title,
                                description = grade.description,
                                showGrade = grade.grade.toString(),
                                showPercentage = grade.percentage.toString(),
                            )
                        }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getGradeFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveGradeWithSubGrades(gradeModel: GradeModel) {
        viewModelScope.launch {
            saveGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) saveSubGrades(result.data.toInt())
                        Log.i("EditGradeViewModel", "saveGrade id: ${result.data}")
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving grade: ${result.message}")
                    }
                }
            }
        }
    }

    private fun updateGradeWithSubGrades(gradeModel: GradeModel) {
        viewModelScope.launch {
            updateGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        deleteSubGradesFromGrade(gradeModel.id)
                        saveSubGrades(gradeModel.id)
                        Log.i("EditGradeViewModel", "updateGrade id: ${gradeModel.id}")
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving course: ${result.message}")
                    }
                }
            }
        }
    }

    fun syncInvalidInputs(): Boolean {
        var result = true
        val state = _uiState.value

        val showGradeValue = state.showGrade.toDoubleOrNull()
        var correctedShowGrade = state.showGrade
        if (state.showGrade.isBlank() || showGradeValue == null) {
            _grade.setBlank()
        } else if (!_grade.check(showGradeValue)) {
            correctedShowGrade = _grade.toString()
            _grade.setValue(correctedShowGrade.toDoubleOrNull() ?: 0.0)
            result = false
        }

        val showPercentageValue = state.showPercentage.toDoubleOrNull()
        if (state.showPercentage.isBlank() || showPercentageValue == null) {
            _percentage.setPercentage(_defaultPercentage)
        } else if (_percentage.getPercentage() == 0.0 ||
            !Percentage.check(showPercentageValue) ||
            showPercentageValue > _defaultPercentage.getPercentage()
        ) {
            _percentage.setPercentage(_defaultPercentage)
            result = false
        }

        val correctedTitle = if (state.title.isBlank()) "Sin Titulo" else state.title
        val correctedDescription = if (state.description.isBlank()) "Sin descripción" else state.description

        _uiState.update {
            it.copy(
                title = correctedTitle,
                description = correctedDescription,
                showGrade = correctedShowGrade,
            )
        }

        return result
    }

    private fun saveOrCreateGrade(gradeId: Int) {
        viewModelScope.launch {
            val state = _uiState.value
            if (gradeId == -1) {
                saveGradeWithSubGrades(
                    GradeModel(
                        courseId = state.courseId,
                        title = state.title,
                        description = state.description,
                        grade = _grade,
                        percentage = _percentage,
                    )
                )
            } else {
                updateGradeWithSubGrades(
                    GradeModel(
                        courseId = state.courseId,
                        title = state.title,
                        description = state.description,
                        grade = _grade,
                        percentage = _percentage,
                        id = gradeId,
                    )
                )
            }
        }
    }

    fun submitGrade(gradeId: Int, activity: Activity?): Boolean {
        if (_uiState.value.courseId == -1) return false

        if (!syncInvalidInputs()) return false

        saveOrCreateGrade(gradeId)

        if (activity != null) {
            viewModelScope.launch {
                launchInAppReviewIfValidUseCase(activity).collect {}
            }
        }
        return true
    }

    private fun getCourseFromId(courseId: Int) {
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseByIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(showCourse = result.data!!) }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun loadCourseOptionsFromSemester(semesterId: Int, courseId: Int = _uiState.value.courseId) {
        val semesterIdOrNull = if (semesterId != -1) semesterId else null
        viewModelScope.launch {
            getCoursesFromSemesterUseCase(semesterIdOrNull).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val courses = result.data!!
                        _uiState.update { it.copy(courses = courses) }

                        if (courses.isNotEmpty()) {
                            if (courseId == -1) {
                                _uiState.update { it.copy(showCourse = courses[0]) }
                                setCourseId(courses[0].id)
                            } else {
                                getCourseFromId(courseId)
                            }
                        }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getCoursesFromSemesterUseCase: ${result.message}")
                    }
                }
            }
        }
    }
}
