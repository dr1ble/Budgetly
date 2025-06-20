package shmr.budgetly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseFAB
import shmr.budgetly.ui.components.BottomNavBar
import shmr.budgetly.ui.navigation.AppNavGraph
import shmr.budgetly.ui.navigation.NavDestination

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val fabRoutes = setOf(
        NavDestination.BottomNav.Expenses.route,
        NavDestination.BottomNav.Incomes.route,
        NavDestination.BottomNav.Account.route
    )

    Scaffold(
        topBar = {
            when (currentRoute) {
                NavDestination.BottomNav.Expenses.route -> AppTopBar(
                    title = stringResource(R.string.expenses_top_bar_title),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(NavDestination.History.route)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_top_bar_history),
                                contentDescription = stringResource(R.string.expenses_top_bar_action_description),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                NavDestination.BottomNav.Incomes.route -> AppTopBar(
                    title = stringResource(R.string.incomes_top_bar_title),
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(NavDestination.History.route)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_top_bar_history),
                                contentDescription = stringResource(R.string.expenses_top_bar_action_description),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                NavDestination.History.route -> AppTopBar(
                    title = "Моя история",
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    },
                    onNavigationClick = {
                        navController.popBackStack()
                    },
                    actions = {
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(
                                painterResource(R.drawable.ic_history_analyze),
                                contentDescription = "Анализ истории",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                NavDestination.BottomNav.Account.route -> AppTopBar(
                    title = stringResource(R.string.account_top_bar_title),
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_top_bar_edit),
                                contentDescription = stringResource(R.string.account_top_bar_action_description),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                NavDestination.BottomNav.Articles.route -> AppTopBar(title = stringResource(R.string.articles_top_bar_title))
                NavDestination.BottomNav.Settings.route -> AppTopBar(title = stringResource(R.string.settings_top_bar_title))
            }
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButton = {
            if (currentRoute in fabRoutes) {
                BaseFAB(
                    onClick = {
                    },
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            AppNavGraph(navController = navController)
        }
    }
}