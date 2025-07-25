package shmr.budgetly.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import shmr.budgetly.ui.navigation.Account
import shmr.budgetly.ui.navigation.BottomNavItem
import shmr.budgetly.ui.navigation.EditAccount
import shmr.budgetly.ui.navigation.History
import shmr.budgetly.ui.navigation.TransactionDetails
import shmr.budgetly.ui.navigation.bottomNavItems

/**
 * Компонент нижней навигационной панели (Bottom Navigation Bar).
 * Отображает вкладки для основных разделов приложения и управляет навигацией между ними.
 *
 * @param navController Контроллер навигации для выполнения переходов.
 * @param modifier Модификатор для настройки внешнего вида и поведения.
 * @param onTabClick Лямбда, вызываемая при нажатии на любую вкладку для воспроизведения вибрации.
 */
@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onTabClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        bottomNavItems.forEach { destination ->
            val isSelected = isDestinationSelected(destination, navBackStackEntry)
            NavigationBarItem(
                label = { Text(text = stringResource(id = destination.label)) },
                icon = {
                    Icon(
                        painterResource(destination.icon),
                        contentDescription = stringResource(destination.label)
                    )
                },
                selected = isSelected,
                onClick = {
                    onTabClick()
                    handleNavigation(navController, destination, isSelected)
                }
            )
        }
    }
}

/**
 * Определяет, выбрана ли данная вкладка навигации.
 * Учитывает случай, когда открыт дочерний экран, и подсвечивает его родительскую вкладку.
 */
private fun isDestinationSelected(
    destination: BottomNavItem,
    navBackStackEntry: androidx.navigation.NavBackStackEntry?
): Boolean {
    val currentRoute = navBackStackEntry?.destination?.route
    if (currentRoute == null) return false

    val historyRouteBase = History::class.qualifiedName!!
    val editAccountRouteBase = EditAccount::class.qualifiedName!!
    val transactionDetailsRouteBase = TransactionDetails::class.qualifiedName!!

    val activeTabRouteName: String? = when {
        currentRoute.startsWith(historyRouteBase) -> {
            navBackStackEntry.toRoute<History>().parentRoute
        }

        currentRoute.startsWith(transactionDetailsRouteBase) -> {
            navBackStackEntry.toRoute<TransactionDetails>().parentRoute
        }

        currentRoute == editAccountRouteBase -> {
            Account::class.qualifiedName
        }

        else -> currentRoute
    }

    return destination::class.qualifiedName == activeTabRouteName
}


/**
 * Обрабатывает навигацию при клике на элемент нижней панели.
 * Если вкладка уже выбрана, возвращается на ее стартовый экран.
 * Иначе, переключается на новую вкладку.
 */
private fun handleNavigation(
    navController: NavController,
    destination: BottomNavItem,
    isSelected: Boolean
) {
    val destinationRouteName = destination::class.qualifiedName!!

    if (isSelected) {
        navController.popBackStack(destinationRouteName, inclusive = false)
    } else {
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
}