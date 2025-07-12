package shmr.budgetly

import android.app.Application
import shmr.budgetly.di.AppComponent
import shmr.budgetly.di.DaggerAppComponent

/**
 * Основной класс приложения, инициализирующий Dagger компонент.
 */
class BudgetlyApp : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
    }
}