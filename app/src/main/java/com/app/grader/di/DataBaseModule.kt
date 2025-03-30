package com.app.grader.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.app.grader.data.database.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "grader_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideCourseDao(db: AppDatabase) = db.getCourseDao()

    @Singleton
    @Provides
    fun provideGradeDao(db: AppDatabase) = db.getGradeDao()

    @Singleton
    @Provides
    fun provideSubGradeDao(db: AppDatabase) = db.getSubGradeDao()
}