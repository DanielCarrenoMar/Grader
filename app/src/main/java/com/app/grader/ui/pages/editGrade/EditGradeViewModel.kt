package com.app.grader.ui.pages.editGrade

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeDetailModel
import com.app.grader.domain.model.GradeFieldError
import com.app.grader.domain.model.GradeDetailValidationException
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.model.average
import com.app.grader.domain.model.normalize
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCourseTotalGradesRemainingPercentageUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradeByIdUseCase
import com.app.grader.domain.usecase.grade.SaveGradeDetailUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeDetailUseCase
import com.app.grader.domain.usecase.review.LaunchInAppReviewIfValidUseCase
import com.app.grader.domain.usecase.subGrade.GetSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.typeGrade.GetTypeGradeFromCourseIdUseCase
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
    val title: String = "",
    val description: String = "",
    val gradeValue: String = "",
    val percentage: String = "",
    val defaultPercentage: Double = 100.0,
    val courseId: Int = -1,
    val course: CourseModel = CourseModel.DEFAULT,
    val courses: List<CourseModel> = emptyList(),
    val subGrades: List<SubGradeModel> = emptyList(),
    val subGradeTexts: List<String> = emptyList(),
    val fieldErrors: Map<String, String> = emptyMap(),
)

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeByIdUseCase: GetGradeByIdUseCase,
    private val saveGradeDetailUseCase: SaveGradeDetailUseCase,
    private val updateGradeDetailUseCase: UpdateGradeDetailUseCase,
    private val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getCourseTotalGradesRemainingPercentageUseCase: GetCourseTotalGradesRemainingPercentageUseCase,
    private val getSubGradesFromGradeUseCase: GetSubGradesFromGradeUseCase,
    private val launchInAppReviewIfValidUseCase: LaunchInAppReviewIfValidUseCase,
    private val getTypeGradeFromCourseIdUseCase: GetTypeGradeFromCourseIdUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow(EditGradeUiState())
    val uiState: StateFlow<EditGradeUiState> = _uiState.asStateFlow()

    private val _defaultTypeGrade = MutableStateFlow<TypeGradeModel?>(null)
    val defaultTypeGrade: StateFlow<TypeGradeModel?> = _defaultTypeGrade.asStateFlow()

    private val _subGrades = mutableListOf<SubGradeModel>()
    private var percentageJob: Job? = null
    private var typeGradeJob: Job? = null
    private var loadedTypeGradeCourseId: Int? = null

    init {
        loadTypeGradeFromCourse(_uiState.value.courseId)
    }

    val defaultPercentage: Percentage get() = Percentage(_uiState.value.defaultPercentage)

    private fun loadTypeGradeFromCourse(courseId: Int) {
        typeGradeJob?.cancel()
        loadedTypeGradeCourseId = null
        _defaultTypeGrade.value = null
        if (courseId == -1) return
        typeGradeJob = viewModelScope.launch {
            getTypeGradeFromCourseIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (_uiState.value.courseId != courseId || result.data == null) return@collect
                        _defaultTypeGrade.value = result.data
                        loadedTypeGradeCourseId = courseId
                        _subGrades.replaceAll { it.normalize(result.data) }
                        _uiState.update { state ->
                            state.copy(
                                subGrades = _subGrades.toList(),
                                subGradeTexts = _subGrades.map { it.gradeValue.toString() },
                            )
                        }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getTypeGradeFromCourseIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun setGrade(grade: String) {
        _uiState.update { it.copy(gradeValue = grade, fieldErrors = it.fieldErrors - "grade") }
    }
    fun setPercentage(percentage: String) {
        _uiState.update { it.copy(percentage = percentage, fieldErrors = it.fieldErrors - "percentage") }
    }
    fun setCourseId(courseId: Int) {
        if (_uiState.value.courseId == courseId) return
        _uiState.update { it.copy(courseId = courseId, fieldErrors = emptyMap()) }
        actDefaultPercentage(courseId)
        loadTypeGradeFromCourse(courseId)
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
        val avg = _subGrades.average()
        _uiState.update { it.copy(gradeValue = avg?.let { g -> GradeValue.formatText(g) } ?: "") }
    }

    fun setSubGrade(index: Int, subGrade: String) {
        val state = _uiState.value
        if (index !in _subGrades.indices || index !in state.subGrades.indices || index !in state.subGradeTexts.indices) return

        val typeGrade = _defaultTypeGrade.value
        val current = _subGrades[index]
        val value = subGrade.trim().replace(',', '.').toDoubleOrNull()
        val result = if (subGrade.isNotBlank() && value == null) {
            Result.failure(IllegalArgumentException("La calificación debe ser un número válido."))
        } else if (typeGrade == null) {
            Result.failure(IllegalArgumentException("Cargando el tipo de calificación"))
        } else {
            SubGradeModel.create(
                gradeId = current.gradeId,
                title = current.title,
                gradeValue = value,
                minToPass = typeGrade.minToPass,
                max = typeGrade.max,
                id = current.id,
            )
        }
        val updatedModels = _subGrades.toMutableList()
        val updatedTexts = state.subGradeTexts.toMutableList()
        updatedTexts[index] = subGrade
        val updatedErrors = state.fieldErrors.toMutableMap()
        if (result.isSuccess) {
            updatedModels[index] = result.getOrThrow()
            _subGrades[index] = updatedModels[index]
            updatedErrors.remove("subgrade:$index")
        } else {
            updatedErrors["subgrade:$index"] = result.exceptionOrNull()?.message ?: "Error de validación"
        }
        _uiState.update {
            it.copy(
                subGrades = updatedModels,
                subGradeTexts = updatedTexts,
                fieldErrors = updatedErrors,
            )
        }
        if (result.isSuccess) calGradeFromSubGrades()
    }

    fun addSubGrade() {
        val typeGrade = _defaultTypeGrade.value ?: return
        if (typeGrade.isDirectPercentage) return
        val currentGrade = _uiState.value.gradeValue.replace(',', '.').toDoubleOrNull()
        val model = SubGradeModel.create(
            gradeId = -1,
            title = "SubGrade ${_subGrades.size}",
            gradeValue = if (_subGrades.isEmpty()) currentGrade else null,
            minToPass = typeGrade.minToPass,
            max = typeGrade.max,
        ).getOrElse { return }
        _subGrades.add(model)
        _uiState.update { it.copy(subGrades = it.subGrades + model, subGradeTexts = it.subGradeTexts + model.gradeValue.toString()) }
        calGradeFromSubGrades()
    }

    fun removeSubGrade(index: Int) {
        if (index !in _subGrades.indices || index !in _uiState.value.subGrades.indices) return

        _subGrades.removeAt(index)
        val updated = _uiState.value.subGrades.toMutableList()
        val updatedTexts = _uiState.value.subGradeTexts.toMutableList()
        if (index in updated.indices) updated.removeAt(index)
        if (index in updatedTexts.indices) updatedTexts.removeAt(index)
        _uiState.update {
            it.copy(
                subGrades = updated,
                subGradeTexts = updatedTexts,
                fieldErrors = it.fieldErrors.filterKeys { key -> key != "subgrade:$index" }
            )
        }
        calGradeFromSubGrades()
    }

    fun loadSubGradesFromGrade(gradeId: Int) {
        if (gradeId == -1) return
        viewModelScope.launch {
            getSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val subGrades: List<SubGradeModel> = result.data!!
                        _subGrades.addAll(subGrades.map { it.normalize(_defaultTypeGrade.value) })
                        _uiState.update {
                            it.copy(
                                subGrades = _subGrades.toList(),
                                subGradeTexts = _subGrades.map { subGrade -> subGrade.gradeValue.toString() },
                            )
                        }
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getSubGradesFromGradeUserCase: ${result.message}")
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
                        _uiState.update {
                            it.copy(
                                title = grade.title,
                                description = grade.description,
                                gradeValue = grade.gradeValue.toString(),
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

    suspend fun submitGrade(gradeId: Int, activity: Activity?): String? {
        val state = _uiState.value
        if (state.courseId == -1) return "Selecciona una asignatura"
        val typeGrade = _defaultTypeGrade.value
        if (typeGrade == null || loadedTypeGradeCourseId != state.courseId) return "Cargando el tipo de calificación"
        var result: Result<GradeDetailModel>

        val gradeText = state.gradeValue.trim()
        val gradeValue = gradeText.replace(',', '.').toDoubleOrNull()
        val inputErrors = mutableMapOf<String, String>()
        val percentageText = state.percentage.trim()
        val percentageValue = if (percentageText.isBlank()) state.defaultPercentage else percentageText.replace(',', '.').toDoubleOrNull()
        if (percentageValue == null) {
            inputErrors["percentage"] = "El porcentaje debe ser un número válido."
        }
        val subgradeErrors = mutableMapOf<String, String>()
        val subgradeModels = _subGrades.mapIndexedNotNull { index, subgrade ->
            val text = state.subGradeTexts.getOrNull(index).orEmpty()
            if (text.isBlank()) return@mapIndexedNotNull null
            val value = text.trim().replace(',', '.').toDoubleOrNull()
            val result = if (value == null) {
                Result.failure(IllegalArgumentException("La calificación debe ser un número válido."))
            } else {
                SubGradeModel.create(
                    gradeId = gradeId,
                    title = subgrade.title,
                    gradeValue = value,
                    minToPass = typeGrade.minToPass,
                    max = typeGrade.max,
                    id = subgrade.id,
                )
            }
            result.getOrElse {
                subgradeErrors["subgrade:$index"] = it.message ?: "Error de validación"
                return@mapIndexedNotNull null
            }
        }
        if (inputErrors.isNotEmpty() || subgradeErrors.isNotEmpty()) {
            _uiState.update { it.copy(fieldErrors = it.fieldErrors + inputErrors + subgradeErrors) }
            return (inputErrors + subgradeErrors).values.first()
        }
        val percentage = Percentage(percentageValue!!)

        result = GradeDetailModel.create(
            courseId = state.courseId,
            title = state.title,
            description = state.description,
            gradeValue = gradeValue,
            percentage = percentage,
            typeGrade = typeGrade,
            id = gradeId,
            subgrades = subgradeModels
        )

        if (result.isFailure) {
            val exception = result.exceptionOrNull()
            val errors = (exception as? GradeDetailValidationException)?.errors.orEmpty()
            val message = exception?.message ?: "Error de validación"
            val weightingError = com.app.grader.domain.policy.GradeRules.isWeightingOverflow(exception?.message)
            val finalErrors = if (weightingError) errors + GradeFieldError("percentage", message) else errors
            _uiState.update { it.copy(fieldErrors = finalErrors.associate { error -> error.field to error.message }) }
            return exception?.message ?: "Error de validación"
        }

        val gradeDetail = result.getOrNull()!!

        val saveResult = if (gradeId == -1) {
            saveGradeDetailUseCase(gradeDetail).first { it !is Resource.Loading }
        } else {
            updateGradeDetailUseCase(gradeDetail).first { it !is Resource.Loading }
        }
        val saveError = (saveResult as? Resource.Error)?.message
        if (saveError != null) {
            return saveError
        }

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
                                loadTypeGradeFromCourse(firstCourse.id)
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
