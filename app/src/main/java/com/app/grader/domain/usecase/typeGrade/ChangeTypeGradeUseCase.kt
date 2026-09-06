package com.app.grader.domain.usecase.typeGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class ChangeTypeGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(isDirectPercentage: Boolean): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            repository.updateDirectPercentageForAll(isDirectPercentage)
            send(Resource.Success(Unit))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
