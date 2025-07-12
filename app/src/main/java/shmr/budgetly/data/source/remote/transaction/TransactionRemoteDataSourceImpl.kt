package shmr.budgetly.data.source.remote.transaction

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.TransactionResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : TransactionRemoteDataSource {
    override suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto> =
        apiService.getTransactionsForPeriod(accountId, startDate, endDate)
}