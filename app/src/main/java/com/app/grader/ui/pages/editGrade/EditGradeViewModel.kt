package com.app.grader.ui.pages.editGrade

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeDetailModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.types.averageGrade
import com.app.grader.domain.usecase.course.GetCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCourseTotalGradesRemainingPercentageUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradeByIdUseCase
import com.app.grader.domain.usecase.grade.SaveGradeDetailUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeDetailUseCase
import com.app.grader.domain.usecase.review.LaunchInAppReviewIfValidUseCase
import com.app.grader.domain.usecase.subGrade.GetSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.typeGrade.GetDefaultTypeGradeUseCase
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
    private val saveGradeDetailUseCase: SaveGradeDetailUseCase,
    private val updateGradeDetailUseCase: UpdateGradeDetailUseCase,
    private val getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    private val getCourseByIdUseCase: GetCourseByIdUseCase,
    private val getCourseTotalGradesRemainingPercentageUseCase: GetCourseTotalGradesRemainingPercentageUseCase,
    private val getSubGradesFromGradeUseCase: GetSubGradesFromGradeUseCase,
    private val launchInAppReviewIfValidUseCase: LaunchInAppReviewIfValidUseCase,
    private val getDefaultTypeGradeUseCase: GetDefaultTypeGradeUseCase,
    private val gradeFactory: GradeFactory,
): ViewModel() {
    private val _uiState = MutableStateFlow(EditGradeUiState())
    val uiState: StateFlow<EditGradeUiState> = _uiState.asStateFlow()

    private val _defaultTypeGrade = MutableStateFlow<TypeGradeModel?>(null)
    val defaultTypeGrade: StateFlow<TypeGradeModel?> = _defaultTypeGrade.asStateFlow()

    private val _subGrades = mutableListOf<GradeValue>()
    private var percentageJob: Job? = null

    init {
        loadDefaultTypeGrade()
    }

    val defaultPercentage: Percentage get() = Percentage(_uiState.value.defaultPercentage)

    private fun loadDefaultTypeGrade() {
        viewModelScope.launch {
            getDefaultTypeGradeUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _defaultTypeGrade.value = result.data
                    }
                    is Resource.Loading -> { }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getDefaultTypeGradeUseCase: ${result.message}")
                    }
                }
            }
        }
    }

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
        _uiState.update { it.copy(gradeValue = avg?.let { g -> GradeValue.formatText(g) } ?: "") }
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
                _uiState.update { it.copy(subGrades = it.subGrades + GradeValue.formatText(currentGrade)) }
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
                            _subGrades.add(GradeValue(it.gradeValue))
                            updated.add(it.gradeValue.toString())
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
                SubGradeModel(gradeId = gradeId, title = "SubGrade $index", gradeValue = g)
            }
        )

        if (result.isFailure) {
            return result.exceptionOrNull()?.message ?: "Error de validación"
        }

        val gradeDetail = result.getOrNull()!!

        val saveResult = if (gradeId == -1) {
            saveGradeDetailUseCase(gradeDetail).first { it !is Resource.Loading }
        } else {
            updateGradeDetailUseCase(gradeDetail).first { it !is Resource.Loading }
        }
        val saveError = (saveResult as? Resource.Error)?.message
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
