package com.app.grader.ui.editGrade

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import com.app.grader.domain.types.averageGrade
import com.app.grader.domain.usecase.course.GetAllCoursesUserCase
import com.app.grader.domain.usecase.course.GetCourseFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradeFromIdUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromCourseUseCase
import com.app.grader.domain.usecase.grade.SaveGradeUseCase
import com.app.grader.domain.usecase.grade.UpdateGradeUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesFromGradeIdUseCase
import com.app.grader.domain.usecase.subGrade.GetSubGradesFromGradeUseCase
import com.app.grader.domain.usecase.subGrade.SaveSubGradeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.isNotBlank
import kotlin.text.toDoubleOrNull

@HiltViewModel
class EditGradeViewModel @Inject constructor(
    private val getGradeFromIdUseCase: GetGradeFromIdUseCase,
    private val getGradesFromCourseUseCase: GetGradesFromCourseUseCase,
    private val saveGradeUseCase: SaveGradeUseCase,
    private val updateGradeUseCase: UpdateGradeUseCase,
    private val getAllCoursesUserCase: GetAllCoursesUserCase,
    private val getCourseFromIdUseCase: GetCourseFromIdUseCase,
    private val getSubGradesFromGradeUseCase: GetSubGradesFromGradeUseCase,
    private val saveSubGradeUseCase: SaveSubGradeUseCase,
    private val deleteAllSubGradesFromGradeUseCase: DeleteAllSubGradesFromGradeIdUseCase
): ViewModel() {
    private val _title = mutableStateOf("Sin Titulo")
    val title = _title
    private val _description = mutableStateOf("Sin Descricción")
    val description = _description
    private val _grade = mutableStateOf(Grade(20.0))
    val grade = _grade
    private val _percentage = mutableStateOf(Percentage(100.0))
    val percentage = _percentage
    private val _defaultPercentage = mutableStateOf(Percentage(100.0))
    val defaultPercentage = _defaultPercentage
    private val _savedPercentage = mutableStateOf(Percentage(0.0))

    private val _showTitle = mutableStateOf("")
    val showTitle = _showTitle
    private val _showDescription = mutableStateOf("")
    val showDescription = _showDescription
    private val _showGrade = mutableStateOf("")
    val showGrade = _showGrade
    private val _showPercentage = mutableStateOf("")
    val showPercentage = _showPercentage

    private val _courseId = mutableIntStateOf(-1)
    val courseId = _courseId
    private val _showCourse = mutableStateOf(CourseModel.DEFAULT)
    val showCourse = _showCourse
    private val _courses = mutableStateOf<List<CourseModel>>(emptyList())
    val courses = _courses

    private val _subGrades = mutableStateListOf<Grade>()
    private val _showSubGrades = mutableStateListOf<String>()
    val showSubGrades = _showSubGrades

    fun setGrade(grade: String){
        _showGrade.value = grade
        val value = grade.toDoubleOrNull()

        if (grade.isNotBlank() && value != null && Grade.check(value) ) _grade.value.setGrade(value)
    }

    fun setPercentage(percentage: String){
        _showPercentage.value = percentage
        val value = percentage.toDoubleOrNull()

        if (percentage.isBlank() || value == null ){
            actDefaultPercentage()
        }
        else if (Percentage.check(value)) _percentage.value.setPercentage(value)
    }

    fun setCourseId(courseId: Int){
        if (_courseId.intValue == courseId) return
        _courseId.intValue = courseId
        actDefaultPercentage()
    }

    fun actDefaultPercentage(courseId: Int = _courseId.intValue) {
        if (courseId == -1) return
        viewModelScope.launch {
            getGradesFromCourseUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grades = result.data!!
                        var totalPercentage = 0.0
                        grades.forEach { grade ->
                            totalPercentage += grade.percentage
                        }
                        _defaultPercentage.value.setPercentage(100.0 - totalPercentage + _savedPercentage.value.getPercentage())
                        _showPercentage.value = " "
                        _showPercentage.value = ""
                    }

                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }

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

    fun calGradeFromSubGrades() {
        if (_subGrades.isEmpty()) return
        _grade.value.setGrade( _subGrades.averageGrade())
        _showGrade.value = _grade.value.toString().removeSuffix(".0")
    }

    fun setSubGrade(index: Int, subGrade: String){
        _showSubGrades[index] = subGrade
        val value = subGrade.toDoubleOrNull()

        if (subGrade.isNotBlank() && value != null && Grade.check(value) ) _subGrades[index].setGrade(value)
        calGradeFromSubGrades()
    }

    fun addSubGrade(){
        _subGrades.add(Grade(1.0))
        _showSubGrades.add("")
        calGradeFromSubGrades()
    }

    fun removeSubGrade(index: Int){
        _subGrades.removeAt(index)
        _showSubGrades.removeAt(index)
        calGradeFromSubGrades()
    }

    fun loadSubGradesFromGrade(gradeId: Int){
        if (gradeId == -1) return
        viewModelScope.launch {
            getSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val subGrades:List<SubGradeModel> = result.data!!
                        subGrades.map {
                            _subGrades.add(Grade(it.grade))
                            _showSubGrades.add(it.grade.toString().removeSuffix(".0"))
                        }
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getSubGradesFromGradeUserCase: ${result.message}")
                    }
                }
            }
        }
    }

    private fun saveSubGrades(gradeId: Int){
        if (gradeId == -1) return
        _subGrades.forEachIndexed { index, grade ->
            saveSubGrade(SubGradeModel(
                title = "SubGrade $index",
                grade = grade.getGrade(),
                gradeId = gradeId
            ))
        }
    }

    private fun saveSubGrade(subGrade: SubGradeModel){
        viewModelScope.launch {
            saveSubGradeUseCase(subGrade).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "saveSubGrade id: ${result.data}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error saving subGrade: ${result.message}")
                    }
                }
            }
        }
    }

    private fun deleteSubGradesFromGrade(gradeId: Int){
        if (gradeId == -1) return
        viewModelScope.launch {
            deleteAllSubGradesFromGradeUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("EditGradeViewModel", "deleteSubGradesFromGrade id: $gradeId amount: ${result.data}")
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
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
            getGradeFromIdUseCase(gradeId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val grade = result.data!!
                        _title.value = grade.title
                        _showTitle.value = grade.title
                        _description.value = grade.description
                        _showDescription.value = grade.description
                        _grade.value.setGrade(grade.grade)
                        _showGrade.value = grade.grade.toString().removeSuffix(".0")
                        _percentage.value.setPercentage(grade.percentage)
                        _showPercentage.value = grade.percentage.toString().removeSuffix(".0")
                        _savedPercentage.value.setPercentage(grade.percentage)
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

    private fun saveGradeWithSubGrades(gradeModel: GradeModel) {
        viewModelScope.launch {
            saveGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        if (result.data != null) saveSubGrades(result.data.toInt())
                        Log.i("EditGradeViewModel", "saveGrade id: ${result.data}")
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

    private fun updateGradeWithSubGrades(gradeModel: GradeModel) {
        viewModelScope.launch {
            updateGradeUseCase(gradeModel = gradeModel).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        deleteSubGradesFromGrade(gradeModel.id)
                        saveSubGrades(gradeModel.id)
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

    private fun checkInputs(): Boolean {
        return _title.value.isNotBlank() &&
                _description.value.isNotBlank() &&
                _grade.value.getGrade() != 0.0 &&
                _grade.value.getGrade() == _showGrade.value.toDoubleOrNull() &&
                _percentage.value.getPercentage() != 0.0 &&
                _percentage.value.getPercentage() == _showPercentage.value.toDoubleOrNull() &&
                _percentage.value.getPercentage() <= _defaultPercentage.value.getPercentage()
    }

    private fun syncInvalidInputs() {
        val showGradeValue = _showGrade.value.toDoubleOrNull()
        if (_showGrade.value.isBlank() ||
            showGradeValue == null ||
            !Grade.check(showGradeValue)
            ) _showGrade.value = _grade.value.toString().removeSuffix(".0")

        val showPercentageValue = _showPercentage.value.toDoubleOrNull()
        if (_showPercentage.value.isBlank() ||
            _percentage.value.getPercentage() == 0.0 ||
            showPercentageValue == null ||
            !Percentage.check(showPercentageValue) ||
            showPercentageValue > _defaultPercentage.value.getPercentage()
            ) {
            _showPercentage.value = _defaultPercentage.value.toString().removeSuffix(".0")
            _percentage.value.setPercentage(_defaultPercentage.value.getPercentage())
        }
    }

    fun updateOrCreateGrade(gradeId: Int): Boolean{
        if (_courseId.intValue == -1) return false
        if (checkInputs().not()) {
            syncInvalidInputs()
            return false
        }
        viewModelScope.launch {
            if (gradeId == -1) {
                saveGradeWithSubGrades(
                    GradeModel(
                        courseId = _courseId.intValue,
                        title = _title.value,
                        description = _description.value,
                        grade = _grade.value.getGrade(),
                        percentage = _percentage.value.getPercentage(),
                    )
                )
            } else {
                updateGradeWithSubGrades(
                    GradeModel(
                        courseId = _courseId.intValue,
                        title = _title.value,
                        description = _description.value,
                        grade = _grade.value.getGrade(),
                        percentage = _percentage.value.getPercentage(),
                        id = gradeId,
                    )
                )
            }
        }
        return true
    }

    private fun getCourseFromId(courseId: Int){
        if (courseId == -1) return
        viewModelScope.launch {
            getCourseFromIdUseCase(courseId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _showCourse.value = result.data!!
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("EditGradeViewModel", "Error getCourseFromIdUseCase: ${result.message}")
                    }
                }
            }
        }
    }

    fun loadCourseOptions(courseId: Int = _courseId.intValue) {
        viewModelScope.launch {
            getAllCoursesUserCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _courses.value = result.data!!

                        if (courses.value.isNotEmpty()) {
                            if (courseId == -1){
                                showCourse.value = courses.value[0]
                                setCourseId(courses.value[0].id)
                            }else{
                                getCourseFromId(courseId)
                            }
                        }
                    }
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Error -> {
                        Log.e("HomeViewModel", "Error getAllcourse: ${result.message}")
                    }
                }
            }
        }
    }

}