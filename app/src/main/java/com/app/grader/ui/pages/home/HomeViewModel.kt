package com.app.grader.ui.pages.home

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.usecase.course.DeleteCourseByIdUseCase
import com.app.grader.domain.usecase.course.GetCoursesFromSemesterUseCase
import com.app.grader.domain.usecase.grade.GetGradesFromSemesterUseCase
import com.app.grader.domain.usecase.semester.GetAverageFromSemesterUseCase
import com.app.grader.ui.sharedViewModels.SemesterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    getCoursesFromSemesterUseCase: GetCoursesFromSemesterUseCase,
    deleteCourseByIdUseCase: DeleteCourseByIdUseCase,
    getGradesFromSemesterUseCase: GetGradesFromSemesterUseCase,
    getAverageFromSemesterUseCase: GetAverageFromSemesterUseCase,
    gradeFactory: GradeFactory,
): SemesterViewModel(
    getCoursesFromSemesterUseCase,
    deleteCourseByIdUseCase,
    getGradesFromSemesterUseCase,
    getAverageFromSemesterUseCase,
    gradeFactory
) {

}