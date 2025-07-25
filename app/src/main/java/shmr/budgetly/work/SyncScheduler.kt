package shmr.budgetly.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.model.SyncInterval
import shmr.budgetly.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Планировщик для задач синхронизации.
 */
@AppScope
class SyncScheduler @Inject constructor(
    private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val workManager = WorkManager.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Запускает наблюдение за изменением интервала синхронизации
     * и планирует периодическую задачу в соответствии с ним.
     */
    fun startObserving() {
        scope.launch {
            userPreferencesRepository.syncInterval
                .distinctUntilChanged()
                .collect { interval ->
                    schedulePeriodicSync(interval)
                }
        }
    }

    /**
     * Запускает периодическую синхронизацию данных с заданным интервалом.
     * Если задача уже запланирована, она будет заменена новой.
     */
    private fun schedulePeriodicSync(interval: SyncInterval) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(interval.interval, interval.timeUnit)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
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