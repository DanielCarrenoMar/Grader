package com.app.grader.domain.usecase.subGrade

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.data.database.entitites.SubGradeEntity
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveSubGradeWithIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(subGradeEntity: SubGradeEntity): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.insertSubGradeWithId(subGradeEntity)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
            } else {
                send(
                    Resource.Error("Save course Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}