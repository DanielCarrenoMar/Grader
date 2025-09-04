package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.Grade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetWeightFromSemesterUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterId: Int?): Flow<Resource<Int>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getWeightOfSemester(semesterId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}