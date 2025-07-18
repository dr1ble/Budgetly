package shmr.budgetly.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import shmr.budgetly.data.local.model.toDomainModel
import shmr.budgetly.data.local.model.toEntity
import shmr.budgetly.data.mapper.toEntity
import shmr.budgetly.data.source.local.transaction.TransactionLocalDataSource
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@AppScope
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val accountRepository: AccountRepository
) : TransactionRepository {

    private val isoLocalDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val apiDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    override fun getTransactions(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Result<List<Transaction>>> {
        return localDataSource.getTransactionsForPeriod(
            startDate.format(isoLocalDateFormatter),
            endDate.format(isoLocalDateFormatter)
        ).map { list ->
            Result.Success(list.map { it.transaction.toDomainModel(it.category.toDomainModel()) })
        }
    }

    override suspend fun refreshTransactions(startDate: LocalDate, endDate: LocalDate) {
        val accountResult = accountRepository.getMainAccount().first()
        if (accountResult is Result.Success) {
            val accountId = accountResult.data.id
            val remoteResult = safeApiCall {
                remoteDataSource.getTransactionsForPeriod(
                    accountId,
                    startDate.format(isoLocalDateFormatter),
                    endDate.format(isoLocalDateFormatter)
                )
            }
            if (remoteResult is Result.Success) {
                localDataSource.upsertTransactions(remoteResult.data.map { it.toEntity() })
            }
        }
    }


    override fun getTransactionById(id: Int): Flow<Result<Transaction>> {
        return localDataSource.getTransactionById(id).map { entity ->
            if (entity != null) {
                Result.Success(entity.transaction.toDomainModel(entity.category.toDomainModel()))
            } else {
                Result.Error(shmr.budgetly.domain.util.DomainError.Unknown(Exception("Transaction not found")))
            }
        }
    }

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val tempId = (System.currentTimeMillis() + Random.nextLong()).toInt() * -1
        val transaction = Transaction(
            id = tempId,
            // Создаем заглушку для категории
            category = shmr.budgetly.domain.entity.Category(
                id = categoryId,
                name = "",
                emoji = "",
                isIncome = false
            ),
            amount = amount,
            currency = "", // Будет взята из счета при синхронизации
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = accountId)
        localDataSource.upsertTransaction(entity)
        return getTransactionById(tempId).first()
    }

    override suspend fun updateTransaction(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val transaction = Transaction(
            id = id,
            // Создаем заглушку для категории
            category = shmr.budgetly.domain.entity.Category(
                id = categoryId,
                name = "",
                emoji = "",
                isIncome = false
            ),
            amount = amount,
            currency = "",
            transactionDate = transactionDate,
            comment = comment
        )
        val entity = transaction.toEntity(isDirty = true).copy(accountId = accountId)
        localDataSource.upsertTransaction(entity)
        return getTransactionById(id).first()
    }


    override suspend fun deleteTransaction(id: Int): Result<Unit> {
        localDataSource.markAsDeleted(id, System.currentTimeMillis())
        return Result.Success(Unit)
    }
}