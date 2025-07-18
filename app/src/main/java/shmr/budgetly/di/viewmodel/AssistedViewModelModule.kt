package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModel
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * Dagger-модуль, который объявляет мультибиндинг для карты assisted-фабрик.
 * Это позволяет Dagger внедрять Map<Class, Provider<Assisted...Factory>>,
 * даже если она пуста.
 */
@Module
abstract class AssistedViewModelModule {
    @Multibinds
    abstract fun assistedViewModelFactories(): Map<Class<out ViewModel>, AssistedSavedStateViewModelFactory<out ViewModel>>
}