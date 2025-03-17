package com.app.grader.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.app.grader.ui.allGrades.*
import com.app.grader.ui.config.*
import com.app.grader.ui.course.*
import com.app.grader.ui.grade.*
import com.app.grader.ui.home.*
import com.app.grader.ui.editGrade.*
import com.app.grader.ui.editCourse.*

/**
 * Navega a una pantalla borrandola de la pila de pantallas
 */
fun NavController.navigatePop(route: Any) {
    this.navigate(route) {
        popUpTo(route) { inclusive = true }
    }
}

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                { myText -> navController.navigate(AllGrades(myText)) },
                { navController.navigatePop(Config) },
                {  courseId ->  navController.navigate(Course(courseId)) },
                {  courseId ->  navController.navigate(EditCourse(courseId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<AllGrades> { backStateEntry ->
            val allGrades:AllGrades = backStateEntry.toRoute()
            AllGradesScreen (
                allGrades.text ,
                { navController.navigatePop(Home) },
                { navController.navigatePop(Config) }
            )
            // Cuando se navega a una pantalla se crea una nueva
            // frente a la anterior, asi que para navegar a la home
            // borramos la pila de pantallas con popUpTo
        }

        composable<Config> {
            ConfigScreen(
                { navController.navigatePop(Home) },
                { myText -> navController.navigatePop(AllGrades(text = myText)) }
            )
        }

        composable<Course> { backStateEntry ->
            val course:Course = backStateEntry.toRoute()
            CourseScreen (
                course.courseId,
                { navController.popBackStack() },
                { gradeId -> navController.navigate(Grade(gradeId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<EditCourse> { backStateEntry ->
            val editCourse:EditCourse = backStateEntry.toRoute()
            EditCourseScreen (editCourse.courseId, { navController.popBackStack() })
        }

        composable<Grade> {  backStateEntry ->
            val grade:Grade = backStateEntry.toRoute()
            GradeScreen (grade.gradeId, { navController.popBackStack() })
        }

        composable<EditGrade> {  backStateEntry ->
            val editGrade:EditGrade = backStateEntry.toRoute()
            EditGradeScreen (editGrade.gradeId, editGrade.courseId, { navController.popBackStack() })
        }

        /*composable<Detail> { backStackEntry ->
            val detail: Detail = backStackEntry.toRoute()
            DetailScreen(name = detail.name,
                navigateBack = { navController.navigateUp() },
                navigateToSettings = {navController.navigate(Settings(it))})
        }

        composable<Settings>(typeMap = mapOf(typeOf<SettingsInfo>() to createNavType<SettingsInfo>())){ backStackEntry ->
            val settings:Settings = backStackEntry.toRoute()
            SettingsScreen(settings.info)
        }*/
    }
}