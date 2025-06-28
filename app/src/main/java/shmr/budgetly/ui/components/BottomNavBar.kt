package shmr.budgetly.ui.components

import androidx.compose.material3.Icon
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
import shmr.budgetly.ui.navigation.NavDestination

/**
 * Компонент нижней навигационной панели (Bottom Navigation Bar).
 * Отображает вкладки для основных разделов приложения и управляет навигацией между ними.
 *
 * @param navController Контроллер навигации для выполнения переходов.
 * @param modifier Модификатор для настройки внешнего вида и поведения.
 */
@Composable
fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val destinations = listOf(
        NavDestination.BottomNav.Expenses,
        NavDestination.BottomNav.Incomes,
        NavDestination.BottomNav.Account,
        NavDestination.BottomNav.Articles,
        NavDestination.BottomNav.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val isSelected = isDestinationSelected(destination, currentRoute, navBackStackEntry)
            NavigationBarItem(
                label = { Text(text = stringResource(id = destination.label)) },
                icon = {
                    Icon(
                        painterResource(destination.icon),
                        contentDescription = stringResource(destination.label)
                    )
                },
                selected = isSelected,
                onClick = { handleNavigation(navController, destination, isSelected) }
            )
        }
    }
}

/**
 * Определяет, выбрана ли данная вкладка навигации.
 * Учитывает случай, когда открыт дочерний экран (например, История).
 */
private fun isDestinationSelected(
    destination: NavDestination.BottomNav,
    currentRoute: String?,
    navBackStackEntry: androidx.navigation.NavBackStackEntry?
): Boolean {
    return if (currentRoute == NavDestination.History.routeWithArgument) {
        navBackStackEntry?.arguments?.getString(NavDestination.History.PARENT_ROUTE_ARG) == destination.route
    } else {
        currentRoute == destination.route
    }
}

/**
 * Обрабатывает навигацию при клике на элемент нижней панели.
 * Если вкладка уже выбрана, возвращается на ее стартовый экран.
 * Иначе, переключается на новую вкладку.
 */
private fun handleNavigation(
    navController: NavController,
    destination: NavDestination.BottomNav,
    isSelected: Boolean
) {
    if (isSelected) {
        // Если вкладка уже выбрана (и мы на дочернем экране),
        // возвращаемся на ее главный экран.
        navController.popBackStack(destination.route, inclusive = false)
    } else {
        // Стандартное поведение: переключаемся на другую вкладку.
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = false
        }
    }
}