package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * UseCase для удаления транзакции.
 */
class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Int) = repository.deleteTransaction(id)
}