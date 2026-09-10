package com.app.grader.domain.usecase.typeGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetTypeGradeFromCourseIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(courseId: Int): Flow<Resource<TypeGradeModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val typeGrade = repository.getTypeGradeFromCourse(courseId)
                ?: throw IllegalStateException("Type grade not found")
            send(Resource.Success(data = typeGrade))
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}