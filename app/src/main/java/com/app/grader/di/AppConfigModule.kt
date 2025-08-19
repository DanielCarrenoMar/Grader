package com.app.grader.di

import android.content.Context
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.core.appConfig.GradeFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppConfigModule {
    @Provides
    @Singleton
    fun provideAppConfig(@ApplicationContext context: Context): AppConfig {
        return AppConfig(context)
    }

    @Provides
    @Singleton
    fun provideGradeFactory(@ApplicationContext context: Context): GradeFactory {
        return GradeFactory(context)
    }
}