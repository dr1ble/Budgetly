package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.TransactionRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase для принудительного обновления транзакций за указанный период.
 */
class RefreshTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate) =
        repository.refreshTransactions(startDate, endDate)
}