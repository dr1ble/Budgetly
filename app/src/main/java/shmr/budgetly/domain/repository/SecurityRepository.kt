package shmr.budgetly.domain.repository

/**
 * Репозиторий для управления настройками безопасности, такими как пин-код.
 */
interface SecurityRepository {
    /**
     * Сохраняет пин-код.
     * @param pin Пин-код для сохранения.
     */
    suspend fun savePin(pin: String)

    /**
     * Проверяет, совпадает ли предоставленный пин-код с сохраненным.
     * @param pin Пин-код для проверки.
     * @return true, если пин-коды совпадают, иначе false.
     */
    suspend fun checkPin(pin: String): Boolean

    /**
     * Проверяет, установлен ли пин-код в приложении.
     */
    suspend fun isPinSet(): Boolean

    /**
     * Удаляет сохраненный пин-код.
     */
    suspend fun clearPin()
}