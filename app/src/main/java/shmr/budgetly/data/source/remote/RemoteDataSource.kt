package shmr.budgetly.data.source.remote

import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto

/**
 * Интерфейс для удаленного источника данных.
 * Абстрагирует получение данных с сервера от конкретной реализации (например, Retrofit),
 * позволяя легко заменять или тестировать слой данных.
 */
interface RemoteDataSource {
    /**
     * Получает список DTO транзакций для указанного счета за период.
     */
    suspend fun getTransactionsForPeriod(
        accountId: Int,
        startDate: String,
        endDate: String
    ): List<TransactionResponseDto>

    /**
     * Получает список DTO всех категорий.
     */
    suspend fun getAllCategories(): List<CategoryDto>

    /**
     * Получает DTO счета по его ID.
     */
    suspend fun getAccountById(id: Int): AccountDto

    /**
     * Получает список DTO всех доступных счетов.
     */
    suspend fun getAccounts(): List<AccountDto>
}