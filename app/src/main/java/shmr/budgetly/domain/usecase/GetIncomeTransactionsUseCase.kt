package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения списка транзакций-доходов за текущий месяц.
 * Инкапсулирует бизнес-логику: определение периода (текущий месяц),
 * запрос транзакций и их фильтрация по типу "доход".
 */
class GetIncomeTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(): Result<List<Transaction>> {
        val today = LocalDate.now()

        return when (val result = repository.getTransactions(today, today)) {
            is Result.Success -> Result.Success(result.data.filter { it.category.isIncome })
            is Result.Error -> result
        }
    }
}