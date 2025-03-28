package com.app.grader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.grader.data.database.entitites.*
import com.app.grader.data.database.dao.*

@Database(entities = [CourseEntity::class, GradeEntity::class, SubGradeEntity::class], version = 3)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getCourseDao():CourseDao

    abstract fun getGradeDao():GradeDao

    abstract fun getSubGradeDao():SubGradeDao
}