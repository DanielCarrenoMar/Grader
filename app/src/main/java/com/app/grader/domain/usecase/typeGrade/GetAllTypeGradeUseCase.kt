package com.app.grader.domain.usecase.typeGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAllTypeGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(): Flow<Resource<List<TypeGradeModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getAllTypeGrades()
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}