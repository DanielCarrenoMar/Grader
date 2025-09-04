package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetSemesterByIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterId: Int): Flow<Resource<SemesterModel?>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getSemesterById(semesterId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}