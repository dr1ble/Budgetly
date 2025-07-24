package shmr.budgetly.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import shmr.budgetly.R

/**
 * Базовый запечатанный интерфейс для всех навигационных маршрутов в приложении.
 * Каждый маршрут является отдельным `@Serializable` классом или объектом.
 */
@Serializable
sealed interface NavDestination

// --- Одиночные экраны ---

@Serializable
data object Splash : NavDestination

@Serializable
data object Main : NavDestination

@Serializable
data object EditAccount : NavDestination

@Serializable
data object ColorPicker : NavDestination

@Serializable
data object Haptics : NavDestination

// --- Экраны с аргументами ---

/**
 * Определяет цель, с которой открывается экран пин-кода.
 */
@Serializable
enum class PinScreenPurpose {
    /** Ввод пин-кода для разблокировки приложения. */
    UNLOCK,

    /** Установка нового или смена существующего пин-кода. */
    SETUP,

    /** Удаление существующего пин-кода. */
    DELETE
}

@Serializable
data class Pin(val purpose: PinScreenPurpose) : NavDestination

@Serializable
data class History(val parentRoute: String) : NavDestination

@Serializable
data class TransactionDetails(
    val transactionId: Int? = null,
    val isIncome: Boolean,
    val parentRoute: String
) : NavDestination

@Serializable
data class Analyze(
    val parentRoute: String,
    val startDate: Long,
    val endDate: Long
) : NavDestination

// --- Экраны нижней навигации ---

/**
 * Запечатанный интерфейс для элементов нижней навигационной панели.
 * Все элементы являются полноценными `NavDestination` и содержат
 * ресурсы для своего отображения в `BottomNavBar`.
 */
@Serializable
sealed interface BottomNavItem : NavDestination {
    @get:DrawableRes
    val icon: Int
    @get:StringRes
    val label: Int
}

@Serializable
data object Expenses : BottomNavItem {
    override val icon: Int = R.drawable.ic_bottom_nav_expenses
    override val label: Int = R.string.bottom_nav_label_expenses
}

@Serializable
data object Incomes : BottomNavItem {
    override val icon: Int = R.drawable.ic_bottom_nav_income
    override val label: Int = R.string.bottom_nav_label_incomes
}

@Serializable
data object Account : BottomNavItem {
    override val icon: Int = R.drawable.ic_bottom_nav_account
    override val label: Int = R.string.bottom_nav_label_score
}

@Serializable
data object Articles : BottomNavItem {
    override val icon: Int = R.drawable.ic_bottom_nav_articles
    override val label: Int = R.string.bottom_nav_label_articles
}

@Serializable
data object Settings : BottomNavItem {
    override val icon: Int = R.drawable.ic_bottom_nav_settings
    override val label: Int = R.string.bottom_nav_label_settigns
}

/** Удобный список для итерации по элементам нижней навигации. */
val bottomNavItems = listOf(Expenses, Incomes, Account, Articles, Settings)