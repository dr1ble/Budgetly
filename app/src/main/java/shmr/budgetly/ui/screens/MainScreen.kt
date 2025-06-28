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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseFAB
import shmr.budgetly.ui.components.BottomNavBar
import shmr.budgetly.ui.navigation.AppNavGraph
import shmr.budgetly.ui.navigation.NavDestination

/**
 * Главный экран приложения, который содержит `Scaffold` с `TopAppBar`,
 * `BottomNavBar` и плавающей кнопкой `FAB`.
 * Отвечает за размещение основного навигационного графа [AppNavGraph].
 */
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
            MainTopAppBar(
                currentRoute = currentRoute,
                navController = navController
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButton = {
            if (currentRoute in fabRoutes) {
                BaseFAB(onClick = { /* TODO */ })
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavGraph(navController = navController)
        }
    }
}

/**
 * Отображает [AppTopBar], сконфигурированный для текущего экрана.
 * @param currentRoute Текущий навигационный маршрут.
 * @param navController Контроллер навигации для выполнения действий.
 */
@Composable
private fun MainTopAppBar(currentRoute: String?, navController: NavController) {
    when (currentRoute) {
        NavDestination.BottomNav.Expenses.route -> AppTopBar(
            title = stringResource(R.string.expenses_top_bar_title),
            actions = { HistoryActionButton { navController.navigateToHistory(currentRoute) } }
        )

        NavDestination.BottomNav.Incomes.route -> AppTopBar(
            title = stringResource(R.string.incomes_top_bar_title),
            actions = { HistoryActionButton { navController.navigateToHistory(currentRoute) } }
        )

        NavDestination.History.routeWithArgument -> AppTopBar(
            title = stringResource(R.string.history_top_bar_title),
            navigationIcon = { BackArrowIcon() },
            onNavigationClick = { navController.popBackStack() },
            actions = { AnalyzeActionButton { /* TODO */ } }
        )

        NavDestination.BottomNav.Account.route -> AppTopBar(
            title = stringResource(R.string.account_top_bar_title),
            actions = { EditActionButton { /* TODO */ } }
        )

        NavDestination.BottomNav.Articles.route -> AppTopBar(
            title = stringResource(R.string.articles_top_bar_title)
        )

        NavDestination.BottomNav.Settings.route -> AppTopBar(
            title = stringResource(R.string.settings_top_bar_title)
        )
    }
}

private fun NavController.navigateToHistory(parentRoute: String) {
    navigate(NavDestination.History.buildRoute(parentRoute))
}

@Composable
private fun HistoryActionButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_top_bar_history),
            contentDescription = stringResource(R.string.history_action_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AnalyzeActionButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_history_analyze),
            contentDescription = stringResource(R.string.analyze_action_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditActionButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_top_bar_edit),
            contentDescription = stringResource(R.string.edit_action_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BackArrowIcon() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back_action_description)
    )
}