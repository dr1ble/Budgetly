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

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val accountRepository: AccountRepository
) : TransactionRepository {

    override suspend fun getTransactions(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<Transaction>> {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val startDateString = startDate.format(formatter)
        val endDateString = endDate.format(formatter)

        return when (val accountResult = accountRepository.getMainAccount()) {
            is Result.Success -> {
                val accountId = accountResult.data.id
                safeApiCall {
                    remoteDataSource.getTransactionsForPeriod(
                        accountId,
                        startDateString,
                        endDateString
                    )
                        .map { it.toDomainModel() }
                }
            }

            is Result.Error -> Result.Error(accountResult.error)
        }
    }
}