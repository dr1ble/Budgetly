package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.util.Result
import javax.inject.Inject

/**
 * UseCase для обновления данных основного счета пользователя.
 */
class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    /**
     * @param name Новое название счета.
     * @param balance Новый баланс счета.
     * @param currency Новая валюта счета.
     * @return [Result] с обновленным [Account] в случае успеха.
     */
    suspend operator fun invoke(
        name: String,
        balance: String,
        currency: String
    ): Result<Account> {
        return repository.updateAccount(name, balance, currency)
    }
}