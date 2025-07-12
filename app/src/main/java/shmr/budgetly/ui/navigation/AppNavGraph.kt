package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import shmr.budgetly.ui.di.LocalViewModelFactory
import shmr.budgetly.ui.screens.account.AccountScreen
import shmr.budgetly.ui.screens.account.edit.EditAccountScreen
import shmr.budgetly.ui.screens.articles.ArticlesScreen
import shmr.budgetly.ui.screens.expenses.ExpensesScreen
import shmr.budgetly.ui.screens.history.HistoryScreen
import shmr.budgetly.ui.screens.history.HistoryViewModel
import shmr.budgetly.ui.screens.incomes.IncomesScreen
import shmr.budgetly.ui.screens.settings.SettingsScreen

const val ACCOUNT_UPDATED_RESULT_KEY = "account_updated"

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val viewModelFactory = LocalViewModelFactory.current

    NavHost(
        navController = navController,
        startDestination = Expenses,
        modifier = modifier
    ) {
        composable<Expenses> {
            ExpensesScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navController = navController
            )
        }
        composable<Incomes> {
            IncomesScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navController = navController
            )
        }
        composable<Account> {
            AccountScreen(
                navController = navController,
                viewModel = viewModel(factory = viewModelFactory)
            )
        }
        composable<Articles> {
            ArticlesScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navController = navController
            )
        }
        composable<Settings> {
            SettingsScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navController = navController
            )
        }
        composable<History> { navBackStackEntry ->
            val historyViewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
            val navArgs: History = navBackStackEntry.toRoute()

            LaunchedEffect(Unit) {
                historyViewModel.init(navArgs)
            }
            HistoryScreen(
                viewModel = historyViewModel,
                navController = navController
            )
        }
        composable<EditAccount> {
            EditAccountScreen(
                viewModel = viewModel(factory = viewModelFactory),
                navController = navController,
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