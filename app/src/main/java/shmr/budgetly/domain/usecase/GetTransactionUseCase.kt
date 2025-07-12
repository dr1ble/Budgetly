package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * UseCase для получения одной транзакции по ее ID.
 */
class GetTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(id: Int) = repository.getTransactionById(id)
}