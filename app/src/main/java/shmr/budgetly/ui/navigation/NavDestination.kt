package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavType
import androidx.navigation.navArgument
import shmr.budgetly.R

sealed class NavDestination(val route: String) {

    data object Splash : NavDestination("splash")
    data object Main : NavDestination("main")

    data object History : NavDestination("history/{parentRoute}") {
        const val PARENT_ROUTE_ARG = "parentRoute"
        val routeWithArgument = "history/{$PARENT_ROUTE_ARG}"
        val arguments = listOf(
            navArgument(PARENT_ROUTE_ARG) { type = NavType.StringType }
        )

        fun buildRoute(parentRoute: String) = "history/$parentRoute"
    }


    sealed class BottomNav(
        route: String,
        @DrawableRes val icon: Int,
        @StringRes val label: Int
    ) : NavDestination(route) {

        data object Expenses : BottomNav(
            route = "expenses",
            icon = R.drawable.ic_bottom_nav_expenses,
            label = R.string.bottom_nav_label_expenses
        )

        data object Incomes : BottomNav(
            route = "incomes",
            icon = R.drawable.ic_bottom_nav_income,
            label = R.string.bottom_nav_label_incomes
        )

        data object Account : BottomNav(
            route = "account",
            icon = R.drawable.ic_bottom_nav_account,
            label = R.string.bottom_nav_label_score
        )

        data object Articles : BottomNav(
            route = "articles",
            icon = R.drawable.ic_bottom_nav_articles,
            label = R.string.bottom_nav_label_articles
        )

        data object Settings : BottomNav(
            route = "settings",
            icon = R.drawable.ic_bottom_nav_settings,
            label = R.string.bottom_nav_label_settigns
        )
    }
}