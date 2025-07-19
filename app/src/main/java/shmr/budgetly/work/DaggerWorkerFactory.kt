package shmr.budgetly.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import shmr.budgetly.domain.repository.SyncRepository
import javax.inject.Inject
import javax.inject.Provider

/**
 * Кастомная фабрика, которая использует Dagger Provider'ы для создания воркеров.
 * Provider'ы позволяют отложить получение зависимостей до момента их реального использования.
 */
class DaggerWorkerFactory @Inject constructor(
    private val syncRepositoryProvider: Provider<SyncRepository>
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name ->
                // Мы вызываем .get() здесь, в момент создания воркера,
                // а не в момент создания самой фабрики.
                SyncWorker(appContext, workerParameters, syncRepositoryProvider.get())

            else -> null
        }
    }
}