package shmr.budgetly.data.source.remote.transaction

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.TransactionResponseDto
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

@AppScope
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