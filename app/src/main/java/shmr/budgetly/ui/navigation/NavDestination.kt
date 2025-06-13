package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import shmr.budgetly.R

sealed class NavDestination(
    val route: String,
    @DrawableRes val icon: Int,
    @StringRes val label: Int
) {
    data object Expenses : NavDestination(
        "expenses",
        R.drawable.ic_bottom_nav_expenses,
        R.string.bottom_nav_label_expenses
    )

    data object Incomes : NavDestination(
        "incomes",
        R.drawable.ic_bottom_nav_income,
        R.string.bottom_nav_label_incomes
    )

    data object Account :
        NavDestination("account", R.drawable.ic_bottom_nav_account, R.string.bottom_nav_label_score)

    data object Articles : NavDestination(
        "articles",
        R.drawable.ic_bottom_nav_articles,
        R.string.bottom_nav_label_articles
    )

    data object Settings : NavDestination(
        "settings",
        R.drawable.ic_bottom_nav_settings,
        R.string.bottom_nav_label_settigns
    )
}