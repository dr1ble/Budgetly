package shmr.budgetly.data.source.remote.transaction

import shmr.budgetly.data.network.dto.TransactionResponseDto

interface TransactionRemoteDataSource {
    suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto>
}