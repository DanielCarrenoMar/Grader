package com.app.grader.domain.usecase.review

import android.app.Activity
import com.app.grader.data.appConfig.AppConfigRepository
import com.app.grader.domain.model.Resource
import com.app.grader.service.review.InAppReviewHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LaunchInAppReviewIfValidUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val inAppReviewHelper: InAppReviewHelper
) {

    companion object {
        private val MILLIS_IN_7_DAYS = TimeUnit.DAYS.toMillis(7)
        private val MILLIS_IN_1_DAY = TimeUnit.DAYS.toMillis(1)
        private const val MIN_LAUNCHES = 10
        private const val MAX_ASK_COUNT = 3
    }

    operator fun invoke(activity: Activity): Flow<Resource<Boolean>> = channelFlow {
        try {
            send(Resource.Loading())
            if (appConfigRepository.isReviewCompleted()) {
                send(Resource.Success(false))
                return@channelFlow
            }

            val currentTime = System.currentTimeMillis()
            
            var firstLaunchTime = appConfigRepository.getFirstLaunchTime()
            if (firstLaunchTime == 0L) {
                firstLaunchTime = currentTime
                appConfigRepository.setFirstLaunchTime(currentTime)
            }

            val launchCount = appConfigRepository.getLaunchCount() + 1
            appConfigRepository.setLaunchCount(launchCount)

            val timeSinceFirstLaunch = currentTime - firstLaunchTime
            if (timeSinceFirstLaunch < MILLIS_IN_7_DAYS) {
                send(Resource.Success(false))
                return@channelFlow
            }

            if (launchCount < MIN_LAUNCHES) {
                send(Resource.Success(false))
                return@channelFlow
            }

            val askedCount = appConfigRepository.getReviewAskedCount()
            if (askedCount >= MAX_ASK_COUNT) {
                appConfigRepository.setReviewCompleted(true)
                send(Resource.Success(false))
                return@channelFlow
            }

            val lastAskedTime = appConfigRepository.getLastReviewAskedTime()
            val timeSinceLastAsked = currentTime - lastAskedTime
            if (lastAskedTime != 0L && timeSinceLastAsked < MILLIS_IN_1_DAY) {
                send(Resource.Success(false))
                return@channelFlow
            }

            send(Resource.Success(true))
            
            inAppReviewHelper.launchReviewFlow(
                activity = activity,
                onComplete = {
                    markReviewAsked()
                },
                onError = { }
            )

        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }

    private fun markReviewAsked() {
        val newCount = appConfigRepository.getReviewAskedCount() + 1
        appConfigRepository.setReviewAskedCount(newCount)
        appConfigRepository.setLastReviewAskedTime(System.currentTimeMillis())
    }
}