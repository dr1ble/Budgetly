package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.TransactionRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * UseCase для создания новой транзакции.
 */
class CreateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        accountId: Int,
        categoryId: Int,
        amount: String,
        transactionDate: LocalDateTime,
        comment: String
    ) = repository.createTransaction(
        accountId,
        categoryId,
        amount,
        transactionDate,
        comment
    )
}