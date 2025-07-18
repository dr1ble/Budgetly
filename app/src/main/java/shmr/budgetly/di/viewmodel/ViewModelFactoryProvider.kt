package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModelProvider

/**
 * Интерфейс, который должны реализовывать все Dagger-компоненты,
 * предоставляющие ViewModelProvider.Factory.
 * Это упростит создание ViewModel в UI слое.
 */
interface ViewModelFactoryProvider {
    fun viewModelFactory(): ViewModelProvider.Factory
}