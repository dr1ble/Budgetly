package shmr.budgetly.domain.repository

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Репозиторий для управления данными транзакций.
 * Абстрагирует доменный слой от источников данных.
 */
interface TransactionRepository {
    /**
     * Получает список транзакций за указанный период в виде потока данных.
     * @param startDate Начальная дата периода.
     * @param endDate Конечная дата периода.
     * @return [Flow] с [Result], содержащим список [Transaction] в случае успеха или ошибку.
     */
    fun getTransactions(startDate: LocalDate, endDate: LocalDate): Flow<Result<List<Transaction>>>

    /**
     * Получает одну транзакцию по ее ID в виде потока данных.
     */
    fun getTransactionById(id: Int): Flow<Result<Transaction>>

    /**
     * Создает новую транзакцию.
     */
    suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction>

    /**
     * Обновляет существующую транзакцию.
     */
    suspend fun updateTransaction(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction>

    /**
     * Удаляет транзакцию.
     */
    suspend fun deleteTransaction(id: Int): Result<Unit>

    /**
     * Запускает принудительную синхронизацию транзакций с сервером.
     * @return [Result.Success] в случае успеха, иначе [Result.Error].
     */
    suspend fun refreshTransactions(startDate: LocalDate, endDate: LocalDate): Result<Unit>
}