package com.app.grader.domain.usecase.review

import android.app.Activity
import com.app.grader.data.appConfig.AppConfigRepository
import com.app.grader.domain.model.Resource
import com.app.grader.service.review.InAppReviewHelper
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import java.util.concurrent.TimeUnit

class LaunchInAppReviewIfValidUseCaseTest {

    private lateinit var appConfigRepository: AppConfigRepository
    private lateinit var inAppReviewHelper: InAppReviewHelper
    private lateinit var useCase: LaunchInAppReviewIfValidUseCase
    private lateinit var activity: Activity

    private val MILLIS_7_DAYS = TimeUnit.DAYS.toMillis(7)
    private val MILLIS_1_DAY = TimeUnit.DAYS.toMillis(1)

    @Before
    fun setup() {
        appConfigRepository = mock()
        inAppReviewHelper = mock()
        activity = mock()
        useCase = LaunchInAppReviewIfValidUseCase(appConfigRepository, inAppReviewHelper)
    }

    @Test
    fun `no pide review si launchCount es menor a 10`() = runBlocking {
        val currentTime = System.currentTimeMillis()
        whenever(appConfigRepository.isReviewCompleted()).thenReturn(false)
        whenever(appConfigRepository.getFirstLaunchTime()).thenReturn(currentTime - MILLIS_7_DAYS - 1000)
        whenever(appConfigRepository.getLaunchCount()).thenReturn(5) // +1 en el use case = 6, menor a 10

        val results = useCase(activity).toList()

        assertTrue(results[1] is Resource.Success)
        assertEquals(false, (results[1] as Resource.Success).data)
        verify(inAppReviewHelper, never()).launchReviewFlow(any(), any(), any())
    }

    @Test
    fun `no pide review si dias desde firstLaunchTime menor a 7`() = runBlocking {
        val currentTime = System.currentTimeMillis()
        whenever(appConfigRepository.isReviewCompleted()).thenReturn(false)
        // 5 días atrás
        whenever(appConfigRepository.getFirstLaunchTime()).thenReturn(currentTime - TimeUnit.DAYS.toMillis(5))
        whenever(appConfigRepository.getLaunchCount()).thenReturn(15) // > 10

        val results = useCase(activity).toList()

        assertTrue(results[1] is Resource.Success)
        assertEquals(false, (results[1] as Resource.Success).data)
        verify(inAppReviewHelper, never()).launchReviewFlow(any(), any(), any())
    }

    @Test
    fun `no pide review si ya se pidio 3 veces`() = runBlocking {
        val currentTime = System.currentTimeMillis()
        whenever(appConfigRepository.isReviewCompleted()).thenReturn(false)
        whenever(appConfigRepository.getFirstLaunchTime()).thenReturn(currentTime - MILLIS_7_DAYS - 1000)
        whenever(appConfigRepository.getLaunchCount()).thenReturn(15)
        whenever(appConfigRepository.getReviewAskedCount()).thenReturn(3)

        val results = useCase(activity).toList()

        assertTrue(results[1] is Resource.Success)
        assertEquals(false, (results[1] as Resource.Success).data)
        verify(appConfigRepository).setReviewCompleted(true)
        verify(inAppReviewHelper, never()).launchReviewFlow(any(), any(), any())
    }

    @Test
    fun `no pide review si hace menos de 1 dia de la ultima vez`() = runBlocking {
        val currentTime = System.currentTimeMillis()
        whenever(appConfigRepository.isReviewCompleted()).thenReturn(false)
        whenever(appConfigRepository.getFirstLaunchTime()).thenReturn(currentTime - MILLIS_7_DAYS - 1000)
        whenever(appConfigRepository.getLaunchCount()).thenReturn(15)
        whenever(appConfigRepository.getReviewAskedCount()).thenReturn(1)
        // medio día atrás
        whenever(appConfigRepository.getLastReviewAskedTime()).thenReturn(currentTime - TimeUnit.HOURS.toMillis(12))

        val results = useCase(activity).toList()

        assertTrue(results[1] is Resource.Success)
        assertEquals(false, (results[1] as Resource.Success).data)
        verify(inAppReviewHelper, never()).launchReviewFlow(any(), any(), any())
    }

    @Test
    fun `pide review si cumple todas las condiciones`() = runBlocking {
        val currentTime = System.currentTimeMillis()
        whenever(appConfigRepository.isReviewCompleted()).thenReturn(false)
        whenever(appConfigRepository.getFirstLaunchTime()).thenReturn(currentTime - MILLIS_7_DAYS - 1000)
        whenever(appConfigRepository.getLaunchCount()).thenReturn(15) // +1 -> 16
        whenever(appConfigRepository.getReviewAskedCount()).thenReturn(1)
        // 2 días atrás
        whenever(appConfigRepository.getLastReviewAskedTime()).thenReturn(currentTime - TimeUnit.DAYS.toMillis(2))

        val results = useCase(activity).toList()

        assertTrue(results[1] is Resource.Success)
        assertEquals(true, (results[1] as Resource.Success).data)
        verify(inAppReviewHelper).launchReviewFlow(eq(activity), any(), any())
    }
}