package com.app.grader.domain.usecase.grade

import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetGradesFromSemesterLessThanUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterId: Int?): Flow<Resource<List<GradeModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getGradesFromSemesterLessThan(semesterId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}