package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для обновления существующей транзакции.
 */
class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        id: Int,
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ) = repository.updateTransaction(
        id,
        accountId,
        categoryId,
        amount,
        transactionDate,
        comment
    )
}