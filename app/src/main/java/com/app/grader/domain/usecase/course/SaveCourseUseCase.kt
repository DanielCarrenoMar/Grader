package com.app.grader.domain.usecase.course

import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.Resource
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveCourseUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val appConfigRepository: AppConfigRepository
) {
    operator fun invoke(courseModel: CourseModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveCourse(
                courseModel.copy(
                    typeGradeId = resolveTypeGradeId(courseModel.typeGradeId)
                )
            )
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
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

    private fun resolveTypeGradeId(typeGradeId: Int): Int {
        if (typeGradeId > 0) return typeGradeId
        return appConfigRepository.getDefaultTypeGradeId()
    }
}