package com.app.grader.di

import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.infrastructure.appConfig.SharedPreferencesAppConfigRepository
import com.app.grader.infrastructure.database.repository.LocalStorageRepositoryImpl
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.repository.DistributionPlatformRepository
import com.app.grader.infrastructure.playStore.PlayStoreDistributionRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindLocalStorageRepository(
        localStorageRepositoryImpl: LocalStorageRepositoryImpl
    ): LocalStorageRepository

    @Singleton
    @Binds
    abstract fun bindDistributionPlatformRepository(
        playStoreDistributionRepositoryImp: PlayStoreDistributionRepositoryImp
    ): DistributionPlatformRepository

    @Singleton
    @Binds
    abstract fun bindAppConfigRepository(
        sharedPreferencesAppConfigRepository: SharedPreferencesAppConfigRepository
    ): AppConfigRepository
}