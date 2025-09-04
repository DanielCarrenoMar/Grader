package com.app.grader.ui.pages.transferSemester

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.usecase.semester.GetSemesterByIdUseCase
import com.app.grader.domain.usecase.semester.SaveSemesterUseCase
import com.app.grader.domain.usecase.semester.UpdateSemesterUseCase
import com.app.grader.ui.sharedViewModels.EditSemesterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferSemesterViewModel @Inject constructor(
    getSemesterByIdUseCase: GetSemesterByIdUseCase,
    saveSemesterUseCase: SaveSemesterUseCase,
    updateSemesterUseCase: UpdateSemesterUseCase
): EditSemesterViewModel(
    getSemesterByIdUseCase,
    saveSemesterUseCase,
    updateSemesterUseCase
) {

}