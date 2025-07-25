package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.SecurityRepository
import javax.inject.Inject

class CheckPinUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(pin: String): Boolean = securityRepository.checkPin(pin)
}