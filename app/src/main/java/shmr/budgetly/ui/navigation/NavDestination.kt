package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavType
import androidx.navigation.navArgument
import shmr.budgetly.R

/**
 * Определяет все возможные навигационные маршруты в приложении в виде
 * типизированного sealed-класса.
 * @param route Строковый идентификатор маршрута.
 */
sealed class NavDestination(val route: String) {

    /** Сплэш-экран. */
    data object Splash : NavDestination("splash")

    /** Главный экран с нижней навигацией. */
    data object Main : NavDestination("main")

    data object EditAccount : NavDestination("edit_account")

    /**
     * Экран истории, принимающий родительский маршрут в качестве аргумента.
     */
    data object History : NavDestination("history") {
        const val PARENT_ROUTE_ARG = "parentRoute"
        val routeWithArgument = "$route/{$PARENT_ROUTE_ARG}"
        val arguments = listOf(
            navArgument(PARENT_ROUTE_ARG) { type = NavType.StringType }
        )

        /** Строит маршрут к экрану истории, указывая, с какого экрана был совершен переход. */
        fun buildRoute(parentRoute: String) = "$route/$parentRoute"
    }

    /**
     * Определяет пункты нижней навигационной панели.
     * @param route Строковый идентификатор маршрута.
     * @param icon Ресурс иконки для вкладки.
     * @param label Ресурс строки для названия вкладки.
     */
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