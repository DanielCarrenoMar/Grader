package com.app.grader.domain.usecase

import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAllCoursesUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(): Flow<Resource<Int>> = flow {
        try {
            emit(Resource.Loading())
            emit(
                Resource.Success(
                    data = repository.deleteAllCourses()
                )
            )
        } catch (e: Exception) {
            emit(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}