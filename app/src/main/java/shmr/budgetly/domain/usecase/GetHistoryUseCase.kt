package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.entity.Transaction
import shmr.budgetly.domain.model.TransactionFilterType
import shmr.budgetly.domain.repository.TransactionRepository
import shmr.budgetly.domain.util.Result
import java.time.LocalDate
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate,
        filterType: TransactionFilterType
    ): Result<List<Transaction>> {
        return when (val result = repository.getTransactions(startDate, endDate)) {
            is Result.Success -> {
                val filtered = when (filterType) {
                    TransactionFilterType.EXPENSE -> result.data.filter { !it.category.isIncome }
                    TransactionFilterType.INCOME -> result.data.filter { it.category.isIncome }
                    TransactionFilterType.ALL -> result.data
                }
                Result.Success(filtered)
            }

            is Result.Error -> result
        }
    }
}