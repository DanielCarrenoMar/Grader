package com.app.grader.domain.usecase.subGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateSubGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(subGradeModel: SubGradeModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.updateSubGrade(subGradeModel)){
                send(
                    Resource.Success(data = Unit)
                )
            } else {
                send(
                    Resource.Error("Update subGrade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}