package com.app.grader.domain.policy

import java.util.concurrent.TimeUnit
import javax.inject.Inject

class InAppReviewPolicy @Inject constructor() {

    companion object {
        private val MILLIS_IN_7_DAYS = TimeUnit.DAYS.toMillis(7)
        private val MILLIS_IN_1_DAY = TimeUnit.DAYS.toMillis(1)
        const val MIN_LAUNCHES = 10
        const val MAX_ASK_COUNT = 3
    }

    fun isEligibleForReview(
        isReviewCompleted: Boolean,
        firstLaunchTime: Long,
        launchCount: Int,
        askedCount: Int,
        lastAskedTime: Long,
        currentTime: Long
    ): Boolean {
        if (isReviewCompleted) return false

        val timeSinceFirstLaunch = currentTime - firstLaunchTime
        if (timeSinceFirstLaunch < MILLIS_IN_7_DAYS) return false

        if (launchCount < MIN_LAUNCHES) return false

        if (askedCount >= MAX_ASK_COUNT) return false

        val timeSinceLastAsked = currentTime - lastAskedTime
        if (lastAskedTime != 0L && timeSinceLastAsked < MILLIS_IN_1_DAY) return false

        return true
    }

    fun shouldMarkAsCompleted(askedCount: Int): Boolean {
        return askedCount >= MAX_ASK_COUNT
    }
}

