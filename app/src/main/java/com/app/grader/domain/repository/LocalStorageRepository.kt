package com.app.grader.domain.repository

import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.data.database.entitites.GradeEntity
import com.app.grader.data.database.entitites.SemesterEntity
import com.app.grader.data.database.entitites.SubGradeEntity
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage
import java.io.File

interface LocalStorageRepository {
    suspend fun insertSemesterWithId(semesterEntity: SemesterEntity): Long
    suspend fun saveSemester(semesterModel: SemesterModel): Long
    suspend fun deleteAllSemesters(): Int
    suspend fun deleteSemesterById(semesterId: Int): Boolean
    suspend fun getAllSemesters(): List<SemesterModel>
    suspend fun getSemesterById(semesterId: Int): SemesterModel?
    suspend fun getAverageFromSemester(semesterId: Int?): Grade
    suspend fun getSizeOfSemesters(semesterId: Int?): Int
    suspend fun getWeightOfSemester(semesterId: Int?): Int
    suspend fun updateSemester(semesterModel: SemesterModel): Boolean
    suspend fun transferSemesterToSemester(semesterIdSender: Int?, semesterIdReceiver: Int?): Int

    suspend fun insertCourseWithId(courseEntity: CourseEntity): Long
    suspend fun getAverageFromCourse(courseId:Int) : Grade
    suspend fun getTotalPercentageFromCourse(courseId:Int) : Percentage
    suspend fun saveCourse(courseModel: CourseModel): Long
    suspend fun deleteAllCourses() : Int
    suspend fun deleteAllCoursesFromSemester(semesterId: Int?): Int
    suspend fun deleteCourseById(courseId: Int): Boolean
    suspend fun getAllCourses(): List<CourseModel>
    suspend fun getCoursesFromSemester(semesterId: Int?): List<CourseModel>
    suspend fun getCourseById(courseId: Int):CourseModel?
    /**
     * Actualiza la informacion de una asignatura guiandose por el id
     */
    suspend fun updateCourse(courseModel: CourseModel): Boolean

    suspend fun insertGradeWithId(gradeEntity: GradeEntity): Long
    /**
     * Si existe devuelve un CoruseModel sino devuelve null
     */
    suspend fun getGradesFromCourse(courseId: Int): List<GradeModel>
    suspend fun getGradesFromSemester(semesterId: Int?): List<GradeModel>
    suspend fun getGradesFromSemesterLessThan(semesterId: Int?): List<GradeModel>
    suspend fun saveGrade(gradeModel: GradeModel): Long
    suspend fun deleteAllGrades(): Int
    suspend fun deleteAllGradesFromCourse(courseId: Int): Int
    suspend fun deleteGradeById(gradeId: Int): Boolean
    suspend fun getAllGrades(): List<GradeModel>
    suspend fun getGradeById(gradeId: Int): GradeModel?
    suspend fun updateGrade(gradeModel: GradeModel): Boolean

    suspend fun insertSubGradeWithId(subGradeEntity: SubGradeEntity): Long
    suspend fun getAllSubGrades(): List<SubGradeModel>
    suspend fun getSubGradesFromGrade(gradeId: Int): List<SubGradeModel>
    suspend fun saveSubGrade(subGradeModel: SubGradeModel): Long
    suspend fun deleteAllSubGrades(): Int
    suspend fun deleteAllSubGradesFromGrade(gradeId: Int): Int
    suspend fun deleteSubGradeById(subGradeId: Int): Boolean
    suspend fun getSubGradeById(subGradeId: Int): SubGradeModel?
    suspend fun updateSubGrade(subGradeModel: SubGradeModel): Boolean
}