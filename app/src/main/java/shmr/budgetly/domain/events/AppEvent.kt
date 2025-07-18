package shmr.budgetly.domain.events

/**
 * Определяет глобальные события, которые могут происходить в приложении.
 */
sealed class AppEvent {
    /**
     * Событие, сигнализирующее об обновлении данных счета.
     */
    data object AccountUpdated : AppEvent()

    /**
     * Событие, сигнализирующее о восстановлении интернет-соединения.
     */
    data object NetworkAvailable : AppEvent()
}