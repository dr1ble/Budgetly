package shmr.budgetly.domain.repository

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными счетов.
 * Абстрагирует доменный слой от источников данных.
 */
interface AccountRepository {
    /**
     * Получает основной счет пользователя в виде потока данных.
     * @return [Flow] с [Result], содержащим [Account] в случае успеха или ошибку.
     */
    fun getMainAccount(): Flow<Result<Account>>

    /**
     * Обновляет основной счет пользователя.
     * @return [Result] с обновленным [Account] в случае успеха.
     */
    suspend fun updateAccount(name: String, balance: String, currency: String): Result<Account>

    /**
     * Запускает принудительную синхронизацию счета с сервером.
     */
    suspend fun refreshMainAccount()
}