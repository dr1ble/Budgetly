package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.screens.account.AccountScreen
import shmr.budgetly.ui.screens.account.edit.EditAccountScreen
import shmr.budgetly.ui.screens.articles.ArticlesScreen
import shmr.budgetly.ui.screens.expenses.ExpensesScreen
import shmr.budgetly.ui.screens.history.HistoryScreen
import shmr.budgetly.ui.screens.incomes.IncomesScreen
import shmr.budgetly.ui.screens.settings.SettingsScreen

const val ACCOUNT_UPDATED_RESULT_KEY = "account_updated"

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Expenses,
        modifier = modifier
    ) {
        composable<Expenses> {
            ExpensesScreen()
        }
        composable<Incomes> {
            IncomesScreen()
        }
        composable<Account> {
            AccountScreen(navController = navController, viewModel = hiltViewModel())
        }
        composable<Articles> {
            ArticlesScreen()
        }
        composable<Settings> {
            SettingsScreen()
        }
        composable<History> {
            HistoryScreen(viewModel = hiltViewModel())
        }
        composable<EditAccount> {
            EditAccountScreen(
                viewModel = hiltViewModel(),
                onSaveSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(ACCOUNT_UPDATED_RESULT_KEY, true)
                    navController.popBackStack()
                }
            )
        }
    }
}