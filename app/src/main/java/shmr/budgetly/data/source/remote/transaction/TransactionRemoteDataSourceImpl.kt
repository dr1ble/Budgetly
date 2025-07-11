package shmr.budgetly.data.source.remote.transaction

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.TransactionRequestDto
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

    override suspend fun createTransaction(request: TransactionRequestDto): TransactionResponseDto =
        apiService.createTransaction(request)

    override suspend fun getTransactionById(id: Int): TransactionResponseDto =
        apiService.getTransactionById(id)

    override suspend fun updateTransaction(
        id: Int,
        request: TransactionRequestDto
    ): TransactionResponseDto =
        apiService.updateTransaction(id, request)

    override suspend fun deleteTransaction(id: Int) = apiService.deleteTransaction(id)
}