package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DeleteSemesterByIdUseCase  @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterId: Int): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.deleteSemesterFromId(semesterId)){
                send(
                    Resource.Success(Unit)
                )
            }else{
                send(
                    Resource.Error("Delete semester id: $semesterId Error")
                )
            }

        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}