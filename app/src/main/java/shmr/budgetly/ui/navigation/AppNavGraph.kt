package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.screens.account.AccountScreen
import shmr.budgetly.ui.screens.account.AccountViewModel
import shmr.budgetly.ui.screens.account.edit.EditAccountScreen
import shmr.budgetly.ui.screens.account.edit.EditAccountViewModel
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
    ) {
        composable(route = NavDestination.BottomNav.Expenses.route) {
            ExpensesScreen()
        }
        composable(route = NavDestination.BottomNav.Incomes.route) {
            IncomesScreen()
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
        composable(NavDestination.BottomNav.Account.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavDestination.BottomNav.Account.route)
            }
            val viewModel: AccountViewModel = hiltViewModel(parentEntry)
            AccountScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(NavDestination.EditAccount.route) { backStackEntry ->
            val viewModel: EditAccountViewModel = hiltViewModel(backStackEntry)
            EditAccountScreen(
                viewModel = viewModel,
                onSaveSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("account_updated", true)
                    navController.popBackStack()
                }
            )
        }
    }
}