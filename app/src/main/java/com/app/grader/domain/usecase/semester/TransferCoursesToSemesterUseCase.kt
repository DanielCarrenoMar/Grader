package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class TransferCoursesToSemesterUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterIdSender: Int?, semesterIdReceiver: Int?): Flow<Resource<Int>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.transferSemesterToSemester(semesterIdSender, semesterIdReceiver)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}