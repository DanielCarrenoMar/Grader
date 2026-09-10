package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateTypeGradeForAllCoursesUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(typeGradeId: Int): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            repository.updateTypeGradeForAllCourses(typeGradeId)
            send(Resource.Success(data = Unit))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}
