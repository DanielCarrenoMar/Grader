package com.app.grader.di

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.infrastructure.appConfig.SharedPreferencesAppConfigRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppConfigModule {
    @Provides
    @Singleton
    fun provideGradeFactory(appConfigRepository: SharedPreferencesAppConfigRepository): GradeFactory {
        return GradeFactory(appConfigRepository)
    }
}