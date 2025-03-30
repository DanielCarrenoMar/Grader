package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DeleteGradeFromIdUseCase  @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(gradeId: Int): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.deleteGradeFromId(gradeId)){
                send(
                    Resource.Success(Unit)
                )
            }else{
                send(
                    Resource.Error("Delete grade id: $gradeId Error")
                )
            }

        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}