package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.screens.AccountScreen
import shmr.budgetly.ui.screens.ArticlesScreen
import shmr.budgetly.ui.screens.ExpensesScreen
import shmr.budgetly.ui.screens.IncomesScreen
import shmr.budgetly.ui.screens.SettingsScreen
import shmr.budgetly.ui.screens.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Splash.route,
        modifier = Modifier
    ) {
        composable(route = NavDestination.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(route = NavDestination.Expenses.route) {
            ExpensesScreen()
        }
        composable(route = NavDestination.Incomes.route) {
            IncomesScreen()
        }
        composable(route = NavDestination.Account.route) {
            AccountScreen()
        }
        composable(route = NavDestination.Articles.route) {
            ArticlesScreen()
        }
        composable(route = NavDestination.Settings.route) {
            SettingsScreen()
        }
    }
}