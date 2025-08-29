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
                {  courseId ->  navController.navigate(EditCourse(courseId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<AllGrades> {
            AllGradesScreen (
                { navController.navigatePop(Home) },
                { navController.navigate(Config) },
                { navController.navigate(Record) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
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
            )
        }

        composable<RecordSemester> (
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val recordSemester:RecordSemester = backStateEntry.toRoute()
            HomeScreen(
                { navController.navigate(AllGrades) },
                { navController.navigate(Config) },
                { navController.navigate(Record) },
                {  courseId ->  navController.navigate(Course(courseId)) },
                {  courseId ->  navController.navigate(EditCourse(courseId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
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
                {courseId -> navController.navigate(EditCourse(courseId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<EditSemester>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val editSemester:EditSemester = backStateEntry.toRoute()
            EditSemesterScreen (editSemester.semesterId, { navController.popBackStack() })
        }

        composable<EditCourse>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) { backStateEntry ->
            val editCourse:EditCourse = backStateEntry.toRoute()
            EditCourseScreen (editCourse.courseId, { navController.popBackStack() })
        }

        composable<EditGrade>(
            enterTransition = { slideInHorizontally(initialOffsetX = { full -> full }, animationSpec = tween(400)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { full -> -full }, animationSpec = tween(400)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { full -> full }, animationSpec = tween(400)) }
        ) {  backStateEntry ->
            val editGrade:EditGrade = backStateEntry.toRoute()
            EditGradeScreen (editGrade.gradeId, editGrade.courseId, { navController.popBackStack() })
        }
    }
}