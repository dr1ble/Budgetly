package shmr.budgetly.domain.usecase

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

/**
 * UseCase для получения основного счета пользователя.
 * Инкапсулирует бизнес-логику получения счета, делегируя задачу репозиторию.
 */
class GetMainAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    operator fun invoke(): Flow<Result<Account>> = repository.getMainAccount()
}