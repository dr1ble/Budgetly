package shmr.budgetly.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import shmr.budgetly.domain.repository.SyncRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val isSuccess = syncRepository.syncData()
            if (isSuccess) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "shmr.budgetly.work.SyncWorker"
    }
}