package com.app.grader.domain.usecase

import android.util.Log
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveCourseUserCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseModel: CourseModel): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.saveCourse(courseModel)){
                send(
                    Resource.Success(data = Unit)
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