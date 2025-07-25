package shmr.budgetly.ui.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.BudgetlyApp
import shmr.budgetly.ui.components.BaseFAB
import shmr.budgetly.ui.components.BottomNavBar
import shmr.budgetly.ui.navigation.AppNavGraph
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.navigation.PinScreenPurpose
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.util.LocalTopAppBarSetter

/**
 * Главный экран приложения, который содержит `Scaffold` с `TopAppBar`,
 * `BottomNavBar` и плавающей кнопкой `FAB`.
 * Конфигурация TopAppBar "поднимается" из дочерних экранов.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainScreen(
    onNavigateToPin: (purpose: PinScreenPurpose) -> Unit
) {
    val appComponent = (LocalContext.current.applicationContext as BudgetlyApp).appComponent
    val mainViewModel: MainViewModel = viewModel(factory = appComponent.viewModelFactory())

    val navController = rememberNavController()

    var topAppBar: @Composable () -> Unit by remember { mutableStateOf({}) }

    val topAppBarSetter: (@Composable () -> Unit) -> Unit = { newTopAppBar ->
        topAppBar = newTopAppBar
    }

    CompositionLocalProvider(LocalTopAppBarSetter provides topAppBarSetter) {
        Scaffold(
            topBar = { topAppBar() },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    modifier = Modifier.navigationBarsPadding(),
                    onTabClick = mainViewModel::performHapticFeedback
                )
            },
            floatingActionButton = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isIncomeScreen = currentRoute == Incomes::class.qualifiedName
                val isExpenseScreen = currentRoute == Expenses::class.qualifiedName

                if (isIncomeScreen || isExpenseScreen) {
                    BaseFAB(onClick = {
                        mainViewModel.performHapticFeedback()
                        val parentRoute =
                            if (isIncomeScreen) Incomes::class.qualifiedName!! else Expenses::class.qualifiedName!!
                        navController.navigate(
                            TransactionDetails(
                                isIncome = isIncomeScreen,
                                parentRoute = parentRoute
                            )
                        )
                    })
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavGraph(
                    navController = navController,
                    onNavigateToPin = onNavigateToPin
                )
            }
        }
    }
}