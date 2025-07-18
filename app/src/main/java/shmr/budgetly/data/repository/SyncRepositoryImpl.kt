package shmr.budgetly.data.repository

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import shmr.budgetly.data.local.model.TransactionEntity
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.network.dto.TransactionRequestDto
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.repository.SyncRepository
import shmr.budgetly.domain.repository.UserPreferencesRepository
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AppScope
class SyncRepositoryImpl @Inject constructor(
    private val localDataSource: TransactionLocalDataSource,
    private val remoteDataSource: TransactionRemoteDataSource,
    private val userPreferencesRepository: UserPreferencesRepository
) : SyncRepository {

    private val apiDateTimeFormatter = DateTimeFormatter.ISO_INSTANT
    private val mutex = Mutex()

    override suspend fun syncData(): Boolean = mutex.withLock {
        Log.d("SyncRepository", "Starting data synchronization...")
        try {
            val dirtyTransactions = localDataSource.getDirtyTransactions()
            if (dirtyTransactions.isEmpty()) {
                Log.d("SyncRepository", "No dirty transactions to sync. Sync finished.")
                userPreferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
                return@withLock true
            }

            Log.d("SyncRepository", "Found ${dirtyTransactions.size} dirty transactions.")

            for (transaction in dirtyTransactions) {
                when {
                    transaction.isDeleted -> {
                        if (transaction.id > 0) {
                            Log.d(
                                "SyncRepository",
                                "Deleting transaction with id: ${transaction.id} on server."
                            )
                            try {
                                remoteDataSource.deleteTransaction(transaction.id)
                            } catch (e: HttpException) {
                                // Если получили 404, значит транзакция уже удалена на сервере.
                                // Считаем это успешным исходом и не прерываем синхронизацию.
                                if (e.code() == 404) {
                                    Log.w(
                                        "SyncRepository",
                                        "Transaction ${transaction.id} already deleted on server (404). Proceeding."
                                    )
                                } else {
                                    throw e
                                }
                            }
                        }
                        Log.d(
                            "SyncRepository",
                            "Permanently deleting transaction with id: ${transaction.id} from local DB."
                        )
                        localDataSource.deleteById(transaction.id)
                    }

                    transaction.id < 0 -> {
                        Log.d(
                            "SyncRepository",
                            "Creating new transaction (local id: ${transaction.id}) on server."
                        )
                        val request = createRequestDto(transaction)

                        val newTransactionDto = remoteDataSource.createTransaction(request)

                        val newSyncedEntity = newTransactionDto.toEntity(sourceEntity = transaction)

                        localDataSource.deleteAndInsert(transaction, newSyncedEntity)
                        Log.d(
                            "SyncRepository",
                            "Replaced local transaction ${transaction.id} with server one ${newSyncedEntity.id}."
                        )
                    }

                    else -> {
                        Log.d(
                            "SyncRepository",
                            "Updating transaction with id: ${transaction.id} on server."
                        )
                        val request = createRequestDto(transaction)
                        val updatedDto = remoteDataSource.updateTransaction(transaction.id, request)
                        localDataSource.upsertTransaction(updatedDto.toEntity(isDirty = false))
                    }
                }
            }

            userPreferencesRepository.updateLastSyncTimestamp(System.currentTimeMillis())
            Log.d("SyncRepository", "Sync finished successfully.")
            return@withLock true
        } catch (e: Exception) {
            Log.e("SyncRepository", "Sync failed", e)
            return@withLock false
        }
    }

    private fun createRequestDto(transaction: TransactionEntity): TransactionRequestDto {
        return TransactionRequestDto(
            accountId = transaction.accountId,
            categoryId = transaction.categoryId,
            amount = transaction.amount,
            transactionDate = apiDateTimeFormatter.format(
                transaction.transactionDate.toInstant(
                    ZoneOffset.UTC
                )
            ),
            comment = transaction.comment
        )
    }
}