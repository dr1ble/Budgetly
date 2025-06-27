package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.util.Result

/**
 * Репозиторий для управления данными счетов.
 */
interface AccountRepository {
    suspend fun getMainAccount(): Result<Account>
}