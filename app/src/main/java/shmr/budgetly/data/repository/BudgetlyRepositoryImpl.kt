package shmr.budgetly.data.repository

import shmr.budgetly.BuildConfig
import shmr.budgetly.data.mapper.toDomainModel
import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.util.safeApiCall
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetlyRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BudgetlyRepository {

    private var cachedAccountId: Int? = null

    private suspend fun resolveCurrentAccountId(): Int {
        if (BuildConfig.USE_HARDCODED_ACCOUNT_ID) {
            return 1
        }

        cachedAccountId?.let { return it }

        val accounts = apiService.getAccounts()
        val firstAccountId = accounts.firstOrNull()?.id
            ?: throw IllegalStateException("No accounts found for the user.")
        cachedAccountId = firstAccountId
        return firstAccountId
    }

    override suspend fun getHistory(
        startDate: String,
        endDate: String
    ): Result<List<Transaction>> {
        return safeApiCall {
            val accountId = resolveCurrentAccountId()
            val dtos = apiService.getTransactionsForPeriod(accountId, startDate, endDate)
            dtos.map { it.toDomainModel() }
        }
    }

    override suspend fun getExpenseTransactions(): Result<List<Transaction>> {
        return when (val result = getHistoryForCurrentMonth()) {
            is Result.Success -> {
                val expenses = result.data.filter { !it.category.isIncome }
                Result.Success(expenses)
            }
            is Result.Error -> result
        }
    }

    override suspend fun getIncomeTransactions(): Result<List<Transaction>> {
        return when (val result = getHistoryForCurrentMonth()) {
            is Result.Success -> {
                val incomes = result.data.filter { it.category.isIncome }
                Result.Success(incomes)
            }
            is Result.Error -> result
        }
    }

    private suspend fun getHistoryForCurrentMonth(): Result<List<Transaction>> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        return getHistory(startOfMonth.format(formatter), today.format(formatter))
    }

    override suspend fun getMainAccount(): Result<Account> {
        return safeApiCall {
            val accountId = resolveCurrentAccountId()
            val accountDto = apiService.getAccountById(accountId)
            accountDto.toDomainModel()
        }
    }

    override suspend fun getAllCategories(): Result<List<Category>> {
        return safeApiCall {
            val dtos = apiService.getAllCategories()
            dtos.map { it.toDomainModel() }
        }
    }
}