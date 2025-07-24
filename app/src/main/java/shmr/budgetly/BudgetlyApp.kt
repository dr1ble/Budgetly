package shmr.budgetly

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import shmr.budgetly.di.AppComponent
import shmr.budgetly.di.DaggerAppComponent
import shmr.budgetly.domain.monitor.LocaleDelegate
import shmr.budgetly.domain.monitor.NetworkMonitor
import shmr.budgetly.work.SyncScheduler
import javax.inject.Inject

/**
 * Основной класс приложения, инициализирующий Dagger компонент и WorkManager.
 */
class BudgetlyApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var syncScheduler: SyncScheduler

    @Inject
    lateinit var localeDelegate: LocaleDelegate

    override fun onCreate() {
        super.onCreate()

        // 1. Сначала создаем Dagger компонент. Он нам нужен для получения WorkerFactory.
        appComponent = DaggerAppComponent.factory().create(this)

        // 2. Создаем конфигурацию WorkManager, получая фабрику из компонента.
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(appComponent.workerFactory())
            .build()

        // 3. Инициализируем WorkManager с этой конфигурацией. Теперь он готов к работе.
        WorkManager.initialize(this, workManagerConfig)

        // 4. Только теперь, когда WorkManager готов, внедряем зависимости, которые от него зависят.
        appComponent.inject(this)

        // 5. Запускаем делегат локали и остальные сервисы.
        localeDelegate.applyLocale()
        networkMonitor.start()
        syncScheduler.startObserving()
    }
}