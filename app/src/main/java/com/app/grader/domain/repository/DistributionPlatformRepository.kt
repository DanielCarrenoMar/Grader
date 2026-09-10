package com.app.grader.domain.repository

import android.app.Activity

interface DistributionPlatformRepository {
    suspend fun requestReview(activity: Activity): Result<Unit>
}