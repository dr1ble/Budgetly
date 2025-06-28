package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.screens.account.AccountScreen
import shmr.budgetly.ui.screens.articles.ArticlesScreen
import shmr.budgetly.ui.screens.expenses.ExpensesScreen
import shmr.budgetly.ui.screens.history.HistoryScreen
import shmr.budgetly.ui.screens.incomes.IncomesScreen
import shmr.budgetly.ui.screens.settings.SettingsScreen

/**
 * Основной навигационный граф приложения, определяющий переходы между экранами,
 * доступными из нижней навигационной панели.
 *
 * @param navController Контроллер навигации, управляющий стеком экранов.
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.BottomNav.Expenses.route,
        modifier = Modifier
    ) {
        composable(route = NavDestination.BottomNav.Expenses.route) {
            ExpensesScreen()
        }

        composable(route = NavDestination.BottomNav.Incomes.route) {
            IncomesScreen()
        }

        composable(route = NavDestination.BottomNav.Account.route) {
            AccountScreen()
        }

        composable(route = NavDestination.BottomNav.Articles.route) {
            ArticlesScreen()
        }

        composable(route = NavDestination.BottomNav.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = NavDestination.History.routeWithArgument,
            arguments = NavDestination.History.arguments
        ) {
            HistoryScreen()
        }
    }
}