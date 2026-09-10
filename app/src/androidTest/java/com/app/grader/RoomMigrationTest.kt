package com.app.grader

import android.util.Log
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.grader.infrastructure.database.AppDatabase
import com.app.grader.infrastructure.database.MIGRATION_3_4
import com.app.grader.infrastructure.database.MIGRATION_4_5
import com.app.grader.infrastructure.database.MIGRATION_7_8
import com.app.grader.infrastructure.database.MIGRATION_8_9
import com.app.grader.infrastructure.database.MIGRATION_9_10

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import java.io.IOException
import kotlin.math.abs


@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    private val TEST_DB = "migration-test"
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    private fun dumpQuery(db: androidx.sqlite.db.SupportSQLiteDatabase, query: String, tag: String = "RoomMigrationTest") {
        db.query(query).use { c ->
            val cols = (0 until c.columnCount).map { c.getColumnName(it) }
            Log.d(tag, "Query: $query -> columns=$cols, rows=${c.count}")
            while (c.moveToNext()) {
                val row = (0 until c.columnCount).joinToString(", ") { idx ->
                    val v = when (c.getType(idx)) {
                        android.database.Cursor.FIELD_TYPE_INTEGER -> c.getLong(idx).toString()
                        android.database.Cursor.FIELD_TYPE_FLOAT -> c.getDouble(idx).toString()
                        android.database.Cursor.FIELD_TYPE_STRING -> c.getString(idx)
                        android.database.Cursor.FIELD_TYPE_NULL -> "NULL"
                        else -> "?"
                    }
                    "${cols[idx]}=$v"
                }
                Log.d(tag, row)
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To5() {

        var db = helper.createDatabase(TEST_DB, 3).apply {
            // Insert some data into the database.
            execSQL(
                "INSERT INTO grade (course_id, title, description, grade, percentage) " +
                        "VALUES (1, 'Math', 'Final Exam', 14, 100.0)"
            )
            execSQL(
                "INSERT INTO grade (course_id, title, description, grade, percentage) "
                        + "VALUES (1, 'Science', 'Midterm Exam', 20, 100.0)"
            )
            execSQL(
                "INSERT INTO sub_grade (grade_id, title, grade) " +
                        "VALUES (0, 'Math', 14)"
            )
            execSQL(
                "INSERT INTO course (id, title, uc) " +
                        "VALUES (1, 'Math', 1)"
            )
            execSQL(
                "INSERT INTO course (id, title, uc) " +
                        "VALUES (2, 'Science', 3)"
            )
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_3_4, MIGRATION_4_5)
    }

    @Test
    fun migrate3To5_convertsGradesPercentage() {
        val dbName = "migration-test-grades"

        var db = helper.createDatabase(dbName, 3).apply {
            // Insert some data into the database.
            execSQL(
                "INSERT INTO grade (course_id, title, description, grade, percentage) " +
                        "VALUES (1, 'Math', 'Final Exam', 14, 100.0)"
            )
            execSQL(
                "INSERT INTO grade (course_id, title, description, grade, percentage) "
                        + "VALUES (1, 'Science', 'Midterm Exam', 20, 100.0)"
            )
            execSQL(
                "INSERT INTO sub_grade (grade_id, title, grade) " +
                        "VALUES (0, 'Math', 14)"
            )
            execSQL(
                "INSERT INTO course (id, title, uc) " +
                        "VALUES (1, 'Math', 1)"
            )
            execSQL(
                "INSERT INTO course (id, title, uc) " +
                        "VALUES (2, 'Science', 3)"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(dbName, 5, true, MIGRATION_3_4, MIGRATION_4_5)

        // Validar conversion: percentage = grade/20
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Math'").use { c ->
            require(c.moveToFirst()); assert(abs(c.getDouble(0)) == 14/20.0)
        }
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Science'").use { c ->
            require(c.moveToFirst()); assert(abs(c.getDouble(0)) == 20/20.0)
        }

        dumpQuery(db, "SELECT id, title, description, grade_percentage, percentage FROM grade ORDER BY id")
    }

    @Test
    @Throws(IOException::class)
    fun migrate7To9_repairsOverweightedCourse() {
        val dbName = "migration-test-repair"

        // Crear DB a v7 con un curso y dos notas de 60% cada una (suma = 120 > 100).
        // La nota más reciente (created_at mayor) debe quedar en 60 - 20 = 40.
        var db = helper.createDatabase(dbName, 7).apply {
            execSQL("INSERT INTO type_grade (id, base_at, active) VALUES (1, 20, 1)")
            execSQL("INSERT INTO course (id, semester_id, type_grade_id, title, uc) VALUES (1, NULL, 1, 'Math', 1)")
            // Nota antigua (id=1, created_at=1000) — debe quedar intacta en 60.
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, percentage, created_at) " +
                        "VALUES (1, 1, 'Old Quiz', 'old', 50.0, 60.0, 1000)"
            )
            // Nota nueva (id=2, created_at=2000) — debe quedar en 40.
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, percentage, created_at) " +
                        "VALUES (2, 1, 'New Quiz', 'new', 70.0, 60.0, 2000)"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(dbName, 9, true, MIGRATION_7_8, MIGRATION_8_9)

        // Verifica la reparación: la nota vieja conserva 60 y la nueva baja a 40.
        db.query("SELECT id, weighting_percentage FROM grade ORDER BY id ASC").use { c ->
            require(c.moveToFirst()) { "expected first row" }
            assert(c.getInt(0) == 1)
            assert(abs(c.getDouble(1) - 60.0) < 1e-9) { "old grade should keep 60%, got ${c.getDouble(1)}" }
            require(c.moveToNext()) { "expected second row" }
            assert(c.getInt(0) == 2)
            assert(abs(c.getDouble(1) - 40.0) < 1e-9) { "new grade should be reduced to 40%, got ${c.getDouble(1)}" }
            assert(!c.moveToNext()) { "expected exactly two rows" }
        }

        // La suma agregada ahora debe ser exactamente 100.
        db.query("SELECT SUM(weighting_percentage) FROM grade WHERE course_id = 1").use { c ->
            require(c.moveToFirst())
            assert(abs(c.getDouble(0) - 100.0) < 1e-9) { "expected total 100, got ${c.getDouble(0)}" }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate7To9_keepsBlankGradePercentage() {
        val dbName = "migration-test-blank"

        // Crear DB a v7 con una nota vacia (grade_percentage = -1) y una nota normal.
        var db = helper.createDatabase(dbName, 7).apply {
            execSQL("INSERT INTO type_grade (id, base_at, active) VALUES (1, 20, 1)")
            execSQL("INSERT INTO course (id, semester_id, type_grade_id, title, uc) VALUES (1, NULL, 1, 'Math', 1)")
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, percentage, created_at) " +
                        "VALUES (1, 1, 'Empty', 'blank', -1.0, 0.0, 1000)"
            )
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, percentage, created_at) " +
                        "VALUES (2, 1, 'Full', 'full', 16.0, 100.0, 2000)"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(dbName, 9, true, MIGRATION_7_8, MIGRATION_8_9)

        // La nota vacia debe conservar grade_percentage = -1.
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Empty'").use { c ->
            require(c.moveToFirst()) { "expected blank grade row" }
            assert(c.getDouble(0) == -1.0) { "blank grade should keep -1, got ${c.getDouble(0)}" }
        }
        // La nota normal debe conservar su valor.
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Full'").use { c ->
            require(c.moveToFirst()) { "expected full grade row" }
            assert(abs(c.getDouble(0) - 16.0) < 1e-9) { "full grade should keep 16, got ${c.getDouble(0)}" }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate8To9_deletesNewestWhenSurplusExceedsIt() {
        val dbName = "migration-test-repair-delete"

        // Crea DB a v8 con tres notas de 60, 60, 10 (sum=130). La nueva (id=3, 10%)
        // es la más reciente: 10 - 30 < 0 → debe eliminarse. Luego 70 sigue > 100
        // y la siguiente más reciente (id=2, 60) baja a 60 - 20 = 40.
        // Resultado final: id=1=60, id=2=40, id=3 borrada. Suma = 100.
        var db = helper.createDatabase(dbName, 8).apply {
            execSQL("INSERT INTO type_grade (id, base_at, active) VALUES (1, 20, 1)")
            execSQL("INSERT INTO course (id, semester_id, type_grade_id, title, uc) VALUES (1, NULL, 1, 'Math', 1)")
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, weighting_percentage, created_at) " +
                        "VALUES (1, 1, 'Oldest', 'oldest', 50.0, 60.0, 1000)"
            )
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, weighting_percentage, created_at) " +
                        "VALUES (2, 1, 'Middle', 'middle', 70.0, 60.0, 2000)"
            )
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, weighting_percentage, created_at) " +
                        "VALUES (3, 1, 'Newest', 'newest', 80.0, 10.0, 3000)"
            )
            // Sub-grado en la nota más reciente (debe desaparecer cuando se borre).
            execSQL("INSERT INTO sub_grade (grade_id, title, grade_percentage) VALUES (3, 'sub', 80.0)")
            close()
        }

        db = helper.runMigrationsAndValidate(dbName, 9, true, MIGRATION_8_9)

        db.query("SELECT id, weighting_percentage FROM grade ORDER BY id ASC").use { c ->
            require(c.moveToFirst())
            assert(c.getInt(0) == 1)
            assert(abs(c.getDouble(1) - 60.0) < 1e-9)
            require(c.moveToNext())
            assert(c.getInt(0) == 2)
            assert(abs(c.getDouble(1) - 40.0) < 1e-9) { "middle should be 40, got ${c.getDouble(1)}" }
            assert(!c.moveToNext()) { "newest should have been deleted" }
        }

        // Verifica que la sub_grade de la nota eliminada también desapareció.
        db.query("SELECT COUNT(*) FROM sub_grade WHERE grade_id = 3").use { c ->
            require(c.moveToFirst())
            assert(c.getInt(0) == 0) { "sub_grade of deleted grade must be gone" }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate9To10_convertsBlankSentinelToNull() {
        val dbName = "migration-test-9to10"

        // Crear DB a v9 con una nota vacia (grade_percentage = -1.0), una nota
        // normal y una sub_grade vacia (-1.0).
        var db = helper.createDatabase(dbName, 9).apply {
            execSQL("INSERT INTO type_grade (id, base_at, active) VALUES (1, 20, 1)")
            execSQL("INSERT INTO course (id, semester_id, type_grade_id, title, uc) VALUES (1, NULL, 1, 'Math', 1)")
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, weighting_percentage, created_at) " +
                        "VALUES (1, 1, 'Empty', 'blank', -1.0, 0.0, 1000)"
            )
            execSQL(
                "INSERT INTO grade (id, course_id, title, description, grade_percentage, weighting_percentage, created_at) " +
                        "VALUES (2, 1, 'Full', 'full', 16.0, 100.0, 2000)"
            )
            execSQL(
                "INSERT INTO sub_grade (id, grade_id, title, grade_percentage) VALUES (1, 1, 'sub-blank', -1.0)"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(dbName, 10, true, MIGRATION_9_10)

        // grade_percentage debe ser NULLABLE en ambas tablas.
        db.query("PRAGMA table_info(grade)").use { c ->
            var nullable = false
            while (c.moveToNext()) {
                if (c.getString(1) == "grade_percentage" && c.getInt(3) == 0) nullable = true
            }
            assert(nullable) { "grade.grade_percentage must be nullable" }
        }
        db.query("PRAGMA table_info(sub_grade)").use { c ->
            var nullable = false
            while (c.moveToNext()) {
                if (c.getString(1) == "grade_percentage" && c.getInt(3) == 0) nullable = true
            }
            assert(nullable) { "sub_grade.grade_percentage must be nullable" }
        }

        // La nota vacia debe convertirse de -1.0 a NULL.
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Empty'").use { c ->
            require(c.moveToFirst()) { "expected blank grade row" }
            assert(c.isNull(0)) { "blank grade should be NULL, got ${c.getDouble(0)}" }
        }
        // La nota normal debe conservar su valor.
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Full'").use { c ->
            require(c.moveToFirst()) { "expected full grade row" }
            assert(abs(c.getDouble(0) - 16.0) < 1e-9) { "full grade should keep 16, got ${c.getDouble(0)}" }
        }
        // La sub_grade vacia tambien debe convertirse a NULL.
        db.query("SELECT grade_percentage FROM sub_grade WHERE id = 1").use { c ->
            require(c.moveToFirst()) { "expected sub_grade row" }
            assert(c.isNull(0)) { "blank sub_grade should be NULL, got ${c.getDouble(0)}" }
        }

        dumpQuery(db, "SELECT id, title, grade_percentage FROM grade ORDER BY id")
        dumpQuery(db, "SELECT id, title, grade_percentage FROM sub_grade ORDER BY id")
    }
}