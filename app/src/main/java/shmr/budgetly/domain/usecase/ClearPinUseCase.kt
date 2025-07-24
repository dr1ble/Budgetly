package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.SecurityRepository
import javax.inject.Inject

class ClearPinUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke() = securityRepository.clearPin()
}