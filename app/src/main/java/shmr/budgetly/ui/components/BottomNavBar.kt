package shmr.budgetly.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier // ДОБАВЛЕНО
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import shmr.budgetly.ui.navigation.NavDestination

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

    NavigationBar(
        modifier = modifier
    ) {
        destinations.forEach { destination ->
            NavigationBarItem(
                label = { Text(text = stringResource(id = destination.label)) },
                icon = {
                    Icon(
                        painterResource(destination.icon),
                        contentDescription = stringResource(destination.label)
                    )
                },
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}