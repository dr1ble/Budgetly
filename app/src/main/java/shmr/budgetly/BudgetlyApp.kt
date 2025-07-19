package shmr.budgetly

import android.app.Application
import shmr.budgetly.di.AppComponent
import shmr.budgetly.di.DaggerAppComponent
import shmr.budgetly.domain.monitor.NetworkMonitor
import javax.inject.Inject

/**
 * Основной класс приложения, инициализирующий Dagger компонент.
 */
class BudgetlyApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)

        networkMonitor.start()
    }
}