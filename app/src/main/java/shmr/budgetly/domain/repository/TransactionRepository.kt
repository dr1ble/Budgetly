package shmr.budgetly.domain.repository

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.Result
import java.time.LocalDate

/**
 * Репозиторий для управления данными транзакций.
 */
interface TransactionRepository {
    suspend fun getTransactions(startDate: LocalDate, endDate: LocalDate): Result<List<Transaction>>
}