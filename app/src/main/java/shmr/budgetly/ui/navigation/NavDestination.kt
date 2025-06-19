package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import shmr.budgetly.R

sealed class NavDestination(val route: String) {

    data object Splash : NavDestination("splash")

    data object Main : NavDestination("main")

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