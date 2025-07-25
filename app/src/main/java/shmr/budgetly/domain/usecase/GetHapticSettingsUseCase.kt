package shmr.budgetly.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import shmr.budgetly.domain.model.HapticEffect
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * UseCase для получения текущих настроек тактильной обратной связи.
 */
class GetHapticSettingsUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<HapticSettings> {
        return combine(
            repository.isHapticsEnabled,
            repository.hapticEffect
        ) { isEnabled, effect ->
            HapticSettings(isEnabled = isEnabled, effect = effect)
        }
    }
}

/**
 * Простая модель для передачи настроек как единого объекта.
 */
data class HapticSettings(
    val isEnabled: Boolean,
    val effect: HapticEffect
)
