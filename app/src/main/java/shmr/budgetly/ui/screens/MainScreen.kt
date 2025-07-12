package shmr.budgetly.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shmr.budgetly.R
import shmr.budgetly.ui.components.AppTopBar
import shmr.budgetly.ui.components.BaseFAB
import shmr.budgetly.ui.components.BottomNavBar
import shmr.budgetly.ui.navigation.*
import shmr.budgetly.ui.screens.account.AccountViewModel
import shmr.budgetly.ui.screens.account.edit.EditAccountViewModel

/**
 * Главный экран приложения, который содержит `Scaffold` с `TopAppBar`,
 * `BottomNavBar` и плавающей кнопкой `FAB`.
 * Отвечает за размещение основного навигационного графа [AppNavGraph].
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            MainTopAppBar(navController = navController)
        },
        bottomBar = {
            // Логика видимости удалена. BottomNavBar теперь отображается всегда.
            BottomNavBar(
                navController = navController,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val fabRoutes = setOf(
                Expenses::class.qualifiedName,
                Incomes::class.qualifiedName
            )
            if (currentDestination?.route in fabRoutes) {
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
 * @param navController Контроллер навигации для выполнения действий.
 */
@Composable
private fun MainTopAppBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination

    when {
        destination?.hasRoute<Expenses>() ?: false -> AppTopBar(
            title = stringResource(R.string.expenses_top_bar_title),
            actions = { HistoryActionButton { navController.navigateToHistory(Expenses::class.qualifiedName) } }
        )

        destination?.hasRoute<Incomes>() ?: false -> AppTopBar(
            title = stringResource(R.string.incomes_top_bar_title),
            actions = { HistoryActionButton { navController.navigateToHistory(Incomes::class.qualifiedName) } }
        )

        destination?.hasRoute<History>() ?: false -> AppTopBar(
            title = stringResource(R.string.history_top_bar_title),
            navigationIcon = { BackArrowIcon() },
            onNavigationClick = { navController.popBackStack() },
            actions = { AnalyzeActionButton { /* TODO */ } }
        )

        destination?.hasRoute<Account>() ?: false -> {
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Account::class)
            }
            val accountViewModel: AccountViewModel = hiltViewModel(parentEntry)
            val accountUiState by accountViewModel.uiState.collectAsState()

            AppTopBar(
                title = accountUiState.account?.name
                    ?: stringResource(R.string.account_top_bar_title),
                actions = { EditActionButton { navController.navigate(EditAccount) } }
            )
        }

        destination?.hasRoute<EditAccount>() ?: false -> {
            val editAccountBackStackEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(EditAccount::class)
            }
            val viewModel: EditAccountViewModel = hiltViewModel(editAccountBackStackEntry)
            val uiState by viewModel.uiState.collectAsState()


            AppTopBar(
                title = stringResource(R.string.edit_account_top_bar_title),
                navigationIcon = { BackArrowIcon() },
                onNavigationClick = { navController.popBackStack() },
                actions = {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        IconButton(
                            onClick = viewModel::saveAccount,
                            enabled = uiState.isSaveEnabled
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_top_bar_confirm),
                                contentDescription = stringResource(R.string.save_action_description)
                            )
                        }
                    }
                }
            )
        }
        destination?.hasRoute<Articles>() ?: false -> AppTopBar(title = stringResource(R.string.articles_top_bar_title))
        destination?.hasRoute<Settings>() ?: false -> AppTopBar(title = stringResource(R.string.settings_top_bar_title))
    }
}

private fun NavController.navigateToHistory(parentRoute: String?) {
    if (parentRoute == null) return
    navigate(History(parentRoute = parentRoute))
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