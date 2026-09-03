package com.app.grader.ui.pages.editGrade

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeDetailModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.types.averageGrade
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCourseTotalGradesRemainingPercentageUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradeByIdUseCase
import com.app.grader.domain.usecase.grade.SaveGradeUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeUseCase
import com.app.grader.domain.usecase.review.LaunchInAppReviewIfValidUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.GetSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.SaveSubGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toDoubleOrNull

data class EditGradeUiState(
    val title: String = "Sin Título",
    val description: String = "Sin descripción",
    val gradeValue: String = "",
    val percentage: String = "",
    val defaultPercentage: Double = 100.0,
    val courseId: Int = -1,
    val course: CourseModel = CourseModel.DEFAULT,
    val courses: List<CourseModel> = emptyList(),
    val subGrades: List<String> = emptyList(),
)

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeByIdUseCase: GetGradeByIdUseCase,
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getCourseTotalGradesRemainingPercentageUseCase: GetCourseTotalGradesRemainingPercentageUseCase,
    private val getSubGradesFromGradeUseCase: GetSubGradesFromGradeUseCase,
    private val saveSubGradeUseCase: SaveSubGradeUseCase,
    private val deleteAllSubGradesFromGradeUseCase: DeleteAllSubGradesFromGradeUseCase,
    private val launchInAppReviewIfValidUseCase: LaunchInAppReviewIfValidUseCase,
    private val gradeFactory: GradeFactory,
): ViewModel() {
    private val _uiState = MutableStateFlow(EditGradeUiState())
    val uiState: StateFlow<EditGradeUiState> = _uiState.asStateFlow()

    private val _subGrades = mutableListOf<Grade>()
    private var percentageJob: Job? = null

    val defaultPercentage: Percentage get() = Percentage(_uiState.value.defaultPercentage)
    val gradeMax: Int get() = gradeFactory.instGrade().getMax()

    fun setGrade(grade: String) {
        _uiState.update { it.copy(gradeValue = grade) }
    }
    fun setPercentage(percentage: String) {
        val value = percentage.toDoubleOrNull()
        if (!percentage.isBlank() && value != null && Percentage.check(value)) {
            _uiState.update { it.copy(percentage = percentage) }
        } else {
            actDefaultPercentage()
        }
    }
    fun setCourseId(courseId: Int) {
        if (_uiState.value.courseId == courseId) return
        _uiState.update { it.copy(courseId = courseId) }
        actDefaultPercentage(courseId)
    }
    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    fun setCourse(course: CourseModel) {
        _uiState.update { it.copy(course = course) }
    }

    fun actDefaultPercentage(courseId: Int = _uiState.value.courseId) {
        if (courseId == -1) return

        percentageJob?.cancel()
        percentageJob = viewModelScope.launch {
            getCourseTotalGradesRemainingPercentageUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val remaining = result.data?.getPercentage() ?: 100.0
                        _uiState.update {
                            it.copy(
                                defaultPercentage = remaining,
                                percentage = ""
                            )
                        }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getCourseTotalGradesPercentageUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    // --- SubGrades ---

    fun calGradeFromSubGrades() {
        if (_subGrades.isEmpty()) return
        val avg = _subGrades.averageGrade()
        _uiState.update { it.copy(gradeValue = avg?.let { g -> Grade.formatText(g) } ?: "") }
    }

    fun setSubGrade(index: Int, subGrade: String) {
        val updated = _uiState.value.subGrades.toMutableList()
        updated[index] = subGrade
        _uiState.update { it.copy(subGrades = updated) }

        val value = subGrade.toDoubleOrNull()
        if (subGrade.isNotBlank() && value != null) _subGrades[index].setValue(value)
        else _subGrades[index].setBlank()
        calGradeFromSubGrades()
    }

    fun addSubGrade() {
        if (_subGrades.isEmpty()) {
            val currentGrade = _uiState.value.gradeValue.toDoubleOrNull()
            if (currentGrade != null) {
                val g = gradeFactory.instGrade()
                g.setValue(currentGrade)
                _subGrades.add(g)
                _uiState.update { it.copy(subGrades = it.subGrades + Grade.formatText(currentGrade)) }
            } else {
                _subGrades.add(gradeFactory.instGrade())
                _uiState.update { it.copy(subGrades = it.subGrades + "") }
            }
        } else {
            _subGrades.add(gradeFactory.instGrade())
            _uiState.update { it.copy(subGrades = it.subGrades + "") }
        }
        calGradeFromSubGrades()
    }

    fun removeSubGrade(index: Int) {
        _subGrades.removeAt(index)
        val updated = _uiState.value.subGrades.toMutableList()
        updated.removeAt(index)
        _uiState.update { it.copy(subGrades = updated) }
        calGradeFromSubGrades()
    }

    fun loadSubGradesFromGrade(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val subGrades: List<SubGradeModel> = result.data!!
                        val updated = _uiState.value.subGrades.toMutableList()
                        subGrades.forEach {
                            _subGrades.add(Grade(it.grade))
                            updated.add(it.grade.toString())
                        }
                        _uiState.update { it.copy(subGrades = updated) }
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

    private suspend fun saveSubGrades(gradeId: Int): String? {
        if (gradeId == -1) return null
        _subGrades.forEachIndexed { index, grade ->
            val error = saveSubGrade(SubGradeModel(
                title = "SubGrade $index",
                grade = grade,
                gradeId = gradeId
            ))
            if (error != null) return error
        }
        return null
    }

    private suspend fun saveSubGrade(subGrade: SubGradeModel): String? {
        val result = saveSubGradeUseCase(subGrade).first { it !is Resource.Loading }
        return when (result) {
            is Resource.Success -> {
                Log.i("EditGradeViewModel", "saveSubGrade id: ${result.data}")
                null
            }
            is Resource.Error -> {
                Log.e("EditGradeViewModel", "Error saving subGrade: ${result.message}")
                result.message ?: "Error al guardar sub-calificación"
            }
            else -> null
        }
    }

    private suspend fun deleteSubGradesFromGrade(gradeId: Int): String? {
        if (gradeId == -1) return null
        val result = deleteAllSubGradesFromGradeUseCase(gradeId).first { it !is Resource.Loading }
        return when (result) {
            is Resource.Success -> {
                Log.i("EditGradeViewModel", "deleteSubGradesFromGrade id: $gradeId amount: ${result.data}")
                null
            }
            is Resource.Error -> {
                Log.e("EditGradeViewModel", "Error deleting subGrades: ${result.message}")
                result.message ?: "Error al eliminar sub-calificaciones"
            }
            else -> null
        }
    }

    fun loadGradeFromId(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getGradeByIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grade = result.data!!
                        _uiState.update {
                            it.copy(
                                title = grade.title,
                                description = grade.description,
                                gradeValue = grade.grade.toString(),
                                percentage = grade.percentage.toString(),
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

    private suspend fun saveGradeWithSubGrades(gradeModel: GradeModel): String? {
        val result = saveGradeUseCase(gradeModel = gradeModel).first { it !is Resource.Loading }
        return when (result) {
            is Resource.Success -> {
                if (result.data != null) {
                    val error = saveSubGrades(result.data.toInt())
                    if (error != null) return error
                }
                Log.i("EditGradeViewModel", "saveGrade id: ${result.data}")
                null
            }
            is Resource.Error -> {
                Log.e("EditGradeViewModel", "Error saving grade: ${result.message}")
                result.message ?: "Error al guardar calificación"
            }
            else -> null
        }
    }

    private suspend fun updateGradeWithSubGrades(gradeModel: GradeModel): String? {
        val result = updateGradeUseCase(gradeModel = gradeModel).first { it !is Resource.Loading }
        return when (result) {
            is Resource.Success -> {
                val error = deleteSubGradesFromGrade(gradeModel.id)
                if (error != null) return error
                val saveError = saveSubGrades(gradeModel.id)
                if (saveError != null) return saveError
                Log.i("EditGradeViewModel", "updateGrade id: ${gradeModel.id}")
                null
            }
            is Resource.Error -> {
                Log.e("EditGradeViewModel", "Error saving grade: ${result.message}")
                result.message ?: "Error al actualizar calificación"
            }
            else -> null
        }
    }

    suspend fun submitGrade(gradeId: Int, activity: Activity?): String? {
        val state = _uiState.value
        if (state.courseId == -1) return "Selecciona una asignatura"
        var result: Result<GradeDetailModel>

        val gradeValue = state.gradeValue.toDoubleOrNull()

        val percentageValue = state.percentage.toDoubleOrNull() ?: state.defaultPercentage
        val percentage = Percentage(percentageValue)

        result = GradeDetailModel.create(
            courseId = state.courseId,
            title = state.title,
            description = state.description,
            gradeValue = gradeValue,
            gradeFactory = gradeFactory,
            percentage = percentage,
            id = gradeId,
            subgrades = _subGrades.mapIndexed { index, g ->
                SubGradeModel(gradeId = gradeId, title = "SubGrade $index", grade = g)
            }
        )

        if (result.isFailure) {
            return result.exceptionOrNull()?.message ?: "Error de validación"
        }

        val gradeDetail = result.getOrNull()!!

        val saveError = if (gradeId == -1) {
            saveGradeWithSubGrades(gradeDetail)
        } else {
            updateGradeWithSubGrades(gradeDetail)
        }
        if (saveError != null) return saveError

        if (activity != null) {
            viewModelScope.launch {
                launchInAppReviewIfValidUseCase(activity).collect {}
            }
        }
        return null
    }

    // --- Course loading ---

    private fun getCourseFromId(courseId: Int) {
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseByIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(course = result.data!!) }
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
                        val courses = result.data.orEmpty()

                        if (courses.isNotEmpty()) {
                            if (courseId == -1) {
                                val firstCourse = courses[0]
                                _uiState.update {
                                    it.copy(
                                        courses = courses,
                                        course = firstCourse,
                                        courseId = firstCourse.id
                                    )
                                }
                                actDefaultPercentage(firstCourse.id)
                            } else {
                                _uiState.update { it.copy(courses = courses) }
                                getCourseFromId(courseId)
                            }
                        } else {
                            _uiState.update { it.copy(courses = courses) }
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
