package com.app.grader.data.database

import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.core.appConfig.toTypeGradeId
import com.app.grader.data.appConfig.AppConfigRepository

/**
 * Agrega tabla semester y la columna semester_id (nullable) en courses.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS semester (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(" ALTER TABLE course ADD COLUMN semester_id INTEGER")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

/**
 * Cambia las calificaciones base 20 a porcentaje para soportar tipos de calificación variables.
 * Grade: grade → grade_percentage
 * SubGrade: grade → grade_percentage
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
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
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

/**
 * Agrega la tabla type_grade y la relación de esta con course.
 * Se asigna a los cursos existentes el tipo de calificación actual guardado en AppConfig
 * o NUMERIC_20 por defecto.
 */
fun migration6To7(appContext: Context): Migration {
    return object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.beginTransaction()
            try {
                db.execSQL(
                    """
                    CREATE TABLE type_grade (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        base_at INTEGER,
                        active INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                seedTypeGrade(db)

                val currentTypeGradeId = runCatching {
                    AppConfigRepository(appContext.applicationContext).getTypeGrade().toTypeGradeId()
                }.getOrDefault(TypeGrade.NUMERIC_20.toTypeGradeId())

                db.execSQL(
                    """
                    CREATE TABLE course_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        semester_id INTEGER,
                        type_grade_id INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        uc INTEGER NOT NULL,
                        FOREIGN KEY(type_grade_id) REFERENCES type_grade(id) ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO course_new (id, semester_id, type_grade_id, title, uc)
                    SELECT id, semester_id, $currentTypeGradeId, title, uc FROM course
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE course")
                db.execSQL("ALTER TABLE course_new RENAME TO course")
                db.execSQL("CREATE INDEX index_course_type_grade_id ON course(type_grade_id)")
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }
}

/**
 * Migración 7 → 8:
 * - GradeEntity: agrega FK course_id → course(id) CASCADE, índice course_id,
 *   renombra columna `percentage` → `weighting_percentage`,
 *   añade CHECK grade_percentage BETWEEN 0 AND 100,
 *   añade CHECK weighting_percentage BETWEEN 0 AND 100,
 *   corrige defaultValue de created_at a timestamp actual.
 * - SubGradeEntity: agrega FK grade_id → grade(id) CASCADE, índice grade_id,
 *   añade CHECK grade_percentage BETWEEN 0 AND 100.
 * - CourseEntity: agrega FK semester_id → semester(id) CASCADE, índice semester_id.
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            // ── grade ─────────────────────────────────────────────────────────────
            // Recrear con FK, CHECK constraints y columna renombrada
            db.execSQL(
                """
                CREATE TABLE grade_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    course_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    grade_percentage REAL NOT NULL
                        CHECK(grade_percentage >= 0 AND grade_percentage <= 100),
                    weighting_percentage REAL NOT NULL
                        CHECK(weighting_percentage >= 0 AND weighting_percentage <= 100),
                    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')*1000),
                    FOREIGN KEY(course_id) REFERENCES course(id)
                        ON UPDATE CASCADE ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO grade_new (id, course_id, title, description, grade_percentage, weighting_percentage, created_at)
                SELECT id, course_id, title, description,
                       CASE WHEN grade_percentage < 0 THEN 0
                            WHEN grade_percentage > 100 THEN 100
                            ELSE grade_percentage END,
                       CASE WHEN percentage < 0 THEN 0
                            WHEN percentage > 100 THEN 100
                            ELSE percentage END,
                       CASE WHEN created_at IS NULL OR created_at = 0
                            THEN (strftime('%s','now')*1000)
                            ELSE created_at END
                FROM grade
                """.trimIndent()
            )
            db.execSQL("DROP TABLE grade")
            db.execSQL("ALTER TABLE grade_new RENAME TO grade")
            db.execSQL("CREATE INDEX index_grade_course_id ON grade(course_id)")

            // ── sub_grade ─────────────────────────────────────────────────────────
            db.execSQL(
                """
                CREATE TABLE sub_grade_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    grade_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    grade_percentage REAL NOT NULL
                        CHECK(grade_percentage >= 0 AND grade_percentage <= 100),
                    FOREIGN KEY(grade_id) REFERENCES grade(id)
                        ON UPDATE CASCADE ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO sub_grade_new (id, grade_id, title, grade_percentage)
                SELECT id, grade_id, title,
                       CASE WHEN grade_percentage < 0 THEN 0
                            WHEN grade_percentage > 100 THEN 100
                            ELSE grade_percentage END
                FROM sub_grade
                """.trimIndent()
            )
            db.execSQL("DROP TABLE sub_grade")
            db.execSQL("ALTER TABLE sub_grade_new RENAME TO sub_grade")
            db.execSQL("CREATE INDEX index_sub_grade_grade_id ON sub_grade(grade_id)")

            // ── course ────────────────────────────────────────────────────────────
            db.execSQL(
                """
                CREATE TABLE course_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    semester_id INTEGER,
                    type_grade_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    uc INTEGER NOT NULL,
                    FOREIGN KEY(type_grade_id) REFERENCES type_grade(id)
                        ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED,
                    FOREIGN KEY(semester_id) REFERENCES semester(id)
                        ON UPDATE CASCADE ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO course_new (id, semester_id, type_grade_id, title, uc)
                SELECT id, semester_id, type_grade_id, title, uc FROM course
                """.trimIndent()
            )
            db.execSQL("DROP TABLE course")
            db.execSQL("ALTER TABLE course_new RENAME TO course")
            db.execSQL("CREATE INDEX index_course_type_grade_id ON course(type_grade_id)")
            db.execSQL("CREATE INDEX index_course_semester_id ON course(semester_id)")

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
