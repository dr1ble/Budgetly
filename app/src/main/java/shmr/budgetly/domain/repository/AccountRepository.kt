package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными счетов.
 * Абстрагирует доменный слой от источников данных.
 */
interface AccountRepository {
    /**
     * Получает основной счет пользователя.
     * @return [Result] с [Account] в случае успеха или [DomainError] в случае ошибки.
     */
    suspend fun getMainAccount(): Result<Account>
}