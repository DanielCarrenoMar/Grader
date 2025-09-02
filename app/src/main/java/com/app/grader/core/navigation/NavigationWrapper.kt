package com.app.grader.core.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.app.grader.ui.pages.allGrades.AllGradesScreen
import com.app.grader.ui.pages.config.ConfigScreen
import com.app.grader.ui.pages.course.CourseScreen
import com.app.grader.ui.pages.editCourse.EditCourseScreen
import com.app.grader.ui.pages.editGrade.EditGradeScreen
import com.app.grader.ui.pages.editSemester.EditSemesterScreen
import com.app.grader.ui.pages.home.HomeScreen
import com.app.grader.ui.pages.record.RecordScreen
import com.app.grader.ui.pages.recordSemester.RecordSemesterScreen

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
    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = {fadeIn(animationSpec = tween(0))},
    ) {
        composable<Home> {
            HomeScreen(
                { navController.navigate(AllGrades) },
                { navController.navigate(Config) },
                { navController.navigate(Record) },
                {  courseId ->  navController.navigate(Course(courseId)) },
                {  semesterId, courseId ->  navController.navigate(EditCourse(semesterId, courseId)) },
                { semesterId, courseId, gradeId -> navController.navigate(EditGrade(semesterId, courseId, gradeId)) },
            )
        }

        composable<AllGrades> {
            AllGradesScreen (
                { navController.navigatePop(Home) },
                { navController.navigate(Config) },
                { navController.navigate(Record) },
                { semesterId, courseId, gradeId -> navController.navigate(EditGrade(semesterId, courseId, gradeId)) },
                { courseId -> navController.navigate(Course(courseId)) },
            )
        }

        composable<Config> {
            ConfigScreen(
                { navController.navigatePop(Home) },
                { navController.navigate(AllGrades) },
                { navController.navigate(Record) },
            )
        }

        composable<Record>{
            RecordScreen(
                { navController.navigatePop(Home) },
                { navController.navigate(AllGrades) },
                { navController.navigate(Config) },
                { semesterId -> navController.navigate(EditSemester(semesterId)) },
                { semesterId -> navController.navigate(RecordSemester(semesterId)) },
            )
        }

        composable<RecordSemester> (
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val recordSemester:RecordSemester = backStateEntry.toRoute()
            RecordSemesterScreen(
                recordSemester.semesterId,
                { navController.popBackStack() },
                { semesterId -> navController.navigate(EditSemester(semesterId)) },
                {  courseId ->  navController.navigate(Course(courseId)) },
                {  semesterId, courseId ->  navController.navigate(EditCourse(semesterId, courseId)) },
                { semesterId, courseId, gradeId -> navController.navigate(EditGrade(semesterId, courseId, gradeId)) },
            )
        }

        composable<Course>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val course:Course = backStateEntry.toRoute()
            CourseScreen (
                course.courseId,
                { navController.popBackStack() },
                {semesterId, courseId -> navController.navigate(EditCourse(semesterId, courseId)) },
                { semesterId, courseId, gradeId -> navController.navigate(EditGrade(semesterId, courseId, gradeId)) },
            )
        }

        composable<EditSemester>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val editSemester:EditSemester = backStateEntry.toRoute()
            EditSemesterScreen (
                editSemester.semesterId,
                { navController.popBackStack() }
            )
        }

        composable<EditCourse>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val editCourse:EditCourse = backStateEntry.toRoute()
            EditCourseScreen (
                editCourse.semesterId,
                editCourse.courseId,
                { navController.popBackStack() },
                { semesterId, courseId, gradeId -> navController.navigate(EditGrade(semesterId, courseId, gradeId)) },
            )
        }

        composable<EditGrade>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) {  backStateEntry ->
            val editGrade:EditGrade = backStateEntry.toRoute()
            EditGradeScreen (
                editGrade.semesterId,
                editGrade.courseId,
                editGrade.gradeId,
                { navController.popBackStack() })
        }
    }
}