package shmr.budgetly.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import shmr.budgetly.di.scope.AppScope

/**
 * Корневой компонент Dagger для всего приложения.
 * Он живет в течение всего жизненного цикла приложения и предоставляет
 * глобальные зависимости (синглтоны), такие как сетевой клиент, репозитории и т.д.
 */
@AppScope
@Component(modules = [AppModule::class, NetworkModule::class, DataModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
}