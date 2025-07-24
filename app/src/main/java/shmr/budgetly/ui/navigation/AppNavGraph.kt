package shmr.budgetly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import shmr.budgetly.ui.di.rememberScreenComponent
import shmr.budgetly.ui.screens.account.AccountScreen
import shmr.budgetly.ui.screens.account.edit.EditAccountScreen
import shmr.budgetly.ui.screens.analyze.AnalyzeScreen
import shmr.budgetly.ui.screens.articles.ArticlesScreen
import shmr.budgetly.ui.screens.expenses.ExpensesScreen
import shmr.budgetly.ui.screens.history.HistoryScreen
import shmr.budgetly.ui.screens.incomes.IncomesScreen
import shmr.budgetly.ui.screens.settings.SettingsScreen
import shmr.budgetly.ui.screens.settings.colorpicker.ColorPickerScreen
import shmr.budgetly.ui.screens.settings.haptics.HapticsScreen
import shmr.budgetly.ui.screens.settings.pincode.PinSettingsScreen
import shmr.budgetly.ui.screens.transactiondetails.TransactionDetailsScreen

const val ACCOUNT_UPDATED_RESULT_KEY = "account_updated"
const val TRANSACTION_SAVED_RESULT_KEY = "transaction_saved"

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onNavigateToPin: (purpose: PinScreenPurpose) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Expenses,
        modifier = modifier
    ) {
        composable<Expenses> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.expensesComponent().create() }
            ExpensesScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }
        composable<Incomes> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.incomesComponent().create() }
            IncomesScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }
        composable<Account> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.accountComponent().create() }
            AccountScreen(
                navController = navController,
                viewModel = viewModel(factory = component.viewModelFactory())
            )
        }
        composable<Articles> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.articlesComponent().create() }
            ArticlesScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }
        composable<Settings> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.settingsComponent().create() }
            SettingsScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }
        composable<History>(
        ) { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.historyComponent().create() }

            HistoryScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }
        composable<EditAccount> { navBackStackEntry ->
            val component =
                rememberScreenComponent(navBackStackEntry) { it.editAccountComponent().create() }
            EditAccountScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController,
                onSaveSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(ACCOUNT_UPDATED_RESULT_KEY, true)
                    navController.popBackStack()
                }
            )
        }
        composable<TransactionDetails> { navBackStackEntry ->
            val component = rememberScreenComponent(navBackStackEntry) {
                it.transactionDetailsComponent().create()
            }

            TransactionDetailsScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController,
                onSaveSuccess = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(TRANSACTION_SAVED_RESULT_KEY, true)
                    navController.popBackStack()
                }
            )
        }

        composable<Analyze> { navBackStackEntry ->
            val component = rememberScreenComponent(navBackStackEntry) {
                it.analyzeComponent().create()
            }
            AnalyzeScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }

        composable<ColorPicker> { navBackStackEntry ->
            val component = rememberScreenComponent(navBackStackEntry) {
                it.colorPickerComponent().create()
            }
            ColorPickerScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }

        composable<Haptics> { navBackStackEntry ->
            val component = rememberScreenComponent(navBackStackEntry) {
                it.hapticsComponent().create()
            }
            HapticsScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController
            )
        }

        composable<PinSettings> { navBackStackEntry ->
            val component = rememberScreenComponent(navBackStackEntry) {
                it.pinSettingsComponent().create()
            }
            PinSettingsScreen(
                viewModel = viewModel(factory = component.viewModelFactory()),
                navController = navController,
                onNavigateToPin = onNavigateToPin
            )
        }
    }
}