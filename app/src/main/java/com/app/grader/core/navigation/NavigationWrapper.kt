package com.app.grader.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.app.grader.ui.allGrades.AllGradesScreen
import com.app.grader.ui.config.ConfigScreen
import com.app.grader.ui.course.CourseScreen
import com.app.grader.ui.course.CourseViewModel
import com.app.grader.ui.grade.GradeScreen
import com.app.grader.ui.home.HomeScreen
import com.app.grader.ui.home.HomeViewModel

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
            HomeScreen(HomeViewModel() ,{
                myText -> navController.navigate(AllGrades(text = myText)) },
                { navController.navigatePop(Config) },
                { navController.navigate(Course) },
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

        composable<Course> {
            CourseScreen (
                { navController.navigatePop(Home) },
                { navController.navigate(Grade) }
            )
        }

        composable<Grade> {
            GradeScreen { navController.navigate(Course) }
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