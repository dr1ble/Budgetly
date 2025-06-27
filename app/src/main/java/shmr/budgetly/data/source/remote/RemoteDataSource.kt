package shmr.budgetly.data.source.remote

import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto

/**
 * Интерфейс для удаленного источника данных.
 * Абстрагирует получение данных с сервера от конкретной реализации (Retrofit).
 */
interface RemoteDataSource {
    suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto>

    suspend fun getAllCategories(): List<CategoryDto>

    suspend fun getAccountById(id: Int): AccountDto

    suspend fun getAccounts(): List<AccountDto>
}