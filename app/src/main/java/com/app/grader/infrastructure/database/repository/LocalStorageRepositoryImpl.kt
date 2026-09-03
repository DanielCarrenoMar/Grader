package com.app.grader.infrastructure.database.repository

import com.app.grader.domain.repository.AppConfigRepository
import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.infrastructure.database.dao.CourseDao
import com.app.grader.infrastructure.database.dao.GradeDao
import com.app.grader.infrastructure.database.dao.SemesterDao
import com.app.grader.infrastructure.database.dao.TypeGradeDao
import com.app.grader.infrastructure.database.dao.SubGradeDao
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.model.TypeGradeModel
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.model.toCourseEntity
import com.app.grader.domain.model.toCourseModel
import com.app.grader.domain.model.toGradeEntity
import com.app.grader.domain.model.toGradeModel
import com.app.grader.domain.model.toSemesterEntity
import com.app.grader.domain.model.toSemesterModel
import com.app.grader.domain.model.toTypeGradeEntity
import com.app.grader.domain.model.toTypeGradeModel
import com.app.grader.domain.model.toSubGradeEntity
import com.app.grader.domain.model.toSubGradeModel
import com.app.grader.domain.repository.LocalStorageRepository
import com.app.grader.domain.types.GradeValue
import com.app.grader.domain.types.Percentage
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val semesterDao: SemesterDao,
    private val courseDao: CourseDao,
    private val gradeDao: GradeDao,
    private val subGradeDao: SubGradeDao,
    private val typeGradeDao: TypeGradeDao,
    private val gradeFactory: GradeFactory,
    private val appConfigRepository: AppConfigRepository
) : LocalStorageRepository {

    override suspend fun saveCourse(courseModel: CourseModel): Long {
        return courseDao.insertCourse(
            courseModel.toCourseEntity()
        )
    }

    override suspend fun updateCourse(courseModel: CourseModel): Boolean {
        val result = courseDao.updateCourseById(
            courseModel.id,
            courseModel.title,
            courseModel.uc,
            courseModel.typeGradeId
        )
        return result == 1
    }

    override suspend fun getAllTypeGrades(): List<TypeGradeModel> {
        return typeGradeDao.getAllTypeGrades().map { typeGradeEntity ->
            typeGradeEntity.toTypeGradeModel()
        }
    }

    override suspend fun saveTypeGrade(typeGradeModel: TypeGradeModel): Long {
        return typeGradeDao.insertTypeGrade(typeGradeModel.toTypeGradeEntity())
    }

    override suspend fun deleteTypeGradeById(typeGradeId: Int): Boolean {
        return typeGradeDao.deleteTypeGradeFromId(typeGradeId) == 1
    }

    override suspend fun getAllCourses(): List<CourseModel> {
        return courseDao.getAllCourses().map { row ->
            row.toCourseModel(
                average = if (row.average != null) gradeFactory.instGradeFromPercentage(row.average) else gradeFactory.instGrade()
            )
        }
    }

    override suspend fun getCoursesFromSemester(semesterId: Int?): List<CourseModel> {
        return courseDao.getAllCoursesFromSemesterId(semesterId).map { row ->
            row.toCourseModel(
                average = if (row.average != null) gradeFactory.instGradeFromPercentage(row.average) else gradeFactory.instGrade()
            )
        }
    }

    override suspend fun getCourseById(courseId: Int): CourseModel? {
        val courseEntity = courseDao.getCourseFromId(courseId) ?: return null
        return courseEntity.toCourseModel(
            if (courseEntity.average != null) gradeFactory.instGradeFromPercentage(courseEntity.average) else gradeFactory.instGrade()
        )
    }

    override suspend fun deleteAllCourses(): Int {
        subGradeDao.deleteAllSubGrades()
        gradeDao.deleteAllGrades()
        courseDao.resetIncrementalCourse()
        return courseDao.deleteAllCourses()
    }

    override suspend fun deleteAllCoursesFromSemester(semesterId: Int?): Int {
        val courses = courseDao.getAllCoursesFromSemesterId(semesterId)
        courses.forEach { course ->
            deleteAllGradesFromCourse(course.id)
        }
        return courseDao.deleteAllCoursesFromSemesterId(semesterId)
    }

    override suspend fun deleteCourseById(courseId: Int): Boolean {
        deleteAllGradesFromCourse(courseId)
        return courseDao.deleteCourseFromId(courseId) == 1
    }

    override suspend fun saveSemester(semesterModel: SemesterModel): Long {
        return semesterDao.insertSemester(semesterModel.toSemesterEntity())
    }

    override suspend fun deleteAllSemesters(): Int {
        // Borrar sub_grades y grades de todos los cursos antes de borrar semestres
        subGradeDao.deleteAllSubGrades()
        gradeDao.deleteAllGrades()
        courseDao.deleteAllCourses()
        semesterDao.resetIncrementalSemester()
        return semesterDao.deleteAllSemesters()
    }

    override suspend fun deleteSemesterById(semesterId: Int): Boolean {
        deleteAllCoursesFromSemester(semesterId)
        return semesterDao.deleteSemesterById(semesterId) == 1
    }

    override suspend fun getAllSemesters(): List<SemesterModel> {
        return semesterDao.getAllSemesters().map { semesterEntity ->
            semesterEntity.toSemesterModel(
                average = getAverageFromSemester(semesterEntity.id),
                size = getSizeOfSemesters(semesterEntity.id),
                weight = getWeightOfSemester(semesterEntity.id)
            )
        }
    }

    override suspend fun getSemesterById(semesterId: Int): SemesterModel? {
        val semesterEntity = semesterDao.getSemesterById(semesterId) ?: return null
        return semesterEntity.toSemesterModel(
            average = getAverageFromCourse(semesterId),
            size = getSizeOfSemesters(semesterId),
            weight = getWeightOfSemester(semesterId)
        )
    }

    override suspend fun getAverageFromSemester(semesterId: Int?): GradeValue {
        val courses = getCoursesFromSemester(semesterId)
        if (courses.isEmpty()) return gradeFactory.instGrade()

        var totalGrades = 0.0
        var totalUC = 0

        courses.forEach { course ->
            if (course.average.isNotBlank()) {
                val accumulatedPoints = (course.average.getGrade() ?: 0.0) * (course.totalPercentage.getPercentage() / 100.0)
                val pendingPoints = (100.0 - course.totalPercentage.getPercentage()) / 100.0 * course.average.getMax()
                if (course.average.isFailValue(pendingPoints + accumulatedPoints)) {
                    return@forEach
                }

                val grade = if (appConfigRepository.isRoundFinalCourseAverage()) course.average.getRoundedGrade() ?: 0.0
                else course.average.getGrade() ?: 0.0

                totalGrades += grade * course.uc
                totalUC += course.uc
            }
        }
        return if (totalUC != 0) gradeFactory.instGrade(totalGrades / totalUC)
        else gradeFactory.instGrade()
    }

    override suspend fun getSizeOfSemesters(semesterId: Int?): Int {
        return semesterDao.getCoursesCountById(semesterId)
    }

    override suspend fun getWeightOfSemester(semesterId: Int?): Int {
        return semesterDao.getSemesterUCSum(semesterId)
    }

    override suspend fun updateSemester(semesterModel: SemesterModel): Boolean {
        val result = semesterDao.updateSemesterById(
            semesterModel.id,
            semesterModel.title
        )
        return result == 1
    }

    override suspend fun transferSemesterToSemester(semesterIdSender: Int?, semesterIdReceiver: Int?): Int {
        return semesterDao.transferSemesterToSemester(semesterIdSender, semesterIdReceiver)
    }

    override suspend fun getAverageFromCourse(courseId: Int): GradeValue {
        val averagePercentage = courseDao.getAverageFromCourse(courseId)
        if (averagePercentage == null) return gradeFactory.instGrade()
        return gradeFactory.instGradeFromPercentage(averagePercentage)
    }

    override suspend fun getTotalPercentageFromCourse(courseId: Int): Percentage {
        val totalPercentage =
            courseDao.getTotalPercentageFromCourse(courseId) ?: return Percentage()
        return Percentage(totalPercentage)
    }

    override suspend fun getGradesFromCourse(courseId: Int): List<GradeModel> {
        return gradeDao.getGradesFromCourseId(courseId).map { gradeEntity ->
            gradeEntity.toGradeModel(gradeFactory)
        }
    }

    override suspend fun getGradesFromSemester(semesterId: Int?): List<GradeModel> {
        return gradeDao.getGradesFromSemesterId(semesterId).map { gradeEntity ->
            gradeEntity.toGradeModel(gradeFactory)
        }
    }

    override suspend fun getGradesFromSemesterLessThan(semesterId: Int?): List<GradeModel> {
        return gradeDao.getGradesFromSemesterLessThanId(semesterId).map { gradeEntity ->
            gradeEntity.toGradeModel(gradeFactory)
        }
    }

    override suspend fun saveGrade(gradeModel: GradeModel): Long {
        return gradeDao.insertGrade(gradeModel.toGradeEntity())
    }

    override suspend fun deleteAllGradesFromCourse(courseId: Int): Int {
        gradeDao.getGradesFromCourseId(courseId).forEach { grade ->
            subGradeDao.deleteAllSubGradesFromGradeId(grade.id)
        }
        return gradeDao.deleteAllGradesFromCourseId(courseId)
    }

    override suspend fun deleteAllGrades(): Int {
        gradeDao.resetIncrementalGrade()
        return gradeDao.deleteAllGrades()
    }

    override suspend fun deleteGradeById(gradeId: Int): Boolean {
        subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
        return gradeDao.deleteGradeFromId(gradeId) == 1
    }

    override suspend fun getAllGrades(): List<GradeModel> {
        return gradeDao.getAllGrades().map { gradeEntity ->
            gradeEntity.toGradeModel(gradeFactory)
        }
    }

    override suspend fun getGradeById(gradeId: Int): GradeModel? {
        val gradeEntity = gradeDao.getGradeFromId(gradeId) ?: return null
        return gradeEntity.toGradeModel(gradeFactory)
    }

    override suspend fun updateGrade(gradeModel: GradeModel): Boolean {
        val result = gradeDao.updateGradeById(
            gradeModel.id,
            gradeModel.title,
            gradeModel.description,
            gradeModel.gradeValue.getGradePercentage(),
            gradeModel.percentage.getPercentage()
        )
        return result == 1
    }

    override suspend fun getSubGradesFromGrade(gradeId: Int): List<SubGradeModel> {
        return subGradeDao.getSubGradesFromGradeId(gradeId).map { subGradeEntity ->
            subGradeEntity.toSubGradeModel(gradeFactory)
        }
    }

    override suspend fun saveSubGrade(subGradeModel: SubGradeModel): Long {
        return subGradeDao.insertSubGrade(subGradeModel.toSubGradeEntity())
    }

    override suspend fun deleteAllSubGrades(): Int {
        subGradeDao.resetIncrementalSubGrade()
        return subGradeDao.deleteAllSubGrades()
    }

    override suspend fun deleteAllSubGradesFromGrade(gradeId: Int): Int {
        return subGradeDao.deleteAllSubGradesFromGradeId(gradeId)
    }

    override suspend fun deleteSubGradeById(subGradeId: Int): Boolean {
        return subGradeDao.deleteSubGradeFromId(subGradeId) == 1
    }

    override suspend fun getSubGradeById(subGradeId: Int): SubGradeModel? {
        val subGradeEntity = subGradeDao.getSubGradeFromId(subGradeId) ?: return null
        return subGradeEntity.toSubGradeModel(gradeFactory)
    }

    override suspend fun updateSubGrade(subGradeModel: SubGradeModel): Boolean {
        val result = subGradeDao.updateSubGradeById(
            subGradeModel.id,
            subGradeModel.title,
            subGradeModel.gradeValue.getGradePercentage(),
        )
        return result == 1
    }
}