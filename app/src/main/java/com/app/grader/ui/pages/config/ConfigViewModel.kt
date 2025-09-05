package com.app.grader.ui.pages.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.grader.core.appConfig.AppConfig
import com.app.grader.core.appConfig.TypeGrade
import com.app.grader.core.appConfig.TypeTheme
import com.app.grader.data.database.entitites.CourseEntity
import com.app.grader.data.database.entitites.GradeEntity
import com.app.grader.data.database.entitites.SemesterEntity
import com.app.grader.data.database.entitites.SubGradeEntity
import com.app.grader.domain.model.Resource
import com.app.grader.domain.usecase.course.DeleteAllCoursesUseCase
import com.app.grader.domain.usecase.course.GetAllCoursesUseCase
import com.app.grader.domain.usecase.course.SaveCourseWithIdUseCase
import com.app.grader.domain.usecase.grade.DeleteAllGradesUseCase
import com.app.grader.domain.usecase.grade.GetAllGradesUseCase
import com.app.grader.domain.usecase.grade.SaveGradeWithIdUseCase
import com.app.grader.domain.usecase.semester.DeleteAllSemestersUseCase
import com.app.grader.domain.usecase.semester.GetAllSemestersUseCase
import com.app.grader.domain.usecase.semester.SaveSemesterWithIdUseCase
import com.app.grader.domain.usecase.subGrade.DeleteAllSubGradesUseCase
import com.app.grader.domain.usecase.subGrade.GetAllSubGradesUseCase
import com.app.grader.domain.usecase.subGrade.SaveSubGradeWithIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ConfigViewModel @Inject constructor(
    private val deleteAllGradesUseCase: DeleteAllGradesUseCase,
    private val deleteAllCoursesUseCase: DeleteAllCoursesUseCase,
    private val deleteAllSubGradesUseCase: DeleteAllSubGradesUseCase,
    private val deleteAllSemestersUseCase: DeleteAllSemestersUseCase,
    private val getAllSemestersUseCase: GetAllSemestersUseCase,
    private val getAllCoursesUseCase: GetAllCoursesUseCase,
    private val getAllGradesUseCase: GetAllGradesUseCase,
    private val getAllSubGradesUseCase: GetAllSubGradesUseCase,
    private val saveSemesterWithIdUseCase: SaveSemesterWithIdUseCase,
    private val saveCourseWithIdUseCase: SaveCourseWithIdUseCase,
    private val saveGradeWithIdUseCase: SaveGradeWithIdUseCase,
    private val saveSubGradeWithIdUseCase: SaveSubGradeWithIdUseCase,
    private val appConfig: AppConfig
) : ViewModel() {
    private val _typeTheme = mutableStateOf(appConfig.getTypeTheme())
    val typeTheme = _typeTheme
    private val _isRoundFinalCourseAverage = mutableStateOf(appConfig.isRoundFinalCourseAverage())
    val isRoundFinalCourseAverage = _isRoundFinalCourseAverage

    private val _typeGrade = mutableStateOf(appConfig.getTypeGrade())
    val typeGrade = _typeGrade

    fun restartApp(context: Context) {
        viewModelScope.launch {
            delay(1000L)
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(context.packageName)
            val componentName = intent?.component
            val mainIntent = Intent.makeRestartActivityTask(componentName)
            context.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)
        }
    }

    fun updateConfiguration() {
        _typeTheme.value = appConfig.getTypeTheme()
        _isRoundFinalCourseAverage.value = appConfig.isRoundFinalCourseAverage()
        _typeGrade.value = appConfig.getTypeGrade()
    }

    fun setTypeTheme(typeTheme: TypeTheme) {
        _typeTheme.value = typeTheme
        appConfig.setTypeTheme(typeTheme)
    }

    fun setRoundFinalCourseAverage(isRoundFinalCourseAverage: Boolean) {
        _isRoundFinalCourseAverage.value = isRoundFinalCourseAverage
        appConfig.setRoundFinalCourseAverage(isRoundFinalCourseAverage)
    }

    fun setTypeGrade(typeGrade: TypeGrade) {
        _typeGrade.value = typeGrade
        appConfig.setTypeGrade(typeGrade)
    }

    fun deleteAll() {
        viewModelScope.launch {
            var finished = true
            deleteAllSemestersUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e(
                            "ConfigViewModel",
                            "Error deleteAllSemestersUseCase: ${result.message}"
                        )
                    }
                }
            }
            deleteAllCoursesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllCoursesUseCase: ${result.message}")
                    }
                }
            }
            deleteAllGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e("ConfigViewModel", "Error deleteAllGradesUseCase: ${result.message}")
                    }
                }
            }
            deleteAllSubGradesUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {}
                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        finished = false
                        Log.e(
                            "ConfigViewModel",
                            "Error deleteAllSubGradesUseCase: ${result.message}"
                        )
                    }
                }
            }
            if (finished) {
                Log.i("ConfigViewModel", "All data deleted successfully")
            }
        }
    }

    fun backupDatabaseCVC(onCreate: (File) -> Unit) {
        viewModelScope.launch {
            val semesters = getAllSemestersUseCase().firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: emptyList()
            val courses = getAllCoursesUseCase().firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: emptyList()
            val grades = getAllGradesUseCase().firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: emptyList()
            val subGrades = getAllSubGradesUseCase().firstOrNull { it is Resource.Success }
                ?.let { (it as Resource.Success).data }
                ?: emptyList()

            if (semesters.isNotEmpty()){
                val fileName = "backup_semesters_${System.currentTimeMillis()}.csv"
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, fileName)

                FileOutputStream(file).bufferedWriter().use { writer ->
                    writer.write("id,title,average,size,weight\n")
                    semesters.forEach { semester ->
                        writer.write("${semester.id},${semester.title}\n")
                    }
                }
                onCreate(file)
            }
            if (courses.isNotEmpty()){
                val fileName = "backup_courses_${System.currentTimeMillis()}.csv"
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, fileName)

                FileOutputStream(file).bufferedWriter().use { writer ->
                    writer.write("id,semester_id,title,average,weight,color,position\n")
                    courses.forEach { course ->
                        writer.write("${course.id},${course.semesterId},${course.title},${course.uc}\n")
                    }
                }
                onCreate(file)
            }
            if (grades.isNotEmpty()){
                val fileName = "backup_grades_${System.currentTimeMillis()}.csv"
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, fileName)

                FileOutputStream(file).bufferedWriter().use { writer ->
                    writer.write("id,course_id,title,description,grade_percentage,percentage\n")
                    grades.forEach { grade ->
                        writer.write("${grade.id},${grade.courseId},${grade.title},${grade.description},${grade.grade.getGradePercentage()},${grade.percentage}\n")
                    }
                }
                onCreate(file)
            }
            if (subGrades.isNotEmpty()){
                val fileName = "backup_subgrades_${System.currentTimeMillis()}.csv"
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(path, fileName)

                FileOutputStream(file).bufferedWriter().use { writer ->
                    writer.write("id,grade_id,title,grade_percentage\n")
                    subGrades.forEach { subGrade ->
                        writer.write("${subGrade.id},${subGrade.gradeId},${subGrade.title},${subGrade.grade.getGradePercentage()}\n")
                    }
                }
                onCreate(file)
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        // Como fallback, intenta obtener el último segmento de la ruta.
        return uri.lastPathSegment
    }

    private suspend fun copyUriToTempFile(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) { // Realizar operaciones de archivo en el hilo de IO
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    Log.e("ConfigViewModel", "No se pudo obtener InputStream para la URI: $uri")
                    return@withContext null
                }

                // Intenta obtener un nombre de archivo original, o usa uno genérico
                val fileName = getFileNameFromUri(context, uri) ?: "temp_backup_${System.currentTimeMillis()}"
                val tempFile = File(context.cacheDir, fileName)

                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.use { input ->
                        input.copyTo(outputStream)
                    }
                }
                Log.d("ConfigViewModel", "URI copiada a archivo temporal: ${tempFile.absolutePath}")
                tempFile
            } catch (e: Exception) {
                Log.e("ConfigViewModel", "Error al copiar URI a archivo temporal: $uri", e)
                null
            }
        }
    }

    fun loadBackUps(context:Context ,uris: List<Uri>) {
        deleteAll()
        viewModelScope.launch {
            val tempFiles = mutableListOf<File>()
            var successfulCopies = 0

            uris.forEach { uri ->
                val tempFile = copyUriToTempFile(context, uri)
                if (tempFile != null) {
                    tempFiles.add(tempFile)
                    successfulCopies++
                } else {
                    // Opcional: Informar al usuario sobre el archivo que no se pudo copiar
                    Log.e("ConfigViewModel", "No se pudo copiar el contenido de la URI: $uri")
                }
            }

            if (tempFiles.isEmpty()) {
                Log.e("ConfigViewModel", "No se pudo copiar ningún archivo para procesar.")
                return@launch
            }

            tempFiles.forEach { file ->
                val name = file.name
                when {
                    name.startsWith("backup_semesters_") && name.endsWith(".csv") -> {
                        file.bufferedReader().useLines { lines ->
                            lines.drop(1).forEach { line ->
                                val tokens = line.split(",")
                                if (tokens.size >= 2) {
                                    val id = tokens[0].toIntOrNull() ?: 0
                                    val title = tokens[1]
                                    saveSemesterWithIdUseCase(
                                        SemesterEntity(
                                            id = id,
                                            title = title
                                        )
                                    ).firstOrNull { it is Resource.Success }
                                        ?: Log.e("ConfigViewModel", "Error importing semester: id=$id, title=$title")
                                }
                            }
                        }
                    }

                    name.startsWith("backup_courses_") && name.endsWith(".csv") -> {
                        file.bufferedReader().useLines { lines ->
                            lines.drop(1).forEach { line ->
                                val tokens = line.split(",")
                                if (tokens.size >= 4) {
                                    val id = tokens[0].toIntOrNull() ?: 0
                                    val semesterId = tokens[1].toIntOrNull()
                                    val title = tokens[2]
                                    val uc = tokens[3].toIntOrNull() ?: 0
                                    saveCourseWithIdUseCase(
                                        CourseEntity(
                                            id = id,
                                            semesterId = semesterId,
                                            title = title,
                                            uc = uc
                                        )
                                    ).firstOrNull { it is Resource.Success }
                                        ?: Log.e("ConfigViewModel", "Error importing course: id=$id, semesterId=$semesterId, title=$title, uc=$uc")
                                }
                            }
                        }
                    }

                    name.startsWith("backup_grades_") && name.endsWith(".csv") -> {
                        file.bufferedReader().useLines { lines ->
                            lines.drop(1).forEach { line ->
                                val tokens = line.split(",")
                                if (tokens.size >= 6) {
                                    val id = tokens[0].toIntOrNull() ?: 0
                                    val courseId = tokens[1].toIntOrNull() ?: 0
                                    val title = tokens[2]
                                    val description = tokens[3]
                                    val gradePercentage = tokens[4].toDoubleOrNull() ?: -1.0
                                    val percentage = tokens[5].toDoubleOrNull() ?: 0.0
                                    saveGradeWithIdUseCase(
                                        GradeEntity(
                                            id = id,
                                            courseId = courseId,
                                            title = title,
                                            description = description,
                                            gradePercentage = gradePercentage,
                                            percentage = percentage
                                        )
                                    ).firstOrNull { it is Resource.Success }
                                        ?: Log.e("ConfigViewModel", "Error importing grade: id=$id, courseId=$courseId, title=$title, description=$description, gradePercentage=$gradePercentage, percentage=$percentage")
                                }
                            }
                        }
                    }

                    name.startsWith("backup_subgrades_") && name.endsWith(".csv") -> {
                        file.bufferedReader().useLines { lines ->
                            lines.drop(1).forEach { line ->
                                val tokens = line.split(",")
                                if (tokens.size >= 4) {
                                    val id = tokens[0].toIntOrNull() ?: 0
                                    val gradeId = tokens[1].toIntOrNull() ?: 0
                                    val title = tokens[2]
                                    val gradePercentage = tokens[3].toDoubleOrNull() ?: -1.0
                                    saveSubGradeWithIdUseCase(
                                        SubGradeEntity(
                                            id = id,
                                            gradeId = gradeId,
                                            title = title,
                                            gradePercentage = gradePercentage
                                        )
                                    ).firstOrNull { it is Resource.Success }
                                        ?: Log.e("ConfigViewModel", "Error importing subGrade: id=$id, gradeId=$gradeId, title=$title, gradePercentage=$gradePercentage" )
                                }
                            }
                        }
                    }
                }
            }

            withContext(Dispatchers.IO) {
                tempFiles.forEach { file ->
                    if (file.exists()) {
                        file.delete()
                        Log.d("ConfigViewModel", "Archivo temporal eliminado: ${file.name}")
                    }
                }
            }
        }

    }

}