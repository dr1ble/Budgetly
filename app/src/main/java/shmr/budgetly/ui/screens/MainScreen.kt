package shmr.budgetly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.ui.components.BaseFAB
import shmr.budgetly.ui.components.BottomNavBar
import shmr.budgetly.ui.navigation.AppNavGraph
import shmr.budgetly.ui.navigation.Expenses
import shmr.budgetly.ui.navigation.Incomes
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.util.LocalTopAppBarSetter

/**
 * Главный экран приложения, который содержит `Scaffold` с `TopAppBar`,
 * `BottomNavBar` и плавающей кнопкой `FAB`.
 * Конфигурация TopAppBar "поднимается" из дочерних экранов.
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    var topAppBar: @Composable () -> Unit by remember { mutableStateOf({}) }

    val topAppBarSetter: (@Composable () -> Unit) -> Unit = { newTopAppBar ->
        topAppBar = newTopAppBar
    }

    CompositionLocalProvider(LocalTopAppBarSetter provides topAppBarSetter) {
        androidx.compose.material3.Scaffold(
            topBar = {
                topAppBar()
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    modifier = Modifier.navigationBarsPadding()
                )
            },
            floatingActionButton = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isIncomeScreen = currentRoute == Incomes::class.qualifiedName
                val isExpenseScreen = currentRoute == Expenses::class.qualifiedName

                if (isIncomeScreen || isExpenseScreen) {
                    BaseFAB(onClick = {
                        navController.navigate(TransactionDetails(isIncome = isIncomeScreen))
                    })
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavGraph(navController = navController)
            }
        }
    }
}