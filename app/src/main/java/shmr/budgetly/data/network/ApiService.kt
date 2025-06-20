package shmr.budgetly.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto

interface ApiService {
    @GET("transactions/account/{accountId}/period")
    suspend fun getTransactionsForPeriod(
        @Path("accountId") accountId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionResponseDto>

    @GET("categories")
    suspend fun getAllCategories(): List<CategoryDto>
}