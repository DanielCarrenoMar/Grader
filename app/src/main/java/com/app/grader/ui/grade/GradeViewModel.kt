package com.app.grader.ui.grade

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.GetGradesFromCourseUserCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GradeViewModel @Inject constructor(

): ViewModel() {

}