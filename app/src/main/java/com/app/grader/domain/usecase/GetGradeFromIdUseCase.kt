package com.app.grader.domain.usecase

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetGradeFromIdUseCase  @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(gradeId:Int): Flow<Resource<GradeModel?>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getGradeFromId(gradeId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}