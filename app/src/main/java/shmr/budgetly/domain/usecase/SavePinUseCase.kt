package shmr.budgetly.domain.usecase

import shmr.budgetly.domain.repository.SecurityRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val securityRepository: SecurityRepository
) {
    suspend operator fun invoke(pin: String) = securityRepository.savePin(pin)
}