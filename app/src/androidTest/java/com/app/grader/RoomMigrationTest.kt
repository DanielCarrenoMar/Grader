package com.app.grader

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.grader.data.database.AppDatabase
import com.app.grader.data.database.AppDatabase.Companion.MIGRATION_3_4

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

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {

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
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, MIGRATION_3_4)
    }

    @Test
    fun migrate3To4_convertsGradesPercentage() {
        val dbName = "migration-test-grades"

        val db = helper.runMigrationsAndValidate(dbName, 4, true, MIGRATION_3_4)

        // Validar conversion: percentage = grade/20
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Math'").use { c ->
            require(c.moveToFirst()); assert(abs(c.getDouble(0)) == 14/20.0)
        }
        db.query("SELECT grade_percentage FROM grade WHERE title = 'Science'").use { c ->
            require(c.moveToFirst()); assert(abs(c.getDouble(0)) == 20/20.0)
        }
    }
}