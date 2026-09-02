package com.app.grader.infrastructure.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.grader.infrastructure.database.converters.DateConverter
import com.app.grader.infrastructure.database.entitites.*
import com.app.grader.infrastructure.database.dao.*

@TypeConverters(DateConverter::class)
@Database(
    version = 9,
    entities = [SemesterEntity::class, CourseEntity::class, GradeEntity::class, SubGradeEntity::class, TypeGradeEntity::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6, spec = AppDatabase.Migration5To6::class),
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getSemesterDao(): SemesterDao

    abstract fun getCourseDao(): CourseDao

    abstract fun getGradeDao(): GradeDao

    abstract fun getSubGradeDao(): SubGradeDao

    abstract fun getTypeGradeDao(): TypeGradeDao

    /**
     * Se agrega la columna created_at a grade para almacenar el tiempo de creación de cada calificación.
     */
    class Migration5To6 : AutoMigrationSpec {
        override fun onPostMigrate(db: SupportSQLiteDatabase) {
            // Asigna el tiempo actual a los registros que tengan 0 o NULL en created_at
            val currentTime = System.currentTimeMillis()
            db.execSQL("UPDATE grade SET created_at = $currentTime WHERE created_at IS NULL OR created_at = 0")
        }
    }
}