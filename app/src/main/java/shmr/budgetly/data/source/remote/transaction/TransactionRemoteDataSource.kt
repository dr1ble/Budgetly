package shmr.budgetly.data.source.remote.transaction

import shmr.budgetly.data.network.dto.TransactionRequestDto
import shmr.budgetly.data.network.dto.TransactionResponseDto

interface TransactionRemoteDataSource {
    suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto>

    suspend fun createTransaction(request: TransactionRequestDto): TransactionResponseDto

    suspend fun getTransactionById(id: Int): TransactionResponseDto

    suspend fun updateTransaction(id: Int, request: TransactionRequestDto): TransactionResponseDto

    suspend fun deleteTransaction(id: Int)
}