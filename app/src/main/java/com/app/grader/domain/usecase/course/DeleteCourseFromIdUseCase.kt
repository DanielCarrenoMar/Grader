package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DeleteCourseFromIdUseCase  @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseId: Int): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.deleteCourseFromId(courseId)){
                send(
                    Resource.Success(Unit)
                )
            }else{
                send(
                    Resource.Error("Delete course id: $courseId Error")
                )
            }

        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}