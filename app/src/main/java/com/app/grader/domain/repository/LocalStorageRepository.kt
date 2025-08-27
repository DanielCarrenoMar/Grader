package com.app.grader.domain.repository

import com.app.grader.core.appConfig.GradeFactory
import com.app.grader.domain.model.CourseModel
import com.app.grader.domain.model.GradeModel
import com.app.grader.domain.model.SemesterModel
import com.app.grader.domain.model.SubGradeModel
import com.app.grader.domain.types.Grade
import com.app.grader.domain.types.Percentage

interface LocalStorageRepository {
    suspend fun saveSemester(semesterModel: SemesterModel): Long
    suspend fun deleteAllSemesters(): Int
    suspend fun deleteSemesterFromId(semesterId: Int): Boolean
    suspend fun getAllSemesters(): List<SemesterModel>
    suspend fun getSemesterFromId(semesterId: Int): SemesterModel?
    suspend fun getAverageFromSemester(semesterId: Int?): Grade
    suspend fun getSizeOfSemesters(semesterId: Int?): Int
    suspend fun updateSemester(semesterModel: SemesterModel): Boolean

    suspend fun getAverageFromCourse(courseId:Int) : Grade
    suspend fun getTotalPercentageFromCourse(courseId:Int) : Percentage
    suspend fun saveCourse(courseModel: CourseModel): Long
    suspend fun deleteAllCourses() : Int
    suspend fun deleteAllCoursesFromSemesterId(semesterId: Int?): Int
    suspend fun deleteCourseFromId(courseId: Int): Boolean
    suspend fun getAllCourses(): List<CourseModel>
    suspend fun getCoursesFromSemester(semesterId: Int?): List<CourseModel>
    suspend fun getCourseFromId(courseId: Int):CourseModel?
    /**
     * Actualiza la informacion de una asignatura guiandose por el id
     */
    suspend fun updateCourse(courseModel: CourseModel): Boolean

    /**
     * Si existe devuelve un CoruseModel sino devuelve null
     */
    suspend fun getGradesFromCourse(courseId: Int): List<GradeModel>
    suspend fun getGradesFromSemester(semesterId: Int?): List<GradeModel>
    suspend fun saveGrade(gradeModel: GradeModel): Long
    suspend fun deleteAllGrades(): Int
    suspend fun deleteAllGradesFromCourseId(courseId: Int): Int
    suspend fun deleteGradeFromId(gradeId: Int): Boolean
    suspend fun getAllGrades(): List<GradeModel>
    suspend fun getGradeFromId(gradeId: Int): GradeModel?
    suspend fun updateGrade(gradeModel: GradeModel): Boolean

    suspend fun getSubGradesFromGrade(gradeId: Int): List<SubGradeModel>
    suspend fun saveSubGrade(subGradeModel: SubGradeModel): Long
    suspend fun deleteAllSubGrades(): Int
    suspend fun deleteAllSubGradesFromGradeId(gradeId: Int): Int
    suspend fun deleteSubGradeFromId(subGradeId: Int): Boolean
    suspend fun getSubGradeFromId(subGradeId: Int): SubGradeModel?
    suspend fun updateSubGrade(subGradeModel: SubGradeModel): Boolean
}