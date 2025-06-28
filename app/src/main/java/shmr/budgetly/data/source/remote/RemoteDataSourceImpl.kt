package shmr.budgetly.data.source.remote

import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [RemoteDataSource] с использованием Retrofit.
 * Отвечает за выполнение конкретных сетевых запросов через [ApiService].
 *
 * @param apiService Retrofit-сервис для взаимодействия с API.
 */
@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteDataSource {

    override suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto> =
        apiService.getTransactionsForPeriod(accountId, startDate, endDate)

    override suspend fun getAllCategories(): List<CategoryDto> =
        apiService.getAllCategories()

    override suspend fun getAccountById(id: Int): AccountDto =
        apiService.getAccountById(id)

    override suspend fun getAccounts(): List<AccountDto> =
        apiService.getAccounts()
}