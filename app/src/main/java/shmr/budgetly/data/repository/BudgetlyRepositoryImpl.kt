package shmr.budgetly.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shmr.budgetly.data.MockData
import shmr.budgetly.data.network.ApiService
import shmr.budgetly.data.network.dto.TransactionResponseDto
import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.BudgetlyRepository
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

    override suspend fun getHistory(
        accountId: Int,
        startDate: String,
        endDate: String
    ): Result<List<Transaction>> {
        return withContext(Dispatchers.IO) {
            try {
                val dtos = apiService.getTransactionsForPeriod(accountId, startDate, endDate)
                val domainModels = dtos.map { it.toDomainModel() }
                Result.Success(domainModels)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getExpenseTransactions(): Result<List<Transaction>> {
        // Вызываем наш общий метод, а затем фильтруем результат
        return when (val result = getHistoryForCurrentMonth()) {
            is Result.Success -> {
                // Если история успешно загружена, фильтруем ее, оставляя только расходы
                val expenses = result.data.filter { !it.category.isIncome }
                Result.Success(expenses)
            }

            is Result.Error -> result // Если при загрузке истории произошла ошибка, просто пробрасываем ее дальше
        }
    }

    override suspend fun getIncomeTransactions(): Result<List<Transaction>> {
        return when (val result = getHistoryForCurrentMonth()) {
            is Result.Success -> {
                // Здесь фильтруем, оставляя только доходы
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
        // Вызываем основной метод с параметрами для текущего месяца
        return getHistory(1, startOfMonth.format(formatter), today.format(formatter))
    }

    override fun getMainAccount(): Account = MockData.mainAccount
    override fun getAllCategories(): List<Category> = MockData.allCategories
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
        comment = this.comment.toString()
    )
}