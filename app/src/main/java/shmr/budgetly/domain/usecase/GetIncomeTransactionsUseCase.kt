package shmr.budgetly.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    operator fun invoke(): Flow<Result<List<Transaction>>> {
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1)

        return repository.getTransactions(startOfMonth, today).map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.filter { it.category.isIncome })
                is Result.Error -> result
            }
        }
    }
}