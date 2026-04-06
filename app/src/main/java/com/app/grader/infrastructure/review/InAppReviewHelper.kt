package com.app.grader.infrastructure.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import javax.inject.Inject

class InAppReviewHelper @Inject constructor() {

    fun launchReviewFlow(
        activity: Activity,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()
        
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    onComplete()
                }
            } else {
                onError()
            }
        }
    }
}