package com.app.grader.ui.course

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.GetAllCoursesUserCase
import com.app.grader.domain.usecase.SaveCourseUserCase
import com.app.grader.domain.usecase.DeleteAllCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel  @Inject constructor(

): ViewModel() {

}