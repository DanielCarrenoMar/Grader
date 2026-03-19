package com.app.grader.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.grader.data.database.converters.DateConverter
import com.app.grader.data.database.entitites.*
import com.app.grader.data.database.dao.*

@TypeConverters(DateConverter::class)
@Database(
    version = 6,
    entities = [SemesterEntity::class,CourseEntity::class, GradeEntity::class, SubGradeEntity::class],
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

    companion object {
        /**
         * Agrega tabla semester y agregar semester_id  que puede ser null en courses.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS semester (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    " ALTER TABLE course ADD COLUMN semester_id INTEGER"
                )
            }
        }

        /**
         * Se cambia las calificaciones base 20 a un porcentaje de esta para usar tipos de calificacion variables
         * Grade: grade -> grade_percentage
         * SubGrade: grade -> grade_percentage
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Grade
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

                // SubGrade
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