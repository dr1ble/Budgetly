package shmr.budgetly.data.repository

import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.network.dto.TransactionRequestDto
import shmr.budgetly.data.source.remote.transaction.TransactionRemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Реализация [TransactionRepository], отвечающая за получение данных о транзакциях.
 * Зависит от [AccountRepository] для получения ID текущего счета перед запросом транзакций.
 */
@AppScope
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: TransactionRemoteDataSource,
    private val accountRepository: AccountRepository
) : TransactionRepository {

    private val isoLocalDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    // Кастомный форматер, который гарантирует формат "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val apiDateTimeFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    override suspend fun getTransactions(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<Transaction>> {
        return when (val accountResult = accountRepository.getMainAccount()) {
            is Result.Success -> fetchTransactionsForAccount(
                accountResult.data.id,
                startDate,
                endDate
            )

            is Result.Error -> Result.Error(accountResult.error)
        }
    }

    override suspend fun getTransactionById(id: Int): Result<Transaction> {
        return safeApiCall {
            remoteDataSource.getTransactionById(id).toDomainModel()
        }
    }

    override suspend fun createTransaction(
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val request = TransactionRequestDto(
            accountId = accountId,
            categoryId = categoryId,
            amount = amount,
            transactionDate = transactionDate.atZone(ZoneOffset.UTC).format(apiDateTimeFormatter),
            comment = comment
        )
        val createResult = safeApiCall {
            remoteDataSource.createTransaction(request)
        }

        return when (createResult) {
            is Result.Success -> {
                getTransactionById(createResult.data.id)
            }

            is Result.Error -> createResult
        }
    }

    override suspend fun updateTransaction(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ): Result<Transaction> {
        val request = TransactionRequestDto(
            accountId = accountId,
            categoryId = categoryId,
            amount = amount,
            transactionDate = transactionDate.atZone(ZoneOffset.UTC).format(apiDateTimeFormatter),
            comment = comment
        )
        return safeApiCall {
            remoteDataSource.updateTransaction(id, request).toDomainModel()
        }
    }

    override suspend fun deleteTransaction(id: Int): Result<Unit> {
        return safeApiCall {
            remoteDataSource.deleteTransaction(id)
        }
    }

    private suspend fun fetchTransactionsForAccount(
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<Transaction>> {
        return safeApiCall {
            remoteDataSource.getTransactionsForPeriod(
                accountId = accountId,
                startDate = startDate.format(isoLocalDateFormatter),
                endDate = endDate.format(isoLocalDateFormatter)
            ).map { it.toDomainModel() }
        }
    }
}