package shmr.budgetly.data.source.local.transaction

import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.model.TransactionEntity
import shmr.budgetly.data.local.model.TransactionWithCategory

interface TransactionLocalDataSource {
    fun getTransactionsForPeriod(
        startDate: String,
        endDate: String
    ): Flow<List<TransactionWithCategory>>

    fun getTransactionById(id: Int): Flow<TransactionWithCategory?>
    suspend fun getDirtyTransactions(): List<TransactionEntity>
    suspend fun upsertTransaction(transaction: TransactionEntity)
    suspend fun upsertTransactions(transactions: List<TransactionEntity>)
    suspend fun markAsDeleted(id: Int, timestamp: Long)
    suspend fun deleteAndInsert(old: TransactionEntity, new: TransactionEntity)
    suspend fun deleteById(id: Int)
}