package shmr.budgetly.data.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.AccountResponseDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionDto
import shmr.budgetly.data.network.dto.TransactionRequestDto
import shmr.budgetly.data.network.dto.TransactionResponseDto
import shmr.budgetly.data.network.dto.UpdateAccountRequestDto

/**
 * Определяет конечные точки API для взаимодействия с сервером с помощью Retrofit.
 * Каждый метод соответствует конкретному запросу к API.
 */
interface ApiService {
    /**
     * Получает список транзакций для указанного счета за определенный период.
     */
    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsForPeriod(
        @Path("accountId") accountId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionResponseDto>

    /**
     * Получает список всех доступных категорий.
     */
    @GET("categories")
    suspend fun getAllCategories(): List<CategoryDto>

    /**
     * Получает информацию о счете по его идентификатору.
     */
    @GET("accounts/{id}")
    suspend fun getAccountById(@Path("id") id: Int): AccountResponseDto

    /**
     * Получает список всех счетов, доступных пользователю.
     */
    @GET("accounts")
    suspend fun getAccounts(): List<AccountDto>

    /**
     * Обновляет информацию о счете по его идентификатору.
     */
    @PUT("accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: Int,
        @Body request: UpdateAccountRequestDto
    ): AccountDto

    /**
     * Создает новую транзакцию.
     */
    @POST("transactions")
    suspend fun createTransaction(@Body request: TransactionRequestDto): TransactionDto

    /**
     * Получает транзакцию по ее ID.
     */
    @GET("transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: Int): TransactionResponseDto

    /**
     * Обновляет существующую транзакцию.
     */
    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: Int,
        @Body request: TransactionRequestDto
    ): TransactionResponseDto

    /**
     * Удаляет транзакцию по ее ID.
     */
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int)
}