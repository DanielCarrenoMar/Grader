package com.app.grader.domain.usecase.subGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveSubGradeUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(subGradeModel: SubGradeModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveSubGrade(subGradeModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
            } else {
                send(
                    Resource.Error("Save subGrade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}