package shmr.budgetly.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для получения истории транзакций за заданный период с возможностью фильтрации.
 * Инкапсулирует бизнес-логику запроса транзакций и их последующей фильтрации
 * по типу (доход, расход или все).
 */
class GetHistoryUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    /**
     * @param startDate Начальная дата периода.
     * @param endDate Конечная дата периода.
     * @param filterType Тип транзакций для фильтрации.
     */
    operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        filterType: TransactionFilterType
    ): Flow<Result<List<Transaction>>> {
        return repository.getTransactions(startDate, endDate).map { result ->
            when (result) {
                is Result.Success -> Result.Success(filterTransactions(result.data, filterType))
                is Result.Error -> result
            }
        }
    }

    /**
     * Фильтрует список транзакций в соответствии с заданным [TransactionFilterType].
     */
    private fun filterTransactions(
        transactions: List<Transaction>,
        filterType: TransactionFilterType
    ): List<Transaction> {
        return when (filterType) {
            TransactionFilterType.EXPENSE -> transactions.filter { !it.category.isIncome }
            TransactionFilterType.INCOME -> transactions.filter { it.category.isIncome }
            TransactionFilterType.ALL -> transactions
        }
    }
}