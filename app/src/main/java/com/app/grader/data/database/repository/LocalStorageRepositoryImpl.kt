package com.app.grader.data.database.repository

import android.util.Log
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.data.database.dao.CourseDao
import com.app.grader.data.database.dao.GradeDao
import com.app.grader.data.database.dao.SemesterDao
import com.app.grader.data.database.dao.SubGradeDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.model.toCourseEntity
import com.app.grader.domain.model.toCourseModel
import com.app.grader.domain.model.toGradeEntity
import com.app.grader.domain.model.toGradeModel
import com.app.grader.domain.model.toSemesterEntity
import com.app.grader.domain.model.toSemesterModel
import com.app.grader.domain.model.toSubGradeEntity
import com.app.grader.domain.model.toSubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao,
    private val courseDao: CourseDao,
    private val gradeDao: GradeDao,
    private val subGradeDao: SubGradeDao,
    private val gradeFactory: GradeFactory,
    private val appConfig: AppConfig
) : LocalStorageRepository {
    override suspend fun saveCourse(courseModel: CourseModel): Long {
        try {
            return courseDao.insertCourse(courseModel.toCourseEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateCourse(courseModel: CourseModel): Boolean {
        try {
            val result = courseDao.updateCourseById(
                courseModel.id,
                courseModel.title,
                courseModel.uc
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllCourses(): List<CourseModel> {
        try {
            return courseDao.getAllCourses().map { courseEntity ->
                courseEntity.toCourseModel(
                    getAverageFromCourse(courseEntity.id),
                    getTotalPercentageFromCourse(courseEntity.id)
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCoursesFromSemester(semesterId: Int?): List<CourseModel> {
        try {
            return courseDao.getAllCoursesFromSemesterId(semesterId).map { courseEntity ->
                courseEntity.toCourseModel(
                    getAverageFromCourse(courseEntity.id),
                    getTotalPercentageFromCourse(courseEntity.id)
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCourseById(courseId: Int): CourseModel? {
        try {
            val courseEntity = courseDao.getCourseFromId(courseId) ?: return null
            return CourseModel(
                    title = courseEntity.title,
                    uc = courseEntity.uc,
                    average = getAverageFromCourse(courseEntity.id),
                    totalPercentage = getTotalPercentageFromCourse(courseEntity.id),
                    id = courseEntity.id
                )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllCourses(): Int {
        try {
            courseDao.resetIncrementalCourse()
            return courseDao.deleteAllCourses()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllCoursesFromSemester(semesterId: Int?): Int {
        try {
            val courses = courseDao.getAllCoursesFromSemesterId(semesterId)
            courses.forEach { course ->
                deleteAllGradesFromCourse(course.id)
            }
            return courseDao.deleteAllCoursesFromSemesterId(semesterId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteCourseById(courseId: Int): Boolean {
        try {
            deleteAllGradesFromCourse(courseId)
            return courseDao.deleteCourseFromId(courseId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSemester(semesterModel: SemesterModel): Long {
        try {
            return semesterDao.insertSemester(semesterModel.toSemesterEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSemesters(): Int {
        try {
            semesterDao.resetIncrementalSemester()
            return semesterDao.deleteAllSemesters()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSemesterById(semesterId: Int): Boolean {
        try {
            deleteAllCoursesFromSemester(semesterId)
            return semesterDao.deleteSemesterById(semesterId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllSemesters(): List<SemesterModel> {
        try {
            return semesterDao.getAllSemesters().map { semesterEntity ->
                semesterEntity.toSemesterModel(
                    average = getAverageFromSemester(semesterEntity.id),
                    size = getSizeOfSemesters(semesterEntity.id),
                    weight = getWeightOfSemester(semesterEntity.id)
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSemesterById(semesterId: Int): SemesterModel? {
        try {
            val semesterEntity = semesterDao.getSemesterById(semesterId) ?: return null
            return semesterEntity.toSemesterModel(
                average = getAverageFromCourse(semesterId),
                size = getSizeOfSemesters(semesterId),
                weight = getWeightOfSemester(semesterId)
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAverageFromSemester(semesterId: Int?): Grade {
        try {
            val courses = getCoursesFromSemester(semesterId)
            if (courses.isEmpty()) return gradeFactory.instGrade()

            var totalGrades = 0.0
            var totalUC = 0

            courses.forEach { course ->
                if (course.average.isNotBlank()) {
                    val accumulatedPoints = course.average.getGrade() * (course.totalPercentage.getPercentage() / 100.0)
                    val pendingPoints = (100.0 - course.totalPercentage.getPercentage()) / 100.0 * course.average.getMax()
                    if (course.average.isFailValue(pendingPoints + accumulatedPoints)) {
                        return@forEach
                    }

                    val grade = if (appConfig.isRoundFinalCourseAverage()) course.average.getRoundedGrade()
                    else course.average.getGrade()

                    totalGrades += grade * course.uc
                    totalUC += course.uc
                }
            }
            val totalAverageGrade = if (totalUC != 0) gradeFactory.instGrade(totalGrades / totalUC)
            else gradeFactory.instGrade()

            return totalAverageGrade
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSizeOfSemesters(semesterId: Int?): Int {
        try {
            return semesterDao.getCoursesCountById(semesterId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getWeightOfSemester(semesterId: Int?): Int {
        try {
            return semesterDao.getSemesterUCSum(semesterId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateSemester(semesterModel: SemesterModel): Boolean {
        try {
            val result = semesterDao.updateSemesterById(
                semesterModel.id,
                semesterModel.title
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAverageFromCourse(courseId:Int): Grade {
        try {
            val averagePercentage = courseDao.getAverageFromCourse(courseId)
            if (averagePercentage == null) return gradeFactory.instGrade()
            return gradeFactory.instGradeFromPercentage(averagePercentage)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getTotalPercentageFromCourse(courseId: Int): Percentage {
        try {
            val totalPercentage = courseDao.getTotalPercentageFromCourse(courseId)
            if (totalPercentage == null) return Percentage()
            return Percentage(totalPercentage)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradesFromCourse(courseId: Int): List<GradeModel> {
        try {
            return gradeDao.getGradesFromCourseId(courseId).map { gradeEntity ->
                gradeEntity.toGradeModel(gradeFactory)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradesFromSemester(semesterId: Int?): List<GradeModel> {
        try {
            return gradeDao.getGradesFromSemesterId(semesterId).map { gradeEntity ->
                gradeEntity.toGradeModel(gradeFactory)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveGrade(gradeModel: GradeModel): Long {
        try {
            return gradeDao.insertGrade(gradeModel.toGradeEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGradesFromCourse(courseId: Int): Int {
        try {
            gradeDao.getGradesFromCourseId(courseId).forEach { grade ->
                subGradeDao.deleteAllSubGradesFromGradeId(grade.id)
            }
            return gradeDao.deleteAllGradesFromCourseId(courseId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllGrades(): Int {
        try {
            gradeDao.resetIncrementalGrade()
            return gradeDao.deleteAllGrades()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteGradeById(gradeId: Int): Boolean {
        try {
            subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
            return gradeDao.deleteGradeFromId(gradeId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllGrades(): List<GradeModel> {
        try {
            return gradeDao.getAllGrades().map { gradeEntity ->
                gradeEntity.toGradeModel(gradeFactory)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getGradeById(gradeId: Int): GradeModel? {
        try {
            val gradeEntity = gradeDao.getGradeFromId(gradeId) ?: return null
            return gradeEntity.toGradeModel(gradeFactory)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateGrade(gradeModel: GradeModel): Boolean {
        try {
            val result = gradeDao.updateGradeById(
                gradeModel.id,
                gradeModel.title,
                gradeModel.description,
                gradeModel.grade.getGradePercentage(),
                gradeModel.percentage.getPercentage()
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubGradesFromGrade(gradeId: Int): List<SubGradeModel> {
        try {
            return subGradeDao.getSubGradesFromGradeId(gradeId).map { subGradeEntity ->
                subGradeEntity.toSubGradeModel(gradeFactory)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSubGrade(subGradeModel: SubGradeModel): Long {
        try {
            return subGradeDao.insertSubGrade(subGradeModel.toSubGradeEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSubGrades(): Int {
        try {
            subGradeDao.resetIncrementalSubGrade()
            return subGradeDao.deleteAllSubGrades()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSubGradesFromGrade(gradeId: Int): Int {
        try {
            return subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSubGradeById(subGradeId: Int): Boolean {
        try {
            return subGradeDao.deleteSubGradeFromId(subGradeId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSubGradeById(subGradeId: Int): SubGradeModel? {
        try {
            val subGradeEntity = subGradeDao.getSubGradeFromId(subGradeId) ?: return null
            return subGradeEntity.toSubGradeModel(gradeFactory)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateSubGrade(subGradeModel: SubGradeModel): Boolean {
        try {
            val result = subGradeDao.updateSubGradeById(
                subGradeModel.id,
                subGradeModel.title,
                subGradeModel.grade.getGradePercentage(),
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }
}