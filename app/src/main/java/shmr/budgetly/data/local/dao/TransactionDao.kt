package shmr.budgetly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.TransactionEntity
import shmr.budgetly.data.local.model.TransactionWithCategory

/**
 * DAO для работы с сущностью TransactionEntity.
 */
@Dao
interface TransactionDao {
    /**
     * Вставляет или обновляет транзакции.
     */
    @Upsert
    suspend fun upsertTransactions(transactions: List<TransactionEntity>)

    /**
     * Вставляет или обновляет одну транзакцию.
     */
    @Upsert
    suspend fun upsertTransaction(transaction: TransactionEntity)

    /**
     * Удаляет транзакцию (жесткое удаление).
     */
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Int)

    /**
     * Удаляет транзакцию по сущности.
     */
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    /**
     * Получает все транзакции за период, которые не помечены как удаленные.
     * Использует JOIN для получения данных о категории.
     */
    @Transaction
    @Query(
        """
        SELECT * FROM transactions
        WHERE date(transactionDate, 'unixepoch') BETWEEN date(:startDate) AND date(:endDate)
        AND isDeleted = 0
    """
    )
    fun getTransactionsForPeriod(
        startDate: String,
        endDate: String
    ): Flow<List<TransactionWithCategory>>

    /**
     * Получает одну транзакцию по ID.
     */
    @Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Int): Flow<TransactionWithCategory?>

    /**
     * Помечает транзакцию как удаленную (мягкое удаление).
     */
    @Query("UPDATE transactions SET isDeleted = 1, isDirty = 1, lastUpdated = :timestamp WHERE id = :id")
    suspend fun markAsDeleted(id: Int, timestamp: Long)

    /**
     * Очищает таблицу транзакций.
     */
    @Query("DELETE FROM transactions")
    suspend fun clearAll()

    /**
     * Получает все "грязные" транзакции (измененные локально).
     */
    @Query("SELECT * FROM transactions WHERE isDirty = 1")
    suspend fun getDirtyTransactions(): List<TransactionEntity>
}