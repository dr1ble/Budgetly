package shmr.budgetly.domain.model

/**
 * Определяет типы тактильных эффектов, доступных в приложении.
 */
enum class HapticEffect {
    /** Стандартный легкий клик. */
    CLICK,

    /** Короткая и легкая вибрация. */
    TICK,

    /** Двойной клик. */
    DOUBLE_CLICK
}