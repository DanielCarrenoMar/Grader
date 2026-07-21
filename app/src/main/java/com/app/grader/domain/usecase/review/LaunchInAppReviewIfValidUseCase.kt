package com.app.grader.domain.usecase.review

import android.app.Activity
import com.app.grader.data.appConfig.AppConfigRepository
import com.app.grader.domain.model.Resource
import com.app.grader.domain.policy.InAppReviewPolicy
import com.app.grader.service.review.InAppReviewHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class LaunchInAppReviewIfValidUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository,
    private val inAppReviewHelper: InAppReviewHelper,
    private val inAppReviewPolicy: InAppReviewPolicy
) {

    operator fun invoke(activity: Activity): Flow<Resource<Boolean>> = channelFlow {
        try {
            send(Resource.Loading())

            val isCompleted = appConfigRepository.isReviewCompleted()

            val firstLaunchTime = appConfigRepository.getFirstLaunchTime()

            val launchCount = appConfigRepository.getLaunchCount() + 1
            appConfigRepository.setLaunchCount(launchCount)

            val askedCount = appConfigRepository.getReviewAskedCount()
            if (inAppReviewPolicy.shouldMarkAsCompleted(askedCount)) {
                appConfigRepository.setReviewCompleted(true)
            }

            val lastAskedTime = appConfigRepository.getLastReviewAskedTime()
            val currentTime = System.currentTimeMillis()

            val isEligible = inAppReviewPolicy.isEligibleForReview(
                isReviewCompleted = isCompleted,
                firstLaunchTime = firstLaunchTime,
                launchCount = launchCount,
                askedCount = askedCount,
                lastAskedTime = lastAskedTime,
                currentTime = currentTime
            )

            if (!isEligible) {
                send(Resource.Success(false))
                return@channelFlow
            }

            inAppReviewHelper.launchReviewFlow(
                activity = activity,
                onComplete = {
                    markReviewAsked()
                },
                onError = { }
            )

            send(Resource.Success(true))
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