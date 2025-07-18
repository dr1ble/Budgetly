package shmr.budgetly.domain.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

/**
 * Шина событий на базе SharedFlow для обмена сообщениями между
 * различными компонентами приложения.
 * Аннотация @AppScope гарантирует, что будет существовать только
 * один экземпляр шины на все приложение.
 */
@AppScope
class AppEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()

    /**
     * Отправляет событие в шину.
     */
    suspend fun postEvent(event: AppEvent) {
        _events.emit(event)
    }
}