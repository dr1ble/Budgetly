package shmr.budgetly.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import shmr.budgetly.di.scope.AppScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Планировщик для задач синхронизации.
 */
@AppScope
class SyncScheduler @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Запускает периодическую синхронизацию данных.
     * Если задача уже запланирована, она будет заменена новой.
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS) // Раз в 6 часов
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Оставляем старую, если уже есть
            periodicSyncRequest
        )
    }

    /**
     * Запускает немедленную одноразовую синхронизацию,
     * как только появится подключение к сети.
     */
    fun scheduleImmediateSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateSyncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(immediateSyncRequest)
    }
}