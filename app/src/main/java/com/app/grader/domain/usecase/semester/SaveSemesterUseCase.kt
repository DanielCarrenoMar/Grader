package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveSemesterUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterModel: SemesterModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveSemester(semesterModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
            } else {
                send(
                    Resource.Error("Save semester Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}