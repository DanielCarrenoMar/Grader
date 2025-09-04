package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.Grade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAverageFromCourseUseCase  @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseId:Int): Flow<Resource<Grade?>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getAverageFromCourse(courseId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}