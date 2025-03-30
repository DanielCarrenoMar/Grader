package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateCourseUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseModel: CourseModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.updateCourse(courseModel)){
                send(
                    Resource.Success(data = Unit)
                )
            } else {
                send(
                    Resource.Error("Update course Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}