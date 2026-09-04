package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.CourseStatisticsModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetCourseStatisticsUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
) {
    operator fun invoke(courseId: Int): Flow<Resource<CourseStatisticsModel>> = channelFlow {
        try {
            send(Resource.Loading())
            send(Resource.Success(repository.getCourseStatistics(courseId)))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
