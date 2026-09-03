package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.Percentage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetCourseTotalGradesRemainingPercentageUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseId: Int): Flow<Resource<Percentage>> = channelFlow {
        try {
            send(Resource.Loading())
            val totalPercentage = repository.getTotalPercentageFromCourse(courseId)
            val remaining = Percentage(100.0 - totalPercentage.getPercentage())
            send(Resource.Success(data = remaining))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
