package com.app.grader.domain.usecase.typeGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetDefaultTypeGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val appConfigRepository: AppConfigRepository
) {
    operator fun invoke(): Flow<Resource<TypeGradeModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val typeGradeId = appConfigRepository.getDefaultTypeGradeId()
            val typeGrade = repository.getTypeGradeById(typeGradeId)
                ?: throw IllegalStateException("Default type grade not found")
            send(Resource.Success(data = typeGrade))
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}
