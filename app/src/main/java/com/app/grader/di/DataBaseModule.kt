package com.app.grader.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.app.grader.data.database.AppDatabase
import com.app.grader.data.database.AppDatabase.Companion.MIGRATION_3_4
import com.app.grader.data.database.AppDatabase.Companion.MIGRATION_4_5

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "grader_database"
        ).addMigrations(MIGRATION_3_4, MIGRATION_4_5).build()
    }
    @Singleton
    @Provides
    fun provideSemesterDao(db: AppDatabase) = db.getSemesterDao()

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