package com.app.grader.domain.usecase

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(gradeModel: GradeModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.updateGrade(gradeModel)){
                send(
                    Resource.Success(data = Unit)
                )
            } else {
                send(
                    Resource.Error("Update grade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}