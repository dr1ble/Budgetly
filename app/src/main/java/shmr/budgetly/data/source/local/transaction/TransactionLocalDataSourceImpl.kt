package shmr.budgetly.data.source.local.transaction

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import shmr.budgetly.data.local.AppDatabase
import shmr.budgetly.data.local.dao.TransactionDao
import shmr.budgetly.data.local.model.TransactionEntity
import shmr.budgetly.data.local.model.TransactionWithCategory
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
class TransactionLocalDataSourceImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val database: AppDatabase
) : TransactionLocalDataSource {

    override fun getTransactionsForPeriod(
        startDate: String,
        endDate: String
    ): Flow<List<TransactionWithCategory>> =
        transactionDao.getTransactionsForPeriod(startDate, endDate)

    override fun getTransactionById(id: Int): Flow<TransactionWithCategory?> =
        transactionDao.getTransactionById(id)

    override suspend fun getDirtyTransactions(): List<TransactionEntity> =
        transactionDao.getDirtyTransactions()

    override suspend fun upsertTransaction(transaction: TransactionEntity) =
        transactionDao.upsertTransaction(transaction)

    override suspend fun upsertTransactions(transactions: List<TransactionEntity>) =
        transactionDao.upsertTransactions(transactions)

    override suspend fun markAsDeleted(id: Int, timestamp: Long) =
        transactionDao.markAsDeleted(id, timestamp)

    override suspend fun deleteAndInsert(old: TransactionEntity, new: TransactionEntity) {
        database.withTransaction {
            transactionDao.deleteTransaction(old)
            transactionDao.upsertTransaction(new)
        }
    }

    override suspend fun deleteById(id: Int) {
        transactionDao.deleteById(id)
    }
}