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
import com.app.grader.ui.allGrades.*
import com.app.grader.ui.config.*
import com.app.grader.ui.course.*
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
                {  courseId ->  navController.navigate(Course(courseId)) },
                {  courseId ->  navController.navigate(EditCourse(courseId)) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<AllGrades> {
            AllGradesScreen (
                { navController.navigatePop(Home) },
                { navController.navigate(Config) },
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
        }

        composable<Config> {
            ConfigScreen(
                { navController.navigatePop(Home) },
                { navController.navigate(AllGrades) }
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
                { gradeId, courseId -> navController.navigate(EditGrade(gradeId, courseId)) },
            )
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