package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.screens.ArticlesScreen
import shmr.budgetly.ui.screens.ExpensesScreen
import shmr.budgetly.ui.screens.IncomesScreen
import shmr.budgetly.ui.screens.ScoreScreen
import shmr.budgetly.ui.screens.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Expenses.route,
        modifier = Modifier
    ) {
        composable(route = NavDestination.Expenses.route) {
            ExpensesScreen()
        }
        composable(route = NavDestination.Incomes.route) {
            IncomesScreen()
        }
        composable(route = NavDestination.Account.route) {
            ScoreScreen()
        }
        composable(route = NavDestination.Articles.route) {
            ArticlesScreen()
        }
        composable(route = NavDestination.Settings.route) {
            SettingsScreen()
        }
    }
}