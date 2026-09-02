package com.app.grader.infrastructure.playStore

import android.app.Activity
import com.app.grader.domain.repository.DistributionPlatformRepository
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class PlayStoreDistributionRepositoryImp @Inject constructor() : DistributionPlatformRepository {

    override suspend fun requestReview(activity: Activity): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    if (continuation.isActive) {
                        continuation.resume(Result.success(Unit))
                    }
                }
            } else {
                if (continuation.isActive) {
                    continuation.resume(Result.failure(task.exception ?: Exception("Unknown error in Play Store review flow")))
                }
            }
        }
    }
}