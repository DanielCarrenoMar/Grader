package com.app.debugGrader.data.database

import androidx.room.withTransaction
import com.app.grader.data.database.AppDatabase
import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.data.database.entitites.GradeEntity
import com.app.grader.data.database.entitites.SemesterEntity
import com.app.grader.data.database.entitites.SubGradeEntity
import java.util.Date

data class DebugDatabaseSeedIds(
    val semesterIds: List<Long>,
    val courseIds: List<Long>,
    val gradeIds: List<Long>,
    val subGradeIds: List<Long>,
)

private data class DebugCourseSeed(
    val semesterIndex: Int?,
    val title: String,
    val uc: Int,
)

object DebugDatabaseSeeder {

    suspend fun seed(
        database: AppDatabase,
        clearExistingData: Boolean = true
    ): DebugDatabaseSeedIds {
        return database.withTransaction {
            if (clearExistingData) {
                clearAllData(database)
            }

            val semesterIds = insertSemesters(database)
            val courseIds = insertCourses(database, semesterIds)
            val gradeIds = insertGrades(database, courseIds)
            val subGradeIds = insertSubGrades(database, gradeIds)

            DebugDatabaseSeedIds(
                semesterIds = semesterIds,
                courseIds = courseIds,
                gradeIds = gradeIds,
                subGradeIds = subGradeIds,
            )
        }
    }

    private suspend fun clearAllData(database: AppDatabase) {
        database.getSubGradeDao().deleteAllSubGrades()
        database.getGradeDao().deleteAllGrades()
        database.getCourseDao().deleteAllCourses()
        database.getSemesterDao().deleteAllSemesters()

        database.getSubGradeDao().resetIncrementalSubGrade()
        database.getGradeDao().resetIncrementalGrade()
        database.getCourseDao().resetIncrementalCourse()
        database.getSemesterDao().resetIncrementalSemester()
    }

    private suspend fun insertSemesters(database: AppDatabase): List<Long> {
        val semesterDao = database.getSemesterDao()
        return listOf(
            semesterDao.insertSemester(SemesterEntity(title = "2025-1")),
            //semesterDao.insertSemester(SemesterEntity(title = "2025-2")),
        )
    }

    private suspend fun insertCourses(database: AppDatabase, semesterIds: List<Long>): List<Long> {
        val courseDao = database.getCourseDao()
        val coursePlan = listOf(
            DebugCourseSeed(0, "Matemáticas I", 4),
            DebugCourseSeed(0, "Programación Orientada a Objetos", 6),
            DebugCourseSeed(0, "Física General", 5),
            DebugCourseSeed(null, "Cálculo II", 4),
            DebugCourseSeed(null, "Estructuras de Datos", 6),
            DebugCourseSeed(null, "Bases de Datos", 5),
        )

        return coursePlan.map { seed ->
            courseDao.insertCourse(
                CourseEntity(
                    semesterId =  if (seed.semesterIndex !== null) semesterIds[seed.semesterIndex].toInt() else null,
                    title = seed.title,
                    uc = seed.uc,
                )
            )
        }
    }

    private suspend fun insertGrades(database: AppDatabase, courseIds: List<Long>): List<Long> {
        val gradeDao = database.getGradeDao()
        val gradePlan = listOf(
            listOf(82.0, 85.0, 90.0, 76.0, 88.0, 94.0, 80.0, 91.0, 87.0, 84.0),
            listOf(70.0, 74.0, 78.0, -1.0, 81.0, 73.0, 69.0, 85.0, 88.0, 90.0),
            listOf(95.0, 92.0, 89.0, 93.0, 90.0, 96.0, 94.0, 91.0, 97.0, 98.0),
            listOf(65.0, 72.0, 68.0, 74.0, 70.0, 75.0, 80.0, 77.0, 79.0, 83.0),
            listOf(88.0, 84.0, 90.0, 86.0, 82.0, 91.0, 87.0, 89.0, 93.0, 95.0),
            listOf(55.0, 60.0, 58.0, 62.0, -1.0, 65.0, 67.0, 70.0, 72.0, 75.0),
        )

        val gradeTitles = listOf(
            "Tarea 1", "Tarea 2", "Quiz 1", "Quiz 2", "Parcial 1",
            "Tarea 3", "Proyecto", "Parcial 2", "Laboratorio", "Examen final"
        )

        val gradeDescriptions = listOf(
            "Actividad de evaluación 1",
            "Actividad de evaluación 2",
            "Actividad de evaluación 3",
            "Actividad de evaluación 4",
            "Actividad de evaluación 5",
            "Actividad de evaluación 6",
            "Actividad de evaluación 7",
            "Actividad de evaluación 8",
            "Actividad de evaluación 9",
            "Actividad de evaluación 10",
        )

        val createdAtBase = 1735689600000L
        val dayMillis = 86_400_000L

        return courseIds.flatMapIndexed { courseIndex, courseId ->
            gradePlan[courseIndex].mapIndexed { gradeIndex, gradePercentage ->
                gradeDao.insertGrade(
                    GradeEntity(
                        courseId = courseId.toInt(),
                        title = gradeTitles[gradeIndex],
                        description = "${gradeDescriptions[gradeIndex]} - ${courseIndex + 1}",
                        gradePercentage = gradePercentage,
                        percentage = 10.0,
                        createdAt = Date(createdAtBase + ((courseIndex * 10L) + gradeIndex) * dayMillis),
                    )
                )
            }
        }
    }

    private suspend fun insertSubGrades(database: AppDatabase, gradeIds: List<Long>): List<Long> {
        val subGradeDao = database.getSubGradeDao()
        return listOf(
            subGradeDao.insertSubGrade(
                SubGradeEntity(
                    gradeId = gradeIds[4].toInt(),
                    title = "Teoría",
                    gradePercentage = 90.0,
                )
            ),
            subGradeDao.insertSubGrade(
                SubGradeEntity(
                    gradeId = gradeIds[4].toInt(),
                    title = "Problemas",
                    gradePercentage = 92.0,
                )
            ),
            subGradeDao.insertSubGrade(
                SubGradeEntity(
                    gradeId = gradeIds[14].toInt(),
                    title = "Exposición",
                    gradePercentage = 80.0,
                )
            ),
            subGradeDao.insertSubGrade(
                SubGradeEntity(
                    gradeId = gradeIds[14].toInt(),
                    title = "Documento final",
                    gradePercentage = 100.0,
                )
            ),
        )
    }
}
