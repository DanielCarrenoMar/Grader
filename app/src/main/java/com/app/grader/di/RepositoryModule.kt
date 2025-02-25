package com.app.grader.di

import com.app.grader.data.database.repository.LocalStorageRepositoryImpl
import com.app.grader.domain.repository.LocalStorageRepository
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
}