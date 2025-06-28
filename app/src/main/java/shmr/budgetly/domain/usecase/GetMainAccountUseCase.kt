package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.AccountRepository
import javax.inject.Inject

/**
 * UseCase для получения основного счета пользователя.
 * Инкапсулирует бизнес-логику получения счета, делегируя задачу репозиторию.
 */
class GetMainAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke() = repository.getMainAccount()
}