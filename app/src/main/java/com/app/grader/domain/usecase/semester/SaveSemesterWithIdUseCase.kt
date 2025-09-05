package com.app.grader.domain.usecase.semester

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.data.database.entitites.SemesterEntity
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveSemesterWithIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterEntity: SemesterEntity): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.insertSemesterWithId(semesterEntity)
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