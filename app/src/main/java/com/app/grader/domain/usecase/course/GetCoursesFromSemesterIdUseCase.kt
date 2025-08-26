package com.app.grader.domain.usecase.course

import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetCoursesFromSemesterIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(semesterId: Int?): Flow<Resource<List<CourseModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getCoursesFromSemester(semesterId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}