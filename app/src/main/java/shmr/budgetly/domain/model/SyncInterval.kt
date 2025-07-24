package shmr.budgetly.domain.model

import androidx.annotation.StringRes
import shmr.budgetly.R
import java.util.concurrent.TimeUnit

/**
 * Определяет возможные интервалы для периодической синхронизации данных.
 *
 * @param labelRes Ресурс строки для отображения в UI.
 * @param interval Продолжительность интервала.
 * @param timeUnit Единица времени для интервала.
 */
enum class SyncInterval(
    @StringRes val labelRes: Int,
    val interval: Long,
    val timeUnit: TimeUnit
) {
    EVERY_5_MINUTES(R.string.sync_interval_5_minutes, 5, TimeUnit.MINUTES),
    EVERY_30_MINUTES(R.string.sync_interval_30_minutes, 30, TimeUnit.MINUTES),
    EVERY_1_HOUR(R.string.sync_interval_1_hour, 1, TimeUnit.HOURS),
    EVERY_3_HOURS(R.string.sync_interval_3_hours, 3, TimeUnit.HOURS);

    companion object {
        val default = EVERY_30_MINUTES

        fun fromString(name: String?): SyncInterval {
            return entries.find { it.name == name } ?: default
        }
    }
}