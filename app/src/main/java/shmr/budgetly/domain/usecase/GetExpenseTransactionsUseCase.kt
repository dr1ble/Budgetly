package shmr.budgetly.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения списка транзакций-расходов за текущий день.
 * Инкапсулирует бизнес-логику: определение периода (текущий день),
 * запрос транзакций из репозитория и их фильтрация по типу "расход".
 * Возвращает Flow, что позволяет UI автоматически обновляться при изменении данных.
 */
class GetExpenseTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    operator fun invoke(): Flow<Result<List<Transaction>>> {
        val today = LocalDate.now()

        return repository.getTransactions(today, today).map { result ->
            when (result) {
                is Result.Success -> {
                    val expenses = result.data.filter { !it.category.isIncome }
                    Result.Success(expenses)
                }

                is Result.Error -> {
                    result
                }
            }
        }
    }
}