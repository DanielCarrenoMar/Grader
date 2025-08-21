package com.app.grader.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.grader.data.database.entitites.*
import com.app.grader.data.database.dao.*

@Database(
    version = 4,
    entities = [CourseEntity::class, GradeEntity::class, SubGradeEntity::class],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCourseDao(): CourseDao

    abstract fun getGradeDao(): GradeDao

    abstract fun getSubGradeDao(): SubGradeDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Grade: agregar percentage, copiar grade/20, eliminar grade
                db.execSQL("ALTER TABLE grade ADD COLUMN grade_percentage REAL NOT NULL DEFAULT 0.0")
                db.execSQL("UPDATE grade SET grade_percentage = grade / 20.0")
                db.execSQL(
                    """
                CREATE TABLE grade_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    course_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    grade_percentage REAL NOT NULL,
                    percentage REAL NOT NULL
                )
                """.trimIndent()
                )
                db.execSQL(
                    "INSERT INTO grade_new (id, course_id, title, description, grade_percentage, percentage) " +
                            "SELECT id, course_id, title, description, grade_percentage, percentage FROM grade"
                )
                db.execSQL("DROP TABLE grade")
                db.execSQL("ALTER TABLE grade_new RENAME TO grade")

                // SubGrade: agregar percentage, copiar grade/20, eliminar grade
                db.execSQL("ALTER TABLE sub_grade ADD COLUMN grade_percentage REAL NOT NULL DEFAULT 0.0")
                db.execSQL("UPDATE sub_grade SET grade_percentage = grade / 20.0")
                db.execSQL(
                    """
                CREATE TABLE sub_grade_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    grade_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    grade_percentage REAL NOT NULL
                )
                """.trimIndent()
                )
                db.execSQL(
                    "INSERT INTO sub_grade_new (id, grade_id, title, grade_percentage) " +
                            "SELECT id, grade_id, title, grade_percentage FROM sub_grade"
                )
                db.execSQL("DROP TABLE sub_grade")
                db.execSQL("ALTER TABLE sub_grade_new RENAME TO sub_grade")
            }
        }
    }
}