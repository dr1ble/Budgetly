package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * UseCase для принудительного обновления данных основного счета с сервера.
 *
 * Эта операция является асинхронной (`suspend fun`) и инициирует фоновый запрос
 * к сети через [AccountRepository]. Результат запроса будет сохранен в локальной
 * базе данных, что, в свою очередь, вызовет обновление данных у всех подписчиков
 * (например, у [GetMainAccountUseCase]), использующих Flow.
 */
class RefreshMainAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    /**
     * Вызывает метод `refreshMainAccount` в репозитории для начала процесса обновления.
     */
    suspend operator fun invoke() {
        repository.refreshMainAccount()
    }
}