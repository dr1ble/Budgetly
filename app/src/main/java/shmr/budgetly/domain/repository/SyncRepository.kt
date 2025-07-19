package shmr.budgetly.domain.repository

/**
 * Репозиторий, отвечающий за синхронизацию локальных данных с сервером.
 */
interface SyncRepository {
    /**
     * Выполняет полную синхронизацию данных.
     * @return true, если синхронизация прошла успешно, иначе false.
     */
    suspend fun syncData(): Boolean
}