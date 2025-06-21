package shmr.budgetly.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import shmr.budgetly.BuildConfig
import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.AccountDto
import shmr.budgetly.data.network.dto.CategoryDto
import shmr.budgetly.data.network.dto.TransactionResponseDto
import shmr.budgetly.data.network.exception.NoConnectivityException
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
import shmr.budgetly.domain.util.DomainError
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import java.time.LocalDateTime
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

        // Если ID уже есть в кэше, возвращаем его
        cachedAccountId?.let { return it }

        // Если ID нет, идем в сеть, получаем первый счет и кэшируем его ID
        val accounts = apiService.getAccounts()
        val firstAccountId = accounts.firstOrNull()?.id
            ?: throw IllegalStateException("No accounts found for the user.")
        cachedAccountId = firstAccountId
        return firstAccountId
    }

    private suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                Result.Success(apiCall.invoke())
            } catch (e: Exception) {
                when (e) {
                    is NoConnectivityException -> Result.Error(DomainError.NoInternet)
                    is HttpException -> {
                        if (e.code() >= 500) {
                            Result.Error(DomainError.ServerError)
                        } else {
                            Result.Error(DomainError.Unknown(e))
                        }
                    }

                    else -> Result.Error(DomainError.Unknown(e))
                }
            }
        }
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

private fun TransactionResponseDto.toDomainModel(): Transaction {
    return Transaction(
        id = this.id,
        category = Category(
            id = this.category.id,
            name = this.category.name,
            emoji = this.category.emoji,
            isIncome = this.category.isIncome
        ),
        amount = "${this.amount} ₽",
        transactionDate = LocalDateTime.parse(
            this.transactionDate,
            DateTimeFormatter.ISO_DATE_TIME
        ),
        comment = this.comment.orEmpty()
    )
}

private fun AccountDto.toDomainModel(): Account {
    return Account(
        id = this.id,
        name = this.name,
        balance = this.balance,
        currency = this.currency
    )
}

private fun CategoryDto.toDomainModel(): Category {
    return Category(
        id = this.id,
        name = this.name,
        emoji = this.emoji,
        isIncome = this.isIncome
    )
}
