package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import shmr.budgetly.R

/**
 * Определяет все возможные навигационные маршруты в приложении.
 * Каждый маршрут является отдельным `@Serializable` классом или объектом.
 */

/** Сплэш-экран. */
@Serializable
data object Splash

/** Главный экран с нижней навигацией. */
@Serializable
data object Main

@Serializable
data object EditAccount

/**
 * Экран истории, принимающий родительский маршрут в качестве аргумента.
 * @param parentRoute Строковое представление маршрута, с которого был совершен переход.
 */
@Serializable
data class History(val parentRoute: String)


/**
 * Базовый интерфейс для элементов BottomNav, чтобы у них были общие свойства.
 * @property route Сам @Serializable объект, который используется для навигации.
 * @property icon Ресурс иконки для вкладки.
 * @property label Ресурс строки для названия вкладки.
 */
interface BottomNavItem {
    val route: Any // Маршрут будет @Serializable объектом
    @get:DrawableRes
    val icon: Int
    @get:StringRes
    val label: Int
}

/** Пункт нижней навигации "Расходы". */
@Serializable
data object Expenses : BottomNavItem {
    override val route = this
    override val icon: Int = R.drawable.ic_bottom_nav_expenses
    override val label: Int = R.string.bottom_nav_label_expenses
}

/** Пункт нижней навигации "Доходы". */
@Serializable
data object Incomes : BottomNavItem {
    override val route = this
    override val icon: Int = R.drawable.ic_bottom_nav_income
    override val label: Int = R.string.bottom_nav_label_incomes
}

/** Пункт нижней навигации "Счет". */
@Serializable
data object Account : BottomNavItem {
    override val route = this
    override val icon: Int = R.drawable.ic_bottom_nav_account
    override val label: Int = R.string.bottom_nav_label_score
}

/** Пункт нижней навигации "Статьи". */
@Serializable
data object Articles : BottomNavItem {
    override val route = this
    override val icon: Int = R.drawable.ic_bottom_nav_articles
    override val label: Int = R.string.bottom_nav_label_articles
}

/** Пункт нижней навигации "Настройки". */
@Serializable
data object Settings : BottomNavItem {
    override val route = this
    override val icon: Int = R.drawable.ic_bottom_nav_settings
    override val label: Int = R.string.bottom_nav_label_settigns
}

/** Удобный список для итерации по элементам нижней навигации. */
val bottomNavItems = listOf(Expenses, Incomes, Account, Articles, Settings)