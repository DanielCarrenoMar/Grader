package com.app.grader.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.app.grader.infrastructure.database.AppDatabase
import com.app.grader.infrastructure.database.MIGRATION_3_4
import com.app.grader.infrastructure.database.MIGRATION_4_5
import com.app.grader.infrastructure.database.MIGRATION_7_8
import com.app.grader.infrastructure.database.MIGRATION_8_9
import com.app.grader.infrastructure.database.MIGRATION_9_10
import com.app.grader.infrastructure.database.migration6To7
import com.app.grader.infrastructure.database.seedTypeGrade

@Module
@InstallIn(SingletonComponent::class)
class DataBaseModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        val migration6To7 = migration6To7(appContext)
        val seedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                seedTypeGrade(db)
            }
        }
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "grader_database"
        ).addCallback(seedCallback)
            .addMigrations(MIGRATION_3_4, MIGRATION_4_5, migration6To7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
            .build()
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

    @Singleton
    @Provides
    fun provideTypeGradeDao(db: AppDatabase) = db.getTypeGradeDao()
}