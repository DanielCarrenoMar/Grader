package com.app.grader.domain.usecase.semester

import com.app.grader.domain.model.Resource
import com.app.grader.domain.model.SemesterStatisticsModel
import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetTotalSemesterStatisticsUseCase @Inject constructor(
    private val repository: LocalStorageRepository,
    private val appConfigRepository: AppConfigRepository,
) {
    operator fun invoke(): Flow<Resource<SemesterStatisticsModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val statistics = repository.getTotalSemestersStatistics(appConfigRepository.isRoundFinalCourseAverage())
            send(
                Resource.Success(
                    data = statistics
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}