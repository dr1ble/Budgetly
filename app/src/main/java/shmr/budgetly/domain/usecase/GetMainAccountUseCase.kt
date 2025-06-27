package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.AccountRepository
import javax.inject.Inject

class GetMainAccountUseCase @Inject constructor(private val repository: AccountRepository) {
    suspend operator fun invoke() = repository.getMainAccount()
}