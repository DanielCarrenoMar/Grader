package com.app.grader.domain.usecase

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAllGradesUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(): Flow<Resource<Int>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.deleteAllGrades()
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}