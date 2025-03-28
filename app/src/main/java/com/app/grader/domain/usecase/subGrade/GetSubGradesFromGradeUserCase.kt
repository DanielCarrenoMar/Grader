package com.app.grader.domain.usecase.subGrade

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetSubGradesFromGradeUserCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(gradeId: Int): Flow<Resource<List<SubGradeModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getSubGradesFromGrade(gradeId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}