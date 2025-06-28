package shmr.budgetly.data.repository

import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.source.remote.RemoteDataSource
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.AccountRepository
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [TransactionRepository], отвечающая за получение данных о транзакциях.
 * Зависит от [AccountRepository] для получения ID текущего счета перед запросом транзакций.
 */
@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val accountRepository: AccountRepository
) : TransactionRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

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

    /**
     * Выполняет сетевой запрос для получения транзакций по ID счета и периоду.
     */
    private suspend fun fetchTransactionsForAccount(
        accountId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<Transaction>> {
        return safeApiCall {
            remoteDataSource.getTransactionsForPeriod(
                accountId = accountId,
                startDate = startDate.format(dateFormatter),
                endDate = endDate.format(dateFormatter)
            ).map { it.toDomainModel() }
        }
    }
}